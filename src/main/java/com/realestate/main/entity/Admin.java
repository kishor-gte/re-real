package com.realestate.main.entity;

import java.time.LocalDateTime;

import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.entity.enums.AdminDepartment;
import com.realestate.main.entity.enums.AdminRoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "full_name", nullable = false, length = 50)
	private String fullName;

	@Column(name = "official_email", nullable = false, unique = true, length = 150)
	private String officialEmail;

	@Column(nullable = false, unique = true, length = 15)
	private String mobile;

	@Column(name = "employee_id", nullable = false, unique = true, length = 20)
	private String employeeId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AdminDepartment department;

	@Enumerated(EnumType.STRING)
	@Column(name = "admin_role", nullable = false, length = 30)
	private AdminRoleType adminRole;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "security_question", nullable = false, length = 255)
	private String securityQuestion;

	@Column(name = "security_answer_hash", nullable = false, length = 255)
	private String securityAnswerHash;

	@Column(name = "profile_image", length = 500)
	private String profileImage;

	@Column(name = "is_verified", nullable = false)
	private boolean verified = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status", nullable = false, length = 20)
	private AccountStatus accountStatus = AccountStatus.PENDING;

	@Column(name = "failed_login_attempts", nullable = false)
	private int failedLoginAttempts = 0;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Admin() {
	}

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOfficialEmail() {
		return officialEmail;
	}

	public void setOfficialEmail(String officialEmail) {
		this.officialEmail = officialEmail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public AdminDepartment getDepartment() {
		return department;
	}

	public void setDepartment(AdminDepartment department) {
		this.department = department;
	}

	public AdminRoleType getAdminRole() {
		return adminRole;
	}

	public void setAdminRole(AdminRoleType adminRole) {
		this.adminRole = adminRole;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswerHash() {
		return securityAnswerHash;
	}

	public void setSecurityAnswerHash(String securityAnswerHash) {
		this.securityAnswerHash = securityAnswerHash;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
