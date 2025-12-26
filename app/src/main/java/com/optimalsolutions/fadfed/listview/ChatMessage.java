package com.optimalsolutions.fadfed.listview;

public class ChatMessage {

    public String body,  senderId;
    public String Date;
    public String msgid;
    public boolean isMine;// Did I send the message.

    public ChatMessage(String sender, String messageString,
                       String date, boolean isMINE) {

        isMine = isMINE;
        Date = date;
        body = messageString;
        senderId = sender;
    }

    @Override
    public boolean equals(Object o) {

        ChatMessage message = (ChatMessage) o;
        return message.senderId.equalsIgnoreCase(senderId) && message.Date.equalsIgnoreCase(Date);

    }
}