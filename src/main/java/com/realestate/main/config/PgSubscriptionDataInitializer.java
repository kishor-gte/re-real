package com.realestate.main.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.realestate.main.entity.PgSubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.repository.PgSubscriptionPlanRepository;

@Component
@Order(20)
public class PgSubscriptionDataInitializer implements CommandLineRunner {

	private final PgSubscriptionPlanRepository planRepository;

	public PgSubscriptionDataInitializer(PgSubscriptionPlanRepository planRepository) {
		this.planRepository = planRepository;
	}

	@Override
	public void run(String... args) {
		seedIfMissing("PG_FREE", "Free Plan", BigDecimal.ZERO, 365, 1, 10, 0, false, false, false, false,
				"1 PG listing, basic visibility, limited inquiries.", 0, false);
		seedIfMissing("PG_STARTER", "Starter Plan", new BigDecimal("499"), 30, 5, 50, 1, true, false, false, false,
				"5 PG listings, 50 rooms, featured PG, inquiry access.", 1, false);
		seedIfMissing("PG_PRO", "Pro Plan", new BigDecimal("999"), 30, -1, -1, 3, true, true, true, false,
				"Unlimited PGs & rooms, premium visibility, analytics, unlimited inquiries.", 2, true);
		seedIfMissing("PG_BUSINESS", "Business Plan", new BigDecimal("2499"), 30, -1, -1, 10, true, true, true, true,
				"Unlimited listings, team access, CRM dashboard, priority support.", 3, false);
	}

	private void seedIfMissing(String code, String name, BigDecimal price, int days, int maxPg, int maxRooms,
			int featured, boolean bedMgmt, boolean analytics, boolean premium, boolean priority, String desc,
			int sortOrder, boolean recommended) {
		if (planRepository.findByPlanCode(code).isPresent()) {
			return;
		}
		PgSubscriptionPlan plan = new PgSubscriptionPlan();
		plan.setPlanCode(code);
		plan.setPlanName(name);
		plan.setPrice(price);
		plan.setDurationDays(days);
		plan.setMaxPgListings(maxPg);
		plan.setMaxRoomListings(maxRooms);
		plan.setFeaturedPgCount(featured);
		plan.setBedManagementAccess(bedMgmt);
		plan.setTenantAnalyticsAccess(analytics);
		plan.setPremiumBadge(premium);
		plan.setPrioritySupport(priority);
		plan.setDescription(desc);
		plan.setSortOrder(sortOrder);
		plan.setRecommended(recommended);
		plan.setStatus(SubscriptionPlanStatus.ACTIVE);
		planRepository.save(plan);
	}
}
