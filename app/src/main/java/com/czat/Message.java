package com.czat;

import java.util.Date;

public class Message {

    public Message(int id, int sender, int recipient, String content) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String username) {
        senderUsername = username;
    }

    public Date getDate_sent() {
        return date_sent;
    }

    public void setDate_sent(Date date_sent) {
        this.date_sent = date_sent;
    }

    private int id;
    private int sender, recipient;
    private String content;
    private String senderUsername;
    private Date date_sent;
}
