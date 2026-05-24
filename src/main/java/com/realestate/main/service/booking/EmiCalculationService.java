package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.realestate.main.config.BookingProperties;
import com.realestate.main.dto.booking.EmiPlanOptionDto;
import com.realestate.main.dto.booking.EmiScheduleItemDto;

@Service
public class EmiCalculationService {

	private static final int[] EMI_MONTH_OPTIONS = { 3, 6, 12, 24, 36 };

	private final BookingProperties bookingProperties;

	public EmiCalculationService(BookingProperties bookingProperties) {
		this.bookingProperties = bookingProperties;
	}

	public List<EmiPlanOptionDto> buildAllPlans(BigDecimal totalAmount) {
		List<EmiPlanOptionDto> plans = new ArrayList<>();
		for (int months : EMI_MONTH_OPTIONS) {
			plans.add(buildPlan(totalAmount, months));
		}
		return plans;
	}

	public EmiPlanOptionDto buildPlan(BigDecimal totalAmount, int months) {
		BigDecimal downPayment = totalAmount.multiply(bookingProperties.getEmiDownPaymentPercent())
				.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		BigDecimal principal = totalAmount.subtract(downPayment).setScale(2, RoundingMode.HALF_UP);
		BigDecimal annualRate = bookingProperties.getEmiAnnualInterest();
		BigDecimal monthlyRate = annualRate.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);

		BigDecimal monthlyEmi;
		BigDecimal totalInterest;
		if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
			monthlyEmi = principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
			totalInterest = BigDecimal.ZERO;
		} else {
			BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate).pow(months, MathContext.DECIMAL64);
			monthlyEmi = principal.multiply(monthlyRate).multiply(onePlusRPowerN)
					.divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
			totalInterest = monthlyEmi.multiply(new BigDecimal(months)).subtract(principal).setScale(2,
					RoundingMode.HALF_UP);
		}

		List<EmiScheduleItemDto> schedule = new ArrayList<>();
		LocalDate due = LocalDate.now().plusMonths(1);
		for (int i = 1; i <= months; i++) {
			EmiScheduleItemDto item = new EmiScheduleItemDto();
			item.setInstallmentNumber(i);
			item.setDueDate(due);
			item.setAmount(monthlyEmi);
			schedule.add(item);
			due = due.plusMonths(1);
		}

		EmiPlanOptionDto plan = new EmiPlanOptionDto();
		plan.setMonths(months);
		plan.setDownPayment(downPayment);
		plan.setPrincipalAmount(principal);
		plan.setMonthlyEmi(monthlyEmi);
		plan.setTotalInterest(totalInterest);
		plan.setPayableNow(downPayment);
		plan.setSchedule(schedule);
		return plan;
	}
}
