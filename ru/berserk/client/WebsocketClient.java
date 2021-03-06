package ru.berserk.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebsocketClient {
	
	Session userSession;
	CycleServerRead commandHandler;
	
	Object monitor = new Object();
	
	static WebsocketClient client;
	
	public static void connect(String url, CycleServerRead cycleReadFromServer) {
		System.out.println("Try to connect url: "+url);
		client = new WebsocketClient(URI.create(url), cycleReadFromServer);  
	}
	
    private WebsocketClient(URI endpointURI, CycleServerRead cycleReadFromServer) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            this.commandHandler = cycleReadFromServer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @OnOpen
	public void onOpen(Session userSession) {
		System.out.println("session started");
		this.userSession = userSession;
	}
	
	@OnClose
	public void onClose(Session userSession) {
		System.out.println("session closed");
	}
	
	@OnMessage
	public void onMessage(String message) throws UnsupportedEncodingException {
		System.out.println("Recieve message: " + message);
		synchronized (monitor) {
			Main.writerToLog.println(message);
			commandHandler.processCommand(message);
		}
	}
	
	public void sendMessage(String message) {
		System.out.println("Send message: " + message);
		this.userSession.getAsyncRemote().sendText(message);
	}
	
	@OnError
	public void onError(Throwable th) {
		System.out.println("error: " + th.getMessage());
		th.printStackTrace();
	}
	
//	public static void main(String[] args) {
//		//WebsocketClient client = new WebsocketClient(URI.create("ws://test1.uralgufk.ru:8080/WebsocketDemo/serverendpointdemo"), null);
//		WebsocketClient client = new WebsocketClient(URI.create("ws://localhost:8080/BHServer/serverendpointdemo"), null);
//		//http://localhost:8080/BHServer/WEB-INF/web.xml
//		client.sendMessage("Hello world");
//	}
}
