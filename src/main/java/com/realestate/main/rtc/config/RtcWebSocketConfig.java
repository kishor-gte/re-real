package com.realestate.main.rtc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class RtcWebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final RtcStompChannelInterceptor channelInterceptor;
	private final RtcHandshakeInterceptor handshakeInterceptor;

	public RtcWebSocketConfig(@Lazy RtcStompChannelInterceptor channelInterceptor,
			RtcHandshakeInterceptor handshakeInterceptor) {
		this.channelInterceptor = channelInterceptor;
		this.handshakeInterceptor = handshakeInterceptor;
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/rtc")
				.setAllowedOriginPatterns("*")
				.addInterceptors(handshakeInterceptor)
				.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(channelInterceptor);
	}
}
