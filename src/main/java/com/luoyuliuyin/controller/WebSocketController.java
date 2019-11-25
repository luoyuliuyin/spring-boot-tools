package com.luoyuliuyin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jingfeng on 2017/7/11.
 */
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private static Map<Session, Thread> runningMap = new ConcurrentHashMap<>();

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        Thread thread = new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", message);
                Process p = processBuilder.start();

                InputStream is = p.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                logger.info("执行命令开始：{}", message);
                while ((line = br.readLine()) != null) {
                    logger.info(line);
                    session.getBasicRemote().sendText(line);
                }

                is = p.getErrorStream();
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    logger.info(line);
                    session.getBasicRemote().sendText(line);
                }

                if (p.waitFor() != 0) {
                    logger.error("执行命令error！");
                    session.getBasicRemote().sendText("执行命令error！");
                }

                br.close();
                is.close();
            } catch (Exception e) {
                logger.error("执行命令error:", e);
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(e.getMessage());//异步
                }
            } finally {
                logger.info("执行命令结束：{}", message);
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText("执行结束");
                }
            }
        });
        thread.start();
        runningMap.put(session, thread);
    }

    @OnClose
    public void onClose(Session session) {
        threadStop(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        threadStop(session);
    }

    private void threadStop(Session session) {
        Thread thread = runningMap.get(session);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        runningMap.remove(session);
    }
}
