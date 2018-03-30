package project.aha.models;


import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.Serializable;

public class Exercise implements Serializable{

    private int ex_id;
    private String subject;
    private int doctor_id;
    private StringBuilder description;
    private String diagnose;
    private String added_date;

    private String image_path;

    private String video_path;

    public Exercise(int ex_id,String subject, int doctor_id, StringBuilder description, String diagnose , String added_date) {
        this.ex_id =ex_id;
        this.subject = subject;
        this.doctor_id = doctor_id;
        this.description = description;
        this.diagnose = diagnose;
        this.added_date=added_date;
    }

    public int getEx_id() {
        return ex_id;
    }

    public void setEx_id(int ex_id) {
        this.ex_id = ex_id;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public StringBuilder getDescription() {
        return description;
    }

    public void setDescription(StringBuilder description) {
        this.description = description;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getImage() {
        return image_path;
    }

    public void setImage(String image) {
        this.image_path = image;
    }

    public String getVideo() {
        return video_path;
    }

    public void setVideo(String video) {
        this.video_path = video;
    }
}
