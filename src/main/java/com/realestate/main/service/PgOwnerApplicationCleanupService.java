package com.realestate.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.entity.PgOwner;
import com.realestate.main.repository.PgOwnerLoginHistoryRepository;
import com.realestate.main.repository.PgOwnerPasswordResetTokenRepository;
import com.realestate.main.repository.PgOwnerRepository;
import com.realestate.main.repository.PgOwnerSessionRepository;

@Service
public class PgOwnerApplicationCleanupService {

	private final PgOwnerRepository pgOwnerRepository;
	private final PgOwnerLoginHistoryRepository loginHistoryRepository;
	private final PgOwnerSessionRepository sessionRepository;
	private final PgOwnerPasswordResetTokenRepository passwordResetTokenRepository;

	public PgOwnerApplicationCleanupService(PgOwnerRepository pgOwnerRepository,
			PgOwnerLoginHistoryRepository loginHistoryRepository, PgOwnerSessionRepository sessionRepository,
			PgOwnerPasswordResetTokenRepository passwordResetTokenRepository) {
		this.pgOwnerRepository = pgOwnerRepository;
		this.loginHistoryRepository = loginHistoryRepository;
		this.sessionRepository = sessionRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
	}

	@Transactional
	public void removeIncompleteApplication(PgOwner owner) {
		if (owner == null || owner.getId() == null) {
			return;
		}
		Long pgOwnerId = owner.getId();
		loginHistoryRepository.deleteByPgOwnerId(pgOwnerId);
		sessionRepository.deleteByPgOwnerId(pgOwnerId);
		passwordResetTokenRepository.deleteByPgOwnerId(pgOwnerId);
		pgOwnerRepository.delete(owner);
		pgOwnerRepository.flush();
	}
}
