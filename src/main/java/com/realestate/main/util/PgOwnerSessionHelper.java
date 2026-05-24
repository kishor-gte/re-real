package com.realestate.main.util;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;

import jakarta.servlet.http.HttpSession;

public final class PgOwnerSessionHelper {

	private PgOwnerSessionHelper() {
	}

	public static Long requirePgOwnerId(HttpSession session) {
		Long pgOwnerId = (Long) session.getAttribute(PgOwnerSessionConstants.PG_OWNER_ID);
		if (pgOwnerId == null) {
			throw new AuthException("Please sign in as a PG owner");
		}
		return pgOwnerId;
	}

	public static PgOwner requireActivePgOwner(HttpSession session, PgOwnerRepository pgOwnerRepository) {
		Long pgOwnerId = requirePgOwnerId(session);
		PgOwner pgOwner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner account not found"));
		if (pgOwner.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AuthException("Your PG owner account is not active");
		}
		return pgOwner;
	}
}
