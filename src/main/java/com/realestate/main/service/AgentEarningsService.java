package com.realestate.main.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AgentEarningsResponse;
import com.realestate.main.dto.response.AgentEarningsResponse.EarningsRow;
import com.realestate.main.dto.response.AgentEarningsResponse.MonthlyEarnings;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyBooking;
import com.realestate.main.entity.User;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyRepository;
import com.realestate.main.repository.UserRepository;

@Service
public class AgentEarningsService {

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy");

	private final PropertyBookingRepository bookingRepository;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final AgentBookingService agentBookingService;

	public AgentEarningsService(PropertyBookingRepository bookingRepository, PropertyRepository propertyRepository,
			UserRepository userRepository, AgentBookingService agentBookingService) {
		this.bookingRepository = bookingRepository;
		this.propertyRepository = propertyRepository;
		this.userRepository = userRepository;
		this.agentBookingService = agentBookingService;
	}

	@Transactional(readOnly = true)
	public AgentEarningsResponse loadEarnings(Long agentId) {
		List<PropertyBooking> bookings = bookingRepository.findByAgentIdOrderByBookingDateDesc(agentId).stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
				.collect(Collectors.toList());

		AgentEarningsResponse r = new AgentEarningsResponse();
		r.setTotalCollected(agentBookingService.sumCollectedForAgent(agentId));
		r.setTotalPending(agentBookingService.sumPendingForAgent(agentId));
		r.setBookingCount(bookings.size());

		BigDecimal totalValue = bookings.stream()
				.map(PropertyBooking::getTotalAmount)
				.filter(a -> a != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		r.setTotalBookingValue(totalValue);

		List<EarningsRow> rows = new ArrayList<>();
		Map<YearMonth, BigDecimal> monthly = new LinkedHashMap<>();
		for (int i = 5; i >= 0; i--) {
			monthly.put(YearMonth.now().minusMonths(i), BigDecimal.ZERO);
		}

		for (PropertyBooking b : bookings) {
			Property property = propertyRepository.findById(b.getPropertyId()).orElse(null);
			User user = userRepository.findById(b.getUserId()).orElse(null);

			EarningsRow row = new EarningsRow();
			row.setBookingId(b.getId());
			row.setBookingCode(b.getBookingCode());
			row.setPropertyTitle(property != null ? property.getTitle() : "Property");
			row.setBuyerName(user != null ? user.getFullName() : "Buyer");
			row.setStatusLabel(b.getBookingStatus().name().replace('_', ' '));
			row.setTotalAmount(b.getTotalAmount());
			row.setPaidAmount(b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO);
			row.setPendingAmount(b.getRemainingAmount() != null ? b.getRemainingAmount() : BigDecimal.ZERO);
			if (b.getBookingDate() != null) {
				row.setBookingDate(b.getBookingDate().format(DATE_FMT));
				YearMonth ym = YearMonth.from(b.getBookingDate());
				if (monthly.containsKey(ym) && row.getPaidAmount() != null) {
					monthly.put(ym, monthly.get(ym).add(row.getPaidAmount()));
				}
			}
			rows.add(row);
		}
		r.setRows(rows);
		r.setMonthlyBreakdown(monthly.entrySet().stream()
				.map(e -> new MonthlyEarnings(e.getKey().format(MONTH_FMT), e.getValue()))
				.collect(Collectors.toList()));
		return r;
	}
}
