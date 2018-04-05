package project.aha.models;

public class Doctor extends User {
    private int diag_id ;


    public Doctor(int user_id, String user_email, String user_name, int user_type, String user_phone , int diag_id) {
        super(user_id, user_email, user_name, user_type, user_phone);
        this.diag_id = diag_id;
    }


    public int getDiag_id() {
        return diag_id;
    }

    public void setDiag_id(int diag_id) {
        this.diag_id = diag_id;
    }
}
