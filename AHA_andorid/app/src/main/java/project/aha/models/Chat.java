package project.aha.models;


import java.io.Serializable;

public class Chat implements Serializable{
    private String chatKey ;
    private User other_user;
    private String last_chat_content ;
    private String last_chat_time ;


    public Chat(String chatKey, User other_user, String last_chat_content, String last_chat_time) {
        this.chatKey = chatKey;
        this.other_user = other_user;
        this.last_chat_content = last_chat_content;
        this.last_chat_time = last_chat_time;
    }


    public String getChatKey() {
        return chatKey;
    }
    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }
    public String getLast_chat_content() {
        return last_chat_content;
    }
    public void setLast_chat_content(String last_chat_content) {
        this.last_chat_content = last_chat_content;
    }
    public String getLast_chat_time() {
        return last_chat_time;
    }

    public User getOther_user() {
        return other_user;
    }

    public void setOther_user(User other_user) {
        this.other_user = other_user;
    }

    public void setLast_chat_time(String last_chat_time) {
        this.last_chat_time = last_chat_time;
    }
}
