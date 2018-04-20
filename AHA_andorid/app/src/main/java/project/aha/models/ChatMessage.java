package project.aha.models;

import java.util.Date;

public class ChatMessage {
    private String author;
    private String content;
    private String time;
    private String seen;
    private int other_user_id;


    public ChatMessage(int other_user_id, String author, String content, String time, String seen) {
        this.author = author;
        this.content = content;
        this.time = time;
        this.seen = seen;
        this.other_user_id = other_user_id;
    }

    public int getOther_user_id() {
        return other_user_id;
    }

    public void setOther_user_id(int other_user_id) {
        this.other_user_id = other_user_id;
    }

    public ChatMessage() {
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
