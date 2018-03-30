package project.aha.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Parent extends User implements Serializable{

    private String childName ;
    private String childBDate ;
    private int currentDiagnoseID ;
    ArrayList<ParentMeta> metas ;

    public Parent(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone,
                  String childName, String childBDate, int currentDiagnoseID) {
        super(user_id, user_email, user_name, user_password, user_type, user_phone);
        this.childName = childName;
        this.childBDate = childBDate;
        this.currentDiagnoseID = currentDiagnoseID;
        metas = new ArrayList<>();
    }

    public Parent(int user_id, String user_name, int parentType) {
        super(user_id , user_name , parentType);
    }


    public ArrayList<ParentMeta> getMetas() {
        return metas;
    }

    public void setMetas(ArrayList<ParentMeta> metas) {
        this.metas = metas;
    }

    public void addMeta(String key , String value){
        metas.add(new ParentMeta(key , value));
    }
    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildBDate() {
        return childBDate;
    }

    public void setChildBDate(String childBDate) {
        this.childBDate = childBDate;
    }

    public int getCurrentDiagnoseID() {
        return currentDiagnoseID;
    }

    public void setCurrentDiagnoseID(int currentDiagnoseID) {
        this.currentDiagnoseID = currentDiagnoseID;
    }
}
