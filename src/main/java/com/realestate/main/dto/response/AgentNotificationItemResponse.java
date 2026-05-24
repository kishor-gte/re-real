package com.realestate.main.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AgentNotificationItemResponse {

	private String id;
	private String category;
	private String type;
	private String title;
	private String message;
	private String channel;
	private String status;
	private String linkUrl;
	private String createdAt;
	private boolean unread;
	private List<DetailLine> details = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public List<DetailLine> getDetails() {
		return details;
	}

	public void setDetails(List<DetailLine> details) {
		this.details = details;
	}

	public static class DetailLine {
		private String label;
		private String value;

		public DetailLine() {
		}

		public DetailLine(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
