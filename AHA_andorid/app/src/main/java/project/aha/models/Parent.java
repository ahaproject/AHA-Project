package project.aha.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Parent extends User implements Serializable {

    private boolean didAdvanceRegistration;
    private int consult_doctor;
    private Doctor doctor_obj;
    private HashMap<String, String> metas;
    private String diagnoses_ids;

    public Parent(int user_id, String user_email, String user_name, int user_type, String user_phone,
                  boolean didAdvanceRegistration, int consult_doctor) {
        super(user_id, user_email, user_name, user_type, user_phone);
        this.didAdvanceRegistration = didAdvanceRegistration;
        this.consult_doctor = consult_doctor;
        metas = new HashMap<>();
    }


    public String getDiagnoses_ids() {
        return diagnoses_ids;
    }

    public void setDiagnoses_ids(String diagnoses_ids) {
        this.diagnoses_ids = diagnoses_ids;
    }

    public boolean isDidAdvanceRegistration() {
        return didAdvanceRegistration;
    }

    public Doctor getDoctor_obj() {
        return doctor_obj;
    }

    public void setDoctor_obj(Doctor doctor_obj) {
        this.doctor_obj = doctor_obj;
    }

    public int getConsult_doctor() {
        return consult_doctor;
    }

    public void setConsult_doctor(int consult_doctor) {
        this.consult_doctor = consult_doctor;
    }

    public boolean DidAdvanceRegistration() {
        return didAdvanceRegistration;
    }

    public void setDidAdvanceRegistration(boolean didAdvanceRegistration) {
        this.didAdvanceRegistration = didAdvanceRegistration;
    }


    public String getMeta(String meta_key) {
        if (metas == null)
            return null;
        return metas.get(meta_key);
    }

    public void addMeta(String meta_key, String meta_value) {
        metas.put(meta_key, meta_value);
    }

    public HashMap<String, String> getMetas() {
        return metas;
    }

}
