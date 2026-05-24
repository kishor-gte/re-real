package com.realestate.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.request.AdminAgentRejectRequest;
import com.realestate.main.dto.response.PgOwnerDetailResponse;
import com.realestate.main.dto.response.PgOwnerListItemResponse;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.enums.AccountStatus;
import com.realestate.main.exception.AuthException;
import com.realestate.main.repository.PgOwnerRepository;

@Service
public class AdminPgOwnerService {

	private final PgOwnerRepository pgOwnerRepository;
	private final EmailService emailService;

	public AdminPgOwnerService(PgOwnerRepository pgOwnerRepository, EmailService emailService) {
		this.pgOwnerRepository = pgOwnerRepository;
		this.emailService = emailService;
	}

	@Transactional(readOnly = true)
	public List<PgOwnerListItemResponse> listPgOwners(String statusFilter) {
		List<PgOwner> owners;
		if (statusFilter == null || statusFilter.isBlank() || "ALL".equalsIgnoreCase(statusFilter)) {
			owners = pgOwnerRepository.findAllByOrderByCreatedAtDesc();
		} else {
			AccountStatus status = parseStatus(statusFilter);
			owners = pgOwnerRepository.findByAccountStatusOrderByCreatedAtDesc(status);
		}
		return owners.stream().map(this::toListItem).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PgOwnerDetailResponse getPgOwnerDetail(Long pgOwnerId) {
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner not found"));
		return toDetail(owner);
	}

	@Transactional(readOnly = true)
	public long countPendingApproval() {
		return pgOwnerRepository.countByAccountStatus(AccountStatus.PENDING);
	}

	@Transactional
	public PgOwnerDetailResponse approvePgOwner(Long pgOwnerId, Long adminId) {
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner not found"));
		if (owner.getAccountStatus() != AccountStatus.PENDING) {
			throw new AuthException("Only PG owners awaiting approval can be approved");
		}
		if (!owner.isVerified()) {
			throw new AuthException("PG owner email is not verified yet");
		}

		owner.setAccountStatus(AccountStatus.ACTIVE);
		owner.setApprovedAt(LocalDateTime.now());
		owner.setApprovedByAdminId(adminId);
		owner.setRejectionReason(null);
		owner.setFailedLoginAttempts(0);
		pgOwnerRepository.save(owner);

		emailService.sendPgOwnerApprovedEmail(owner.getEmail(), owner.getFullName(), owner.getPgOwnerCode());
		return toDetail(owner);
	}

	@Transactional
	public PgOwnerDetailResponse rejectPgOwner(Long pgOwnerId, Long adminId, AdminAgentRejectRequest request) {
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner not found"));
		if (owner.getAccountStatus() != AccountStatus.PENDING) {
			throw new AuthException("Only PG owners awaiting approval can be rejected");
		}

		owner.setAccountStatus(AccountStatus.DISABLED);
		owner.setRejectionReason(request.getReason().trim());
		owner.setApprovedByAdminId(adminId);
		owner.setApprovedAt(LocalDateTime.now());
		pgOwnerRepository.save(owner);

		emailService.sendPgOwnerRejectedEmail(owner.getEmail(), owner.getFullName(), request.getReason().trim());
		return toDetail(owner);
	}

	@Transactional
	public PgOwnerDetailResponse suspendPgOwner(Long pgOwnerId) {
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner not found"));
		if (owner.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new AuthException("Only active PG owners can be suspended");
		}
		owner.setAccountStatus(AccountStatus.DISABLED);
		pgOwnerRepository.save(owner);
		return toDetail(owner);
	}

	@Transactional
	public PgOwnerDetailResponse reactivatePgOwner(Long pgOwnerId, Long adminId) {
		PgOwner owner = pgOwnerRepository.findById(pgOwnerId)
				.orElseThrow(() -> new AuthException("PG owner not found"));
		if (owner.getAccountStatus() != AccountStatus.DISABLED) {
			throw new AuthException("Only disabled PG owners can be reactivated");
		}
		owner.setAccountStatus(AccountStatus.ACTIVE);
		owner.setApprovedAt(LocalDateTime.now());
		owner.setApprovedByAdminId(adminId);
		owner.setRejectionReason(null);
		owner.setFailedLoginAttempts(0);
		pgOwnerRepository.save(owner);
		emailService.sendPgOwnerApprovedEmail(owner.getEmail(), owner.getFullName(), owner.getPgOwnerCode());
		return toDetail(owner);
	}

	private AccountStatus parseStatus(String filter) {
		try {
			return AccountStatus.valueOf(filter.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException("Invalid status filter");
		}
	}

	private PgOwnerListItemResponse toListItem(PgOwner owner) {
		PgOwnerListItemResponse item = new PgOwnerListItemResponse();
		item.setId(owner.getId());
		item.setPgOwnerCode(owner.getPgOwnerCode());
		item.setFullName(owner.getFullName());
		item.setEmail(owner.getEmail());
		item.setMobile(owner.getMobile());
		item.setPgName(owner.getPgName());
		item.setPgType(owner.getPgType());
		item.setGenderAllowed(owner.getGenderAllowed());
		item.setCity(owner.getCity());
		item.setState(owner.getState());
		item.setAccountStatus(owner.getAccountStatus());
		item.setVerified(owner.isVerified());
		item.setCreatedAt(owner.getCreatedAt());
		return item;
	}

	private PgOwnerDetailResponse toDetail(PgOwner owner) {
		PgOwnerDetailResponse detail = new PgOwnerDetailResponse();
		PgOwnerListItemResponse base = toListItem(owner);
		detail.setId(base.getId());
		detail.setPgOwnerCode(base.getPgOwnerCode());
		detail.setFullName(base.getFullName());
		detail.setEmail(base.getEmail());
		detail.setMobile(base.getMobile());
		detail.setPgName(base.getPgName());
		detail.setPgType(base.getPgType());
		detail.setGenderAllowed(base.getGenderAllowed());
		detail.setCity(base.getCity());
		detail.setState(base.getState());
		detail.setAccountStatus(base.getAccountStatus());
		detail.setVerified(base.isVerified());
		detail.setCreatedAt(base.getCreatedAt());
		detail.setExperience(owner.getExperience());
		detail.setBusinessRegistrationNumber(owner.getBusinessRegistrationNumber());
		detail.setOfficeAddress(owner.getOfficeAddress());
		detail.setPincode(owner.getPincode());
		detail.setProfilePhoto(owner.getProfilePhoto());
		detail.setPgImages(owner.getPgImages());
		detail.setGovernmentId(owner.getGovernmentId());
		detail.setReferralCodeUsed(owner.getReferralCodeUsed());
		detail.setApprovedAt(owner.getApprovedAt());
		detail.setApprovedByAdminId(owner.getApprovedByAdminId());
		detail.setRejectionReason(owner.getRejectionReason());
		detail.setLastLogin(owner.getLastLogin());
		detail.setUpdatedAt(owner.getUpdatedAt());
		return detail;
	}
}
