package com.realestate.main.rtc.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatUnreadSummaryDto {

	private long total;
	private Map<String, Long> enquiries = new LinkedHashMap<>();
	private Map<String, Long> bookings = new LinkedHashMap<>();
	private Map<String, Long> pgBookings = new LinkedHashMap<>();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Map<String, Long> getEnquiries() {
		return enquiries;
	}

	public void setEnquiries(Map<String, Long> enquiries) {
		this.enquiries = enquiries;
	}

	public Map<String, Long> getBookings() {
		return bookings;
	}

	public void setBookings(Map<String, Long> bookings) {
		this.bookings = bookings;
	}

	public Map<String, Long> getPgBookings() {
		return pgBookings;
	}

	public void setPgBookings(Map<String, Long> pgBookings) {
		this.pgBookings = pgBookings;
	}
}
