package com.realestate.main.dto.pgbooking;

import java.time.LocalDate;

public class PgVacateNoticeResponse {

	private LocalDate plannedVacateDate;
	private int noticePeriodDays;
	private String message;

	public LocalDate getPlannedVacateDate() {
		return plannedVacateDate;
	}

	public void setPlannedVacateDate(LocalDate plannedVacateDate) {
		this.plannedVacateDate = plannedVacateDate;
	}

	public int getNoticePeriodDays() {
		return noticePeriodDays;
	}

	public void setNoticePeriodDays(int noticePeriodDays) {
		this.noticePeriodDays = noticePeriodDays;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
