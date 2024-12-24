package ChatApp.example.Real_TimeChatApp.Config;

import ChatApp.example.Real_TimeChatApp.Chat.ChatMessage;
import ChatApp.example.Real_TimeChatApp.Chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEvent {
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void WebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("Người dùng đã ngắt kết nối: {}", username);
            WebSocketLogout(username);
        }
    }

    public void WebSocketLogout(String username) {
        var chatMessage = ChatMessage.builder()
                .type(MessageType.LEAVE)
                .sender(username)
                .content(username + " đã rời khỏi phòng trò chuyện.")
                .build();
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}

