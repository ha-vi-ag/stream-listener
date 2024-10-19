package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.net.URI;

@Service
public class DataStreamListener {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(65536);  // Set the text buffer size to 64KB
        container.setMaxBinaryMessageBufferSize(65536); // Set the binary buffer size to 64KB
        container.setAsyncSendTimeout(5000L); // Optional: set async send timeout
        container.setMaxSessionIdleTimeout(500000L); // Optional: set session timeout
        return container;
    }

    private final WebSocketClient webSocketClient;

    public DataStreamListener() {
        this.webSocketClient = new StandardWebSocketClient();
    }

    @PostConstruct
    public void connect() {
        String uri = "wss://bintu-h5live-secure.nanocosmos.de/h5live/authstream/stream.mp4?url=rtmp%3A%2F%2Fbintu-splay.nanocosmos.de%3A1935%2Fsplay&stream=Av7gf-aBTPF&cid=588891&pid=36404592467&flags=faststart&token=b6042db769654168d58bc23acb789046%2B9545cb18f4b1931453307c2200f8d648&expires=1729388759&options=13&tag=Demo";

        System.out.println("Connecting to: " + uri);

        WebSocketHandler webSocketHandler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                System.out.println("Connected to WebSocket!");
                // Handle successful connection
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                if (message instanceof BinaryMessage) {
                    byte[] binaryData = ((BinaryMessage) message).getPayload().array();
                    System.out.println("Received binary data of length: " + binaryData.length);
                    // Process binary data as needed
                } else {
                    System.out.println("Received unexpected message type: " + message);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                System.err.println("Transport Error: " + exception.getMessage());
                exception.printStackTrace(); // Log stack trace for more details
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                System.out.println("Connection closed: " + closeStatus);
            }

            @Override
            public boolean supportsPartialMessages() {
                return false; // Adjust based on your requirements
            }
        };

        // Connect to the WebSocket server
        try {
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.set("Origin", "https://on777bet.co");
            webSocketClient.doHandshake(webSocketHandler, headers, URI.create(uri));
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
