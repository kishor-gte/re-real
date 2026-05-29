package com.realestate.main.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Creates the MySQL database before the DataSource connects (if it does not exist),
 * and removes duplicate/orphan foreign keys that cause Hibernate errno 121 on startup.
 */
@Configuration
public class DatabaseConfig {

	private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

	private static final Set<String> ALLOWED_FOREIGN_KEYS = Set.of(
			"fk_referrals_referrer_id",
			"fk_referrals_referred_user_id",
			"fk_sessions_user_id",
			"fk_login_history_user_id");

	private static final Set<String> FK_TABLES = Set.of("referrals", "sessions", "login_history");

	@Value("${app.database.name:realestate}")
	private String databaseName;

	@Value("${app.database.host:localhost}")
	private String host;

	@Value("${app.database.port:3306}")
	private String port;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	@Primary
	public DataSource dataSource(DataSourceProperties properties) {
		// If DB credentials are not fully configured (common in local/test runs), skip proactive DB creation
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			log.warn("DB credentials incomplete (spring.datasource.username/password). Skipping DB creation and migrations.");
			// Prevent Flyway and Hibernate DDL from running in environments without DB credentials
			System.setProperty("spring.flyway.enabled", "false");
			System.setProperty("spring.jpa.hibernate.ddl-auto", "none");
			System.setProperty("spring.jpa.generate-ddl", "false");
			return properties.initializeDataSourceBuilder().build();
		}

		createDatabaseIfNotExists();
		removeDuplicateForeignKeys();
		migrateUsersPhoneColumn();
		migrateChatRoomsForPgOwner();
		return properties.initializeDataSourceBuilder().build();
	}

	private void createDatabaseIfNotExists() {
		String serverUrl = String.format(
				"jdbc:mysql://%s:%s/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				host, port);

		String sql = "CREATE DATABASE IF NOT EXISTS `" + databaseName
				+ "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";

		try (Connection connection = DriverManager.getConnection(serverUrl, username, password);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate(sql);
			log.info("MySQL database '{}' is ready.", databaseName);
		} catch (Exception ex) {
			log.error("Failed to create database '{}'. Check MySQL is running and credentials are correct.",
					databaseName, ex);
			throw new IllegalStateException("Could not create MySQL database: " + databaseName, ex);
		}
	}

	/**
	 * Drops Hibernate-generated FK names left from earlier runs so ddl-auto=update
	 * can apply stable constraint names without errno 121.
	 */
	private void removeDuplicateForeignKeys() {
		String dbUrl = String.format(
				"jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				host, port, databaseName);

		String findSql = """
				SELECT tc.TABLE_NAME, tc.CONSTRAINT_NAME
				FROM information_schema.TABLE_CONSTRAINTS tc
				WHERE tc.CONSTRAINT_SCHEMA = ?
				  AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
				  AND tc.TABLE_NAME IN ('referrals', 'sessions', 'login_history')
				""";

		try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
			try (PreparedStatement ps = connection.prepareStatement(findSql)) {
				ps.setString(1, databaseName);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String table = rs.getString("TABLE_NAME");
						String constraint = rs.getString("CONSTRAINT_NAME");
						if (!FK_TABLES.contains(table) || ALLOWED_FOREIGN_KEYS.contains(constraint)) {
							continue;
						}
						try (Statement stmt = connection.createStatement()) {
							stmt.executeUpdate(
									"ALTER TABLE `" + table + "` DROP FOREIGN KEY `" + constraint + "`");
							log.debug("Dropped orphan foreign key {} on {}", constraint, table);
						}
					}
				}
			}
		} catch (Exception ex) {
			log.debug("Foreign key cleanup skipped (tables may not exist yet): {}", ex.getMessage());
		}
	}

	/**
	 * Aligns users table: MySQL may have required {@code phone} while Hibernate added {@code mobile}.
	 */
	private void migrateUsersPhoneColumn() {
		String dbUrl = String.format(
				"jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				host, port, databaseName);

		try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
				Statement statement = connection.createStatement()) {
			boolean hasPhone = columnExists(connection, databaseName, "users", "phone");
			boolean hasMobile = columnExists(connection, databaseName, "users", "mobile");

			if (!hasPhone && hasMobile) {
				statement.executeUpdate("ALTER TABLE users CHANGE mobile phone VARCHAR(15) NOT NULL");
				log.info("Renamed users.mobile -> users.phone");
			} else if (hasPhone && hasMobile) {
				statement.executeUpdate(
						"UPDATE users SET phone = mobile WHERE (phone IS NULL OR phone = '') AND mobile IS NOT NULL AND mobile <> ''");
				statement.executeUpdate(
						"UPDATE users SET mobile = phone WHERE (mobile IS NULL OR mobile = '') AND phone IS NOT NULL AND phone <> ''");
				try {
					statement.executeUpdate("ALTER TABLE users DROP COLUMN mobile");
					log.info("Merged users.mobile into phone and dropped mobile column");
				} catch (Exception ex) {
					log.debug("Could not drop users.mobile (may be referenced): {}", ex.getMessage());
				}
			} else if (hasPhone) {
				log.debug("users.phone column present");
			}
		} catch (Exception ex) {
			log.debug("users phone/mobile migration skipped: {}", ex.getMessage());
		}
	}

	/**
	 * PG owner chat rooms use pg_owner_id + pg_booking_id; agent_id must be nullable.
	 * Hibernate ddl-auto=update often adds columns but does not drop NOT NULL on agent_id.
	 */
	private void migrateChatRoomsForPgOwner() {
		String dbUrl = String.format(
				"jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				host, port, databaseName);

		try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
				Statement statement = connection.createStatement()) {
			if (!tableExists(connection, databaseName, "chat_rooms")) {
				return;
			}
			if (columnExists(connection, databaseName, "chat_rooms", "agent_id")
					&& !columnAllowsNull(connection, databaseName, "chat_rooms", "agent_id")) {
				statement.executeUpdate("ALTER TABLE chat_rooms MODIFY COLUMN agent_id BIGINT NULL");
				log.info("chat_rooms.agent_id is now nullable (PG owner rooms)");
			}
			if (!columnExists(connection, databaseName, "chat_rooms", "pg_owner_id")) {
				statement.executeUpdate("ALTER TABLE chat_rooms ADD COLUMN pg_owner_id BIGINT NULL");
				log.info("Added chat_rooms.pg_owner_id");
			}
			if (!columnExists(connection, databaseName, "chat_rooms", "pg_booking_id")) {
				statement.executeUpdate("ALTER TABLE chat_rooms ADD COLUMN pg_booking_id BIGINT NULL");
				log.info("Added chat_rooms.pg_booking_id");
			}
		} catch (Exception ex) {
			log.warn("chat_rooms PG migration skipped: {}", ex.getMessage());
		}
	}

	private boolean tableExists(Connection connection, String schema, String table) {
		String sql = """
				SELECT COUNT(*) FROM information_schema.TABLES
				WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
				""";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, schema);
			ps.setString(2, table);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean columnAllowsNull(Connection connection, String schema, String table, String column) {
		String sql = """
				SELECT IS_NULLABLE FROM information_schema.COLUMNS
				WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?
				""";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, schema);
			ps.setString(2, table);
			ps.setString(3, column);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
			}
		} catch (Exception ex) {
			return true;
		}
	}

	private boolean columnExists(Connection connection, String schema, String table, String column) {
		String sql = """
				SELECT COUNT(*) FROM information_schema.COLUMNS
				WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?
				""";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, schema);
			ps.setString(2, table);
			ps.setString(3, column);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (Exception ex) {
			return false;
		}
	}
}
