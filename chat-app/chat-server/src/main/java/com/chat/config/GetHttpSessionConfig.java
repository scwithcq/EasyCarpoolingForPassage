package com.chat.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 获取 HttpSession 的配置类，用于在 WebSocket 握手时拿到登录用户信息
 */
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取 HttpSession 对象，JWT 无状态登录下可能为空，此处不再中断握手
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession == null) {
            return;
        }
        // 存储 HttpSession 对象到 WebSocket 会话属性中，供 onOpen 时取出
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}