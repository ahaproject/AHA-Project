package project.aha.models;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class ReportedChat implements Serializable {

    private int r_id;
    @Nullable
    private User reporter;
    @Nullable
    private User reported;
    private String chatKey;
    private String reported_date;
    private int is_solved;
    private String reason;
    private String seen;

    public ReportedChat() {
    }

    public int getR_id() {
        return r_id;
    }

    public void setR_id(int r_id) {
        this.r_id = r_id;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReported() {
        return reported;
    }

    public void setReported(User reported) {
        this.reported = reported;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getReported_date() {
        return reported_date;
    }

    public void setReported_date(String reported_date) {
        this.reported_date = reported_date;
    }

    public int getIs_solved() {
        return is_solved;
    }

    public void setIs_solved(int is_solved) {
        this.is_solved = is_solved;
    }

    public String getReason() {
        if (reason == null || reason.equals("null")) {
            return null;
        }
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReportedChat(int r_id, User reporter, User reported, String chatKey, String reported_date, int is_solved, String reason, String seen) {
        this.r_id = r_id;
        this.reporter = reporter;
        this.reported = reported;
        this.chatKey = chatKey;
        this.reported_date = reported_date;
        this.is_solved = is_solved;
        this.reason = reason;
        this.seen = seen;
    }
}
