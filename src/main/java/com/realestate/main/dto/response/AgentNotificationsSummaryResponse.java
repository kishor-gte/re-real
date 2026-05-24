package com.realestate.main.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AgentNotificationsSummaryResponse {

	private long unreadCount;
	private long totalCount;
	private List<AgentNotificationItemResponse> notifications = new ArrayList<>();

	public long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(long unreadCount) {
		this.unreadCount = unreadCount;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<AgentNotificationItemResponse> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<AgentNotificationItemResponse> notifications) {
		this.notifications = notifications;
	}
}
