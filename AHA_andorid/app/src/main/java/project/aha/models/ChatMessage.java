package project.aha.models;

import java.util.Date;

public class ChatMessage {
    String author;
    String content;
    String time ;


    public ChatMessage(String author, String content, String time) {
        this.author = author;
        this.content = content;
        this.time = time;
    }

    public ChatMessage() {
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
