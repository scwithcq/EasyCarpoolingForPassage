package com.chat.WebSocket;

import com.alibaba.fastjson.JSON;
import com.chat.config.GetHttpSessionConfig;
import com.chat.entity.ChatMessage;
import com.chat.service.ChatMessageService;
import com.chat.utils.WsMessageUtils;
import com.chat.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws/chat/{username}", configurator = GetHttpSessionConfig.class)
public class WebSocketServer {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //用户ID
    private String username;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    private static final CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();

    //存放会话对象
    private static final Map<String, Session> onLineSession = new ConcurrentHashMap<>();

    // 每个连接由 Tomcat 反射创建，非 Spring 管理，故用静态字段注入业务 Bean
    private static ChatMessageService chatMessageService;

    @Autowired
    public void setChatMessageService(ChatMessageService chatMessageService) {
        WebSocketServer.chatMessageService = chatMessageService;
    }

    private HttpSession httpSession;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "username") String username, Session session) {
        try {
            this.session = session;
            this.username = username;
            // 从握手请求中取出 HttpSession（可能为空，JWT 无状态登录下不再强依赖）
            Object httpSessionAttr = session.getUserProperties().get(HttpSession.class.getName());
            this.httpSession = httpSessionAttr instanceof HttpSession ? (HttpSession) httpSessionAttr : null;
            webSockets.add(this);
            Session previous = onLineSession.put(username, session);
            if (previous != null && !previous.equals(session)) {
                // 同一账号重复连接：关闭旧连接，避免消息被投递到失效会话
                try {
                    previous.close();
                } catch (Exception ignore) {
                    log.warn("关闭重复连接失败，用户：{}", username);
                }
            }
            log.info("WebSocket消息有新的连接，用户：{}，总数为：{}", username, webSockets.size());
            // 新连接建立后广播最新在线用户列表，避免新打开的页面拿不到在线列表
            broadcastAllUser(WsMessageUtils.getMessage(true, null, getAllOnLineUser()));
        } catch (Exception e) {
            log.error("WebSocket异常-链接失败(onOpen)，原因：{}", e.getMessage(), e);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        try {
            MessageVO msg = JSON.parseObject(message, MessageVO.class);
            String sender = resolveSenderName();
            if (msg == null || !StringUtils.hasText(msg.getReceiveName()) || !StringUtils.hasText(msg.getMessage())) {
                log.warn("WebSocket收到非法消息，发送人：{}，内容：{}", sender, message);
                sendToSelf(WsMessageUtils.getMessage(true, null, "消息格式错误：接收人与内容不能为空"));
                return;
            }
            String receiveName = msg.getReceiveName();
            String content = msg.getMessage();

            // 1. 无论对方是否在线，先落库，避免消息丢失
            saveMessage(sender, receiveName, content);

            // 2. 对方在线则实时推送，离线则回执告知发送方
            Session targetSession = onLineSession.get(receiveName);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.getBasicRemote().sendText(WsMessageUtils.getMessage(false, sender, content));
            } else {
                log.info("接收方不在线，消息已存为离线消息，{} -> {}", sender, receiveName);
                sendToSelf(WsMessageUtils.getMessage(true, null, "对方不在线，消息已保存，对方上线后可查看"));
            }
        } catch (Exception e) {
            // 捕获全部异常（含 NPE/IOException），避免异常抛出到容器导致连接中断
            log.error("WebSocket异常-消息处理失败，原因：{}", e.getMessage(), e);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        try {
            webSockets.remove(this);
            // 仅当当前在线会话确为本连接时才移除，避免误删同账号的新连接
            onLineSession.remove(username, session);
            log.info("用户{} 连接断开，当前在线数：{}", username, webSockets.size());
            //广播消息，把在线用户列表推送给所有用户
            broadcastAllUser(WsMessageUtils.getMessage(true, null, getAllOnLineUser()));
        } catch (Exception e) {
            log.error("WebSocket异常-关闭连接失败，原因：{}", e.getMessage(), e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket异常-连接错误，用户：{}，原因：{}", username, error.getMessage());
    }

    /**
     * 发送人名称：优先取 HttpSession 中的登录用户名，回退到连接路径上的用户名
     */
    private String resolveSenderName() {
        if (httpSession != null) {
            Object attr = httpSession.getAttribute("username");
            if (attr != null && StringUtils.hasText(attr.toString())) {
                return attr.toString();
            }
        }
        return username;
    }

    /**
     * 消息落库，失败不影响实时推送链路
     */
    private void saveMessage(String fromName, String toName, String content) {
        if (chatMessageService == null) {
            log.warn("ChatMessageService 尚未注入，消息未落库");
            return;
        }
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFromName(fromName);
            chatMessage.setToName(toName);
            chatMessage.setContent(content);
            chatMessage.setIsRead(0);
            chatMessage.setCreateTime(LocalDateTime.now());
            chatMessageService.save(chatMessage);
        } catch (Exception e) {
            log.error("聊天消息落库失败，{} -> {}，原因：{}", fromName, toName, e.getMessage());
        }
    }

    /**
     * 给当前连接发送提示消息
     */
    private void sendToSelf(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("WebSocket异常-回执发送失败，原因：{}", e.getMessage());
        }
    }

    //获取所有在线的用户名称
    private Set getAllOnLineUser() {
        return onLineSession.keySet();
    }

    //系统广播
    private void broadcastAllUser(String message) {
        try {
            Set<Map.Entry<String, Session>> entries = onLineSession.entrySet();
            for (Map.Entry<String, Session> entry : entries) {
                //获取所有用户对应的session
                Session session = entry.getValue();
                if (session != null && session.isOpen()) {
                    //发送消息
                    session.getBasicRemote().sendText(message);
                }
            }
        } catch (Exception e) {
            //记录错误日志
            log.error("广播信息出错！原因：{}", e.getMessage());
        }
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = onLineSession.values();
        for (Session session : sessions) {
            try {
                if (session != null && session.isOpen()) {
                    //服务器向客户端发送消息
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("群发消息出错！原因：{}", e.getMessage());
            }
        }
    }

}
