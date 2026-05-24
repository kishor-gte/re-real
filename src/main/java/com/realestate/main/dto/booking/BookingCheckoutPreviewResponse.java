package com.realestate.main.dto.booking;

import java.util.List;

import com.realestate.main.dto.response.PublicPropertyDetailResponse;

public class BookingCheckoutPreviewResponse {

	private PublicPropertyDetailResponse property;
	private boolean available;
	private String availabilityMessage;
	private PriceBreakdownDto priceBreakdown;
	private List<EmiPlanOptionDto> emiPlans;
	private int reservationMinutes;
	private boolean razorpayConfigured;

	public PublicPropertyDetailResponse getProperty() {
		return property;
	}

	public void setProperty(PublicPropertyDetailResponse property) {
		this.property = property;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getAvailabilityMessage() {
		return availabilityMessage;
	}

	public void setAvailabilityMessage(String availabilityMessage) {
		this.availabilityMessage = availabilityMessage;
	}

	public PriceBreakdownDto getPriceBreakdown() {
		return priceBreakdown;
	}

	public void setPriceBreakdown(PriceBreakdownDto priceBreakdown) {
		this.priceBreakdown = priceBreakdown;
	}

	public List<EmiPlanOptionDto> getEmiPlans() {
		return emiPlans;
	}

	public void setEmiPlans(List<EmiPlanOptionDto> emiPlans) {
		this.emiPlans = emiPlans;
	}

	public int getReservationMinutes() {
		return reservationMinutes;
	}

	public void setReservationMinutes(int reservationMinutes) {
		this.reservationMinutes = reservationMinutes;
	}

	public boolean isRazorpayConfigured() {
		return razorpayConfigured;
	}

	public void setRazorpayConfigured(boolean razorpayConfigured) {
		this.razorpayConfigured = razorpayConfigured;
	}
}
