package project.aha.models;

import java.io.Serializable;

public class MedicalHistory implements Serializable {

    private int parent_id;
    private int medical_hist_id;
    private double score;
    private String date;
    private int diagn_id;
    private int doctor_id;


    public MedicalHistory(int parent_id, int medical_hist_id, double score, String date, int diagn_id, int doctor_id) {
        this.parent_id = parent_id;
        this.medical_hist_id = medical_hist_id;
        this.score = score;
        this.date = date;
        this.diagn_id = diagn_id;
        this.doctor_id = doctor_id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getMedical_hist_id() {
        return medical_hist_id;
    }

    public void setMedical_hist_id(int medical_hist_id) {
        this.medical_hist_id = medical_hist_id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDiagn_id() {
        return diagn_id;
    }

    public void setDiagn_id(int diagn_id) {
        this.diagn_id = diagn_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }
}
