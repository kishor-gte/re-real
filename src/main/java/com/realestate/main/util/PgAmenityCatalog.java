package com.realestate.main.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PgAmenityCatalog {

	private PgAmenityCatalog() {
	}

	public static final List<String> ALL = List.of(
			"wifi", "ro_water", "power_backup", "laundry", "housekeeping", "attached_bathroom", "hot_water",
			"study_table", "wardrobe", "bed_mattress",
			"breakfast", "lunch", "dinner", "refrigerator", "microwave", "common_kitchen",
			"cctv", "security_guard", "biometric_entry", "gated_security",
			"tv", "indoor_games", "gym", "common_lounge",
			"parking", "bike_parking", "near_metro", "college_nearby", "it_park_nearby");

	public static final Set<String> ALLOWED = Set.copyOf(ALL);

	public static Map<String, String> labels() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("wifi", "WiFi");
		map.put("ro_water", "RO Water");
		map.put("power_backup", "Power Backup");
		map.put("laundry", "Laundry");
		map.put("housekeeping", "Housekeeping");
		map.put("attached_bathroom", "Attached Bathroom");
		map.put("hot_water", "Hot Water");
		map.put("study_table", "Study Table");
		map.put("wardrobe", "Wardrobe");
		map.put("bed_mattress", "Bed & Mattress");
		map.put("breakfast", "Breakfast");
		map.put("lunch", "Lunch");
		map.put("dinner", "Dinner");
		map.put("refrigerator", "Refrigerator");
		map.put("microwave", "Microwave");
		map.put("common_kitchen", "Common Kitchen");
		map.put("cctv", "CCTV");
		map.put("security_guard", "Security Guard");
		map.put("biometric_entry", "Biometric Entry");
		map.put("gated_security", "Gated Security");
		map.put("tv", "TV");
		map.put("indoor_games", "Indoor Games");
		map.put("gym", "Gym");
		map.put("common_lounge", "Common Lounge");
		map.put("parking", "Parking");
		map.put("bike_parking", "Bike Parking");
		map.put("near_metro", "Near Metro");
		map.put("college_nearby", "College Nearby");
		map.put("it_park_nearby", "IT Park Nearby");
		return map;
	}
}
