package com.ticketbackend.ticketVendor.Controller;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class TicketWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("Message received from client: " + message.getPayload());
        session.sendMessage(new TextMessage("Ticket update: " + System.currentTimeMillis()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        System.out.println("WebSocket connection established");
        session.sendMessage(new TextMessage("Connected to WebSocket!"));
    }
}
