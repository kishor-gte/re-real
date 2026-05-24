package com.realestate.main.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.realestate.main.entity.SubscriptionPlan;
import com.realestate.main.entity.enums.SubscriptionPlanStatus;
import com.realestate.main.repository.SubscriptionPlanRepository;

@Component
@Order(20)
public class SubscriptionPlanSeeder implements CommandLineRunner {

	private final SubscriptionPlanRepository planRepository;

	public SubscriptionPlanSeeder(SubscriptionPlanRepository planRepository) {
		this.planRepository = planRepository;
	}

	@Override
	public void run(String... args) {
		seed("FREE", "Free Plan", BigDecimal.ZERO, 365, 2, 0, 3, false, 5, false, false,
				"2 property listings, basic visibility, limited leads.", false, 0);
		seed("STARTER", "Starter Plan", new BigDecimal("499"), 30, 10, 2, 8, false, 20, true, false,
				"10 listings, featured listings, 20 leads access.", false, 1);
		seed("PRO", "Pro Plan", new BigDecimal("999"), 30, -1, 5, 15, true, -1, true, true,
				"Unlimited listings, premium visibility, unlimited leads, analytics.", true, 2);
		seed("AGENCY", "Agency Plan", new BigDecimal("2499"), 30, -1, 10, 25, true, -1, true, true,
				"Unlimited listings, team access, CRM, priority support, featured properties.", false, 3);
	}

	private void seed(String code, String name, BigDecimal price, int days, int maxProps, int featured, int images,
			boolean premium, int leads, boolean whatsapp, boolean analytics, String desc, boolean recommended,
			int sort) {
		if (planRepository.findByPlanCode(code).isPresent()) {
			return;
		}
		SubscriptionPlan plan = new SubscriptionPlan();
		plan.setPlanCode(code);
		plan.setPlanName(name);
		plan.setPrice(price);
		plan.setDurationDays(days);
		plan.setMaxProperties(maxProps);
		plan.setFeaturedListings(featured);
		plan.setPropertyImagesLimit(images);
		plan.setPremiumBadge(premium);
		plan.setLeadsLimit(leads);
		plan.setWhatsappLeadAccess(whatsapp);
		plan.setAnalyticsAccess(analytics);
		plan.setDescription(desc);
		plan.setRecommended(recommended);
		plan.setSortOrder(sort);
		plan.setStatus(SubscriptionPlanStatus.ACTIVE);
		planRepository.save(plan);
	}
}
