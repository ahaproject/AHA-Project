package project.aha.models;

public class Parent extends User {

    private String childName ;
    private String childBDate ;
    private int currentDiagnoseID ;
    private String fileNumber ;

    public Parent(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone,
                  String childName, String childBDate, int currentDiagnoseID, String fileNumber) {
        super(user_id, user_email, user_name, user_password, user_type, user_phone);
        this.childName = childName;
        this.childBDate = childBDate;
        this.currentDiagnoseID = currentDiagnoseID;
        this.fileNumber = fileNumber;
    }

    public Parent(int user_id, String user_name, int parentType, String fileNumber) {
        super(user_id , user_name , parentType);
        this.fileNumber = fileNumber;
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

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }
}
