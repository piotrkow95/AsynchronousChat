package Messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

import static Messenger.services.PresenceService.JSESSIONID_TO_STOMPID_MAP;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new UserInterceptor());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .addInterceptors(new MyHandshakeInterceptor())
                .withSockJS();
    }

    private static class MyHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            List<String> cookie = request.getHeaders().get("cookie");
            System.out.println("Received determineUser with cookie " + cookie + " and request " + request.getURI());
            String jsessionid = cookie.stream().filter(s -> s.startsWith("JSESSIONID")).map(s -> s.replaceFirst("JSESSIONID=", "")).findFirst().get();
            String[] uriParts = request.getURI().toString().split("/");
            String stompClientId = uriParts[uriParts.length - 2];
            System.out.println("Adding mapping jsessionid=" + jsessionid + " to stompClientId=" + stompClientId);
            JSESSIONID_TO_STOMPID_MAP.put(jsessionid, stompClientId);
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        }
    }
}