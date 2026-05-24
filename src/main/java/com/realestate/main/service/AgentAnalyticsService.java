package com.realestate.main.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.main.dto.response.AgentAnalyticsResponse;
import com.realestate.main.dto.response.AgentAnalyticsResponse.MonthlyCount;
import com.realestate.main.dto.response.AgentAnalyticsResponse.PropertyViewStat;
import com.realestate.main.dto.response.AgentAnalyticsResponse.StatusCount;
import com.realestate.main.entity.Property;
import com.realestate.main.entity.PropertyEnquiry;
import com.realestate.main.entity.enums.BookingStatus;
import com.realestate.main.entity.enums.EnquiryStatus;
import com.realestate.main.repository.PropertyBookingRepository;
import com.realestate.main.repository.PropertyEnquiryRepository;
import com.realestate.main.repository.PropertyRepository;

@Service
public class AgentAnalyticsService {

	private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy");

	private final PropertyRepository propertyRepository;
	private final PropertyEnquiryRepository enquiryRepository;
	private final PropertyBookingRepository bookingRepository;
	private final AgentBookingService agentBookingService;

	public AgentAnalyticsService(PropertyRepository propertyRepository,
			PropertyEnquiryRepository enquiryRepository, PropertyBookingRepository bookingRepository,
			AgentBookingService agentBookingService) {
		this.propertyRepository = propertyRepository;
		this.enquiryRepository = enquiryRepository;
		this.bookingRepository = bookingRepository;
		this.agentBookingService = agentBookingService;
	}

	@Transactional(readOnly = true)
	public AgentAnalyticsResponse loadAnalytics(Long agentId) {
		AgentAnalyticsResponse r = new AgentAnalyticsResponse();
		List<Property> properties = propertyRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
		List<PropertyEnquiry> enquiries = enquiryRepository.findByAgentIdOrderByCreatedAtDesc(agentId);

		r.setTotalProperties(properties.size());
		r.setTotalViews(propertyRepository.sumViewCountByAgentId(agentId));
		r.setTotalLeads(enquiries.size());
		r.setPendingLeads(enquiries.stream().filter(e -> e.getStatus() == EnquiryStatus.PENDING).count());
		r.setAcceptedLeads(enquiries.stream().filter(e -> e.getStatus() == EnquiryStatus.ACCEPTED).count());

		int totalBookings = (int) bookingRepository.countByAgentIdAndBookingStatusNot(agentId, BookingStatus.CANCELLED);
		int pending = (int) bookingRepository.countByAgentIdAndBookingStatus(agentId, BookingStatus.PENDING);
		r.setTotalBookings(totalBookings);
		r.setPendingBookings(pending);
		r.setCompletedBookings(totalBookings - pending);
		r.setTotalCollected(formatInr(agentBookingService.sumCollectedForAgent(agentId)));
		r.setTotalPending(formatInr(agentBookingService.sumPendingForAgent(agentId)));

		if (enquiries.size() > 0) {
			r.setLeadToBookingRate(Math.round((totalBookings * 1000.0) / enquiries.size()) / 10.0);
		} else {
			r.setLeadToBookingRate(0);
		}

		Map<EnquiryStatus, Long> leadCounts = enquiries.stream()
				.collect(Collectors.groupingBy(PropertyEnquiry::getStatus, Collectors.counting()));
		List<StatusCount> leadsByStatus = new ArrayList<>();
		for (EnquiryStatus st : EnquiryStatus.values()) {
			leadsByStatus.add(new StatusCount(formatEnquiryStatus(st), leadCounts.getOrDefault(st, 0L)));
		}
		r.setLeadsByStatus(leadsByStatus);

		Map<BookingStatus, Long> bookingCounts = bookingRepository.findByAgentIdOrderByBookingDateDesc(agentId)
				.stream()
				.filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
				.collect(Collectors.groupingBy(b -> b.getBookingStatus(), Collectors.counting()));
		List<StatusCount> bookingsByStatus = new ArrayList<>();
		for (BookingStatus st : BookingStatus.values()) {
			if (st == BookingStatus.CANCELLED) {
				continue;
			}
			bookingsByStatus.add(new StatusCount(formatBookingStatus(st), bookingCounts.getOrDefault(st, 0L)));
		}
		r.setBookingsByStatus(bookingsByStatus);

		Map<Long, Long> enquiryPerProperty = enquiries.stream()
				.collect(Collectors.groupingBy(PropertyEnquiry::getPropertyId, Collectors.counting()));
		List<PropertyViewStat> top = properties.stream()
				.sorted(Comparator.comparingInt(Property::getViewCount).reversed())
				.limit(8)
				.map(p -> {
					PropertyViewStat s = new PropertyViewStat();
					s.setPropertyId(p.getId());
					s.setTitle(p.getTitle());
					s.setPropertyCode(p.getPropertyCode());
					s.setViews(p.getViewCount());
					s.setEnquiries(enquiryPerProperty.getOrDefault(p.getId(), 0L).intValue());
					return s;
				})
				.collect(Collectors.toList());
		r.setTopPropertiesByViews(top);

		Map<YearMonth, Long> monthly = new LinkedHashMap<>();
		for (int i = 5; i >= 0; i--) {
			YearMonth ym = YearMonth.now().minusMonths(i);
			monthly.put(ym, 0L);
		}
		for (PropertyEnquiry e : enquiries) {
			if (e.getCreatedAt() == null) {
				continue;
			}
			YearMonth ym = YearMonth.from(e.getCreatedAt());
			if (monthly.containsKey(ym)) {
				monthly.put(ym, monthly.get(ym) + 1);
			}
		}
		List<MonthlyCount> monthlyLeads = monthly.entrySet().stream()
				.map(e -> new MonthlyCount(e.getKey().format(MONTH_FMT), e.getValue()))
				.collect(Collectors.toList());
		r.setMonthlyLeads(monthlyLeads);

		return r;
	}

	private static String formatInr(BigDecimal amount) {
		if (amount == null) {
			return "0";
		}
		double n = amount.doubleValue();
		if (n >= 100000) {
			return String.format("%.2f L", n / 100000);
		}
		return String.format("%,.0f", n);
	}

	private static String formatEnquiryStatus(EnquiryStatus st) {
		return st.name().charAt(0) + st.name().substring(1).toLowerCase().replace('_', ' ');
	}

	private static String formatBookingStatus(BookingStatus st) {
		return st.name().replace('_', ' ');
	}
}
