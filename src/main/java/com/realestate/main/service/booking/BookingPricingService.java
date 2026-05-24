package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.realestate.main.config.BookingProperties;
import com.realestate.main.dto.booking.PriceBreakdownDto;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.enums.PaymentPlanType;

@Service
public class BookingPricingService {

	private final BookingProperties bookingProperties;
	private final EmiCalculationService emiCalculationService;

	public BookingPricingService(BookingProperties bookingProperties, EmiCalculationService emiCalculationService) {
		this.bookingProperties = bookingProperties;
		this.emiCalculationService = emiCalculationService;
	}

	public PriceBreakdownDto calculate(Property property, PaymentPlanType planType, Integer emiMonths, String couponCode) {
		BigDecimal base = property.getPrice().setScale(2, RoundingMode.HALF_UP);
		BigDecimal gst = percentOf(base, bookingProperties.getGstPercent());
		BigDecimal registration = percentOf(base, bookingProperties.getRegistrationPercent());
		BigDecimal bookingCharges = bookingProperties.getBookingChargesFlat().setScale(2, RoundingMode.HALF_UP);
		BigDecimal discount = resolveDiscount(couponCode, base);
		BigDecimal total = base.add(gst).add(registration).add(bookingCharges).subtract(discount)
				.setScale(2, RoundingMode.HALF_UP);

		BigDecimal payableNow = switch (planType) {
		case FULL -> total;
		case BOOKING_ADVANCE -> percentOf(total, bookingProperties.getAdvancePercent());
		case EMI -> {
			if (emiMonths == null || emiMonths <= 0) {
				yield percentOf(total, bookingProperties.getEmiDownPaymentPercent());
			}
			yield emiCalculationService.buildPlan(total, emiMonths).getDownPayment();
		}
		};

		PriceBreakdownDto dto = new PriceBreakdownDto();
		dto.setBasePrice(base);
		dto.setGstAmount(gst);
		dto.setRegistrationCharges(registration);
		dto.setBookingCharges(bookingCharges);
		dto.setDiscountAmount(discount);
		dto.setTotalAmount(total);
		dto.setPayableNow(payableNow.setScale(2, RoundingMode.HALF_UP));
		return dto;
	}

	private BigDecimal resolveDiscount(String couponCode, BigDecimal base) {
		if (couponCode == null || couponCode.isBlank()) {
			return BigDecimal.ZERO;
		}
		String code = couponCode.trim().toUpperCase();
		if ("ESTATE10".equals(code)) {
			return percentOf(base, new BigDecimal("10"));
		}
		if ("LUXURY5".equals(code)) {
			return percentOf(base, new BigDecimal("5"));
		}
		return BigDecimal.ZERO;
	}

	private static BigDecimal percentOf(BigDecimal amount, BigDecimal percent) {
		return amount.multiply(percent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
	}
}
