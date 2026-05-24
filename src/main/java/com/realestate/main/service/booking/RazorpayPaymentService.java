package com.realestate.main.service.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.realestate.main.config.RazorpayProperties;
import com.realestate.main.exception.AuthException;

@Service
public class RazorpayPaymentService {

	private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentService.class);

	private final RazorpayProperties razorpayProperties;

	public RazorpayPaymentService(RazorpayProperties razorpayProperties) {
		this.razorpayProperties = razorpayProperties;
	}

	public boolean isConfigured() {
		return razorpayProperties.isConfigured();
	}

	public String getKeyId() {
		return razorpayProperties.getKeyId();
	}

	public String createOrder(BigDecimal amountInr, String receipt) {
		if (!isConfigured()) {
			return "demo_order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
		}
		long paise = toPaise(amountInr);
		if (paise < 100) {
			throw new AuthException("Minimum payment amount is ₹1");
		}
		try {
			RazorpayClient client = new RazorpayClient(razorpayProperties.getKeyId(), razorpayProperties.getKeySecret());
			JSONObject options = new JSONObject();
			options.put("amount", paise);
			options.put("currency", razorpayProperties.getCurrency());
			options.put("receipt", sanitizeReceipt(receipt));
			options.put("payment_capture", 1);
			Order order = client.orders.create(options);
			String orderId = extractOrderId(order);
			if (orderId == null || orderId.isBlank()) {
				throw new AuthException("Razorpay did not return an order ID");
			}
			log.info("Razorpay test order created: {} amount={} paise receipt={}", orderId, paise, receipt);
			return orderId;
		} catch (RazorpayException e) {
			log.error("Razorpay order creation failed", e);
			throw new AuthException("Payment gateway error: " + e.getMessage());
		}
	}

	public void verifySignature(String orderId, String paymentId, String signature) {
		if (!isConfigured()) {
			return;
		}
		try {
			JSONObject attributes = new JSONObject();
			attributes.put("razorpay_order_id", orderId);
			attributes.put("razorpay_payment_id", paymentId);
			attributes.put("razorpay_signature", signature);
			if (!Utils.verifyPaymentSignature(attributes, razorpayProperties.getKeySecret())) {
				throw new AuthException("Invalid payment signature");
			}
		} catch (RazorpayException e) {
			throw new AuthException("Payment verification failed");
		}
	}

	public static long toPaise(BigDecimal amountInr) {
		return amountInr.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValue();
	}

	private static String extractOrderId(Order order) {
		if (order == null) {
			return null;
		}
		Object id = order.get("id");
		return id != null ? id.toString() : null;
	}

	private static String sanitizeReceipt(String receipt) {
		if (receipt == null || receipt.isBlank()) {
			return "bk_" + System.currentTimeMillis();
		}
		String r = receipt.replaceAll("[^a-zA-Z0-9_-]", "");
		return r.length() > 40 ? r.substring(0, 40) : r;
	}
}
