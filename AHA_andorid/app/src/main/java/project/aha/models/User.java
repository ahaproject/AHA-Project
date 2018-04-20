package project.aha.models;


import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String user_email;
    private String user_name;
    private int user_type;
    private String user_phone;

    public User(int user_id, String user_email, String user_name, int user_type, String user_phone) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_name = user_name;
        this.user_type = user_type;
        this.user_phone = user_phone;
    }

    public User(int user_id, String user_name, int user_type) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_type = user_type;
    }

    public User() {
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        if (user_name == null) {
            return user_email;
        }
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getUser_phone() {
        if (user_phone == null || user_phone.length() == 0 || user_phone.equals("null")) {
            return "";
        }

        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }


    public User clone(){
     return new User( user_id, user_email, user_name, user_type, user_phone);
    }
}
