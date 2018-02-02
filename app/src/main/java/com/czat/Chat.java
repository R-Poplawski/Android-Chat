package com.czat;

import android.app.Application;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

interface Callback {
    void run(JSONObject response);
}

public class Chat extends Application {
    private WebSocketClient socket;
    private String baseUrl = "ws://192.168.2.3:8888";
    private URI serverUri;
    private String pendingMessage = null;
    private String username = null;
    private int userId = -1;

    private Callback onLoginResponse, onRegisterResponse, onRequestContactResponse,
            onAcceptRequestResponse, onIncomingContactRequest, onRemoveContactResponse,
            onSendMessageResponse, onIncomingMessage, onIncomingRemove, onGetMessages;

    public void setOnLoginResponse(Callback response) {
        onLoginResponse = response;
    }

    public void setOnRegisterResponse(Callback response) {
        onRegisterResponse = response;
    }

    public void setOnRequestContactResponse(Callback response) { onRequestContactResponse = response; }

    public void setOnAcceptRequestResponse(Callback response) { onAcceptRequestResponse = response; }

    public void setOnIncomingContactRequest(Callback response) { onIncomingContactRequest = response; }

    public void setOnRemoveContactResponse(Callback response) { onRemoveContactResponse = response; }

    public void setOnSendMessageResponse(Callback response) { onSendMessageResponse = response; }

    public void setOnIncomingMessage(Callback response) { onIncomingMessage = response; }

    public void setOnIncomingRemove(Callback response) { onIncomingRemove = response; }

    public void setOnGetMessages(Callback response) { onGetMessages = response; }

    public String getUsername() {
        return username;
    }

    public int getUserId() { return userId; }

    public boolean isLoggedIn() {
        return username != null && socket.getConnection().isOpen();
    }

    public Object getValueFromJSON(JSONObject jsonObject, String key) {
        Object value;
        try { value = jsonObject.get(key); }
        catch (Exception e) { return null; }
        return value;
    }

    public Chat() {
        try { serverUri = new URI(baseUrl); }
        catch (URISyntaxException e) { e.printStackTrace(); }
        initSocket();
    }

    private void initSocket() {
        Log.d("Chat", "initSocket()");
        socket = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //socket.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                /*while (!messageQueue.isEmpty()) {
                    String msg = messageQueue.poll();
                    if (msg != null) socket.send(msg);
                }*/
                if (pendingMessage != null) {
                    socket.send(pendingMessage);
                    pendingMessage = null;
                }
            }

            @Override
            public void onMessage(String s) {
                Log.i("Websocket", "Message: " + s);
                //String handler = s.split("\n")[0];
                //Callback callback = handlers.get(handler);
                //if (callback != null) callback.run(s);
                handleMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                username = null;
                userId = -1;
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        socket.connect();
    }

    private void sendPacket(String handler, JSONObject data) {
        try {
            JSONObject jsonObject = new JSONObject().put(handler, data);
            String msg = jsonObject.toString();
            if (socket.getConnection().isOpen()) {
                socket.send(msg);
                Log.d("Chat - Sending message", msg);
            }
            else {
                pendingMessage = msg;
                if (socket.getConnection().isClosed()) initSocket();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String handler = jsonObject.keys().next();
            jsonObject = jsonObject.getJSONObject(handler);

            switch (handler) {
                case "incoming_message":
                    onIncomingMessage.run(jsonObject);
                    break;
                case "send_message":
                    onSendMessageResponse.run(jsonObject);
                    break;
                case "get_messages":
                    onGetMessages.run(jsonObject);
                    break;
                case "request_contact":
                    onRequestContactResponse.run(jsonObject);
                    break;
                case "accept_request": {
                    boolean acceptSuccess = (boolean) getValueFromJSON(jsonObject, "success");
                    if (acceptSuccess) {
                        int id = (int) getValueFromJSON(jsonObject, "id");
                        String username = (String) getValueFromJSON(jsonObject, "username");
                        ContactList.addItem(new Contact(id, username));
                    }
                    onAcceptRequestResponse.run(jsonObject);
                    break;
                }
                case "incoming_request": {
                    int id = (int) getValueFromJSON(jsonObject, "id");
                    String username = (String) getValueFromJSON(jsonObject, "username");
                    RequestList.addItem(new Request(id, username));
                    onIncomingContactRequest.run(jsonObject);
                    break;
                }
                case "remove_contact":
                    onRemoveContactResponse.run(jsonObject);
                    break;
                case "incoming_remove":
                    onIncomingRemove.run(jsonObject);
                    break;
                case "login": {
                    boolean loginSuccess = (boolean)getValueFromJSON(jsonObject, "success");
                    if (loginSuccess) {
                        username = (String) getValueFromJSON(jsonObject, "username");
                        userId = (int) getValueFromJSON(jsonObject, "id");
                    }
                    else {
                        username = null;
                        userId = -1;
                    }
                    onLoginResponse.run(jsonObject);
                    break;
                }
                case "register": {
                    boolean registerSuccess = (boolean) getValueFromJSON(jsonObject, "success");
                    if (registerSuccess) {
                        username = (String) getValueFromJSON(jsonObject, "username");
                        userId = (int) getValueFromJSON(jsonObject, "id");
                    } else {
                        username = (String) getValueFromJSON(jsonObject, "username");
                        userId = (int) getValueFromJSON(jsonObject, "id");
                    }
                    onRegisterResponse.run(jsonObject);
                    break;
                }
            }
        }
        catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("username", username).put("password", password);
            sendPacket("login", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(String username, String password) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("username", username).put("password", password);
            sendPacket("register", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestContact(String username) {
        try {
            JSONObject jsonObject = new JSONObject().put("username", username);
            sendPacket("request_contact", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptRequest(int userId) {
        try {
            JSONObject jsonObject = new JSONObject().put("id", userId);
            sendPacket("accept_request", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeContact(int userId) {
        try {
            JSONObject jsonObject = new JSONObject().put("id", userId);
            sendPacket("remove_contact", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int userId, String content) {
        try {
            JSONObject jsonObject = new JSONObject().put("id", userId).put("content", content);
            sendPacket("send_message", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMessages(int userId, int oldestMessageId) {
        try {
            JSONObject jsonObject = new JSONObject().put("user_id", userId).put("oldest_message_id", oldestMessageId);
            sendPacket("get_messages", jsonObject);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        if (socket != null && !socket.getConnection().isClosed()) socket.close();
        userId = -1;
        username = null;
        ContactList.ITEMS.clear();
        RequestList.ITEMS.clear();
        MessageList.clear();
    }
}
