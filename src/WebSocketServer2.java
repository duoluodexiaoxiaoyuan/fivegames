import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/myws2/{nickname}")
public class WebSocketServer2 {
    private String nickname;
    private Session session;
    private static final Set<WebSocketServer2> WEB_SOCKET_SERVER_2_SET = new CopyOnWriteArraySet<WebSocketServer2>();

    @OnMessage
    public void onMessage(String message, @PathParam(value = "nickname") String nickname) throws IOException {
        System.out.println("收到了客户端发来的消息：" + message);
        sendText(nickname+"发来了:"+message);
    }

    private static void sendText(String msg) {
        for (WebSocketServer2 webSocketServer2 : WEB_SOCKET_SERVER_2_SET) {
            try {
                synchronized (webSocketServer2) {
                    webSocketServer2.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                WEB_SOCKET_SERVER_2_SET.remove(webSocketServer2);
                try {
                    webSocketServer2.session.close();
                } catch (IOException e1) {
                }
                sendText(webSocketServer2.nickname + "同学已经下线");
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "nickname") String nickname) throws IOException {
        this.nickname = nickname;
        this.session = session;
        WEB_SOCKET_SERVER_2_SET.add(this);
        sendText(nickname + "进入房间");
        StringBuffer sb = new StringBuffer();
        for (WebSocketServer2 webSocketServer2 : WEB_SOCKET_SERVER_2_SET) {
            sb.append(webSocketServer2.nickname).append(";");
        }
        sendText("当前房间有："+sb.toString());
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        WEB_SOCKET_SERVER_2_SET.remove(this);
        sendText(this.nickname+"童鞋已下线");
    }
}