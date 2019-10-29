package com.luoyuliuyin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by jingfeng on 2017/7/11.
 */
@Component
@ServerEndpoint(value = "/websocket")
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> paras = session.getRequestParameterMap();
        String command = paras.get("command").get(0);

        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(new String[]{"/bin/sh", "-c", command});

            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            logger.info("执行命令开始：{}", command);
            while ((line = br.readLine()) != null) {
                logger.info(line);
                session.getAsyncRemote().sendText(line);
            }

            while (p.isAlive()) {
                Thread.sleep(100L);
            }

            if (p.exitValue() != 0) {
                is = p.getErrorStream();
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    logger.error(line);
                    session.getAsyncRemote().sendText(line);
                }
            }

            br.close();
            is.close();
        } catch (Exception e) {
            logger.error("执行命令error:", e);
            session.getAsyncRemote().sendText(e.toString());
        }
        logger.info("执行命令结束：{}", command);
        session.getAsyncRemote().sendText("执行结束");
    }

    @OnClose
    public void onClose() {
        logger.info("WebSocket close.");
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("WebSocket error!", error);
    }
}
