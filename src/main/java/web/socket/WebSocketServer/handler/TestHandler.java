package web.socket.WebSocketServer.handler;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestHandler implements WebSocketHandler {
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final static Logger logger = LoggerFactory.getLogger(TestHandler.class);
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("11111111111111111");
        logger.info("connection established, session id={}", session.getId());
        sessionMap.putIfAbsent(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("22222222222222");
        Object payload = message.getPayload();
        logger.info("received message, session id={}, message={}", session.getId(), payload);
        //broadcasting message to all session
        sessionMap.forEach((sessionId, session1) -> {
            try {
                session1.sendMessage(message);
            } catch (IOException e) {
                logger.error("fail to send message to session id={}, error={}",
                        sessionId, e.getMessage());
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("33333333333333");
        logger.error("session transport exception, session id={}, error={}", session.getId(), exception.getMessage());
        sessionMap.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("444444444444444");
        logger.info("connection closed, sesson id={}, close status={}", session.getId(), closeStatus);
        sessionMap.remove(session.getId());

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
