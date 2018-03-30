package project.aha.models;

public class Doctor extends User {
    private String specialized ;


    public Doctor(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone , String user_specialized) {
        super(user_id, user_email, user_name, user_password, user_type, user_phone);
        this.specialized = user_specialized;
    }

    public String getSpecialized() {
        return specialized;
    }

    public void setSpecialized(String specialized) {
        this.specialized = specialized;
    }
}
