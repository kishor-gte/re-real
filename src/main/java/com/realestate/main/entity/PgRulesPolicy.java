package com.realestate.main.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_rules_policies")
public class PgRulesPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pg_id", nullable = false, unique = true)
	private Long pgPropertyId;

	@Column(name = "no_smoking")
	private Boolean noSmoking = false;

	@Column(name = "no_alcohol")
	private Boolean noAlcohol = false;

	@Column(name = "visitors_allowed")
	private Boolean visitorsAllowed = false;

	@Column(name = "pets_allowed")
	private Boolean petsAllowed = false;

	@Column(name = "curfew_enabled")
	private Boolean curfewEnabled = false;

	@Column(name = "id_proof_mandatory")
	private Boolean idProofMandatory = false;

	@Column(name = "curfew_timing", length = 32)
	private String curfewTiming;

	@Column(name = "notice_period_days")
	private Integer noticePeriodDays;

	@Column(name = "security_deposit_amount", precision = 12, scale = 2)
	private BigDecimal securityDepositAmount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPgPropertyId() {
		return pgPropertyId;
	}

	public void setPgPropertyId(Long pgPropertyId) {
		this.pgPropertyId = pgPropertyId;
	}

	public Boolean getNoSmoking() {
		return noSmoking;
	}

	public void setNoSmoking(Boolean noSmoking) {
		this.noSmoking = noSmoking;
	}

	public Boolean getNoAlcohol() {
		return noAlcohol;
	}

	public void setNoAlcohol(Boolean noAlcohol) {
		this.noAlcohol = noAlcohol;
	}

	public Boolean getVisitorsAllowed() {
		return visitorsAllowed;
	}

	public void setVisitorsAllowed(Boolean visitorsAllowed) {
		this.visitorsAllowed = visitorsAllowed;
	}

	public Boolean getPetsAllowed() {
		return petsAllowed;
	}

	public void setPetsAllowed(Boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}

	public Boolean getCurfewEnabled() {
		return curfewEnabled;
	}

	public void setCurfewEnabled(Boolean curfewEnabled) {
		this.curfewEnabled = curfewEnabled;
	}

	public Boolean getIdProofMandatory() {
		return idProofMandatory;
	}

	public void setIdProofMandatory(Boolean idProofMandatory) {
		this.idProofMandatory = idProofMandatory;
	}

	public String getCurfewTiming() {
		return curfewTiming;
	}

	public void setCurfewTiming(String curfewTiming) {
		this.curfewTiming = curfewTiming;
	}

	public Integer getNoticePeriodDays() {
		return noticePeriodDays;
	}

	public void setNoticePeriodDays(Integer noticePeriodDays) {
		this.noticePeriodDays = noticePeriodDays;
	}

	public BigDecimal getSecurityDepositAmount() {
		return securityDepositAmount;
	}

	public void setSecurityDepositAmount(BigDecimal securityDepositAmount) {
		this.securityDepositAmount = securityDepositAmount;
	}
}
