package project.aha.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Parent extends User implements Serializable{

    private boolean didAdvanceRegistration ;
    HashMap<String , String> metas ;

    public Parent(int user_id, String user_email, String user_name, int user_type, String user_phone,
                  boolean didAdvanceRegistration) {
        super(user_id, user_email, user_name, user_type, user_phone);
        this.didAdvanceRegistration = didAdvanceRegistration;
        metas = new HashMap<>();
    }

    public Parent(int user_id, String user_name, int parentType) {
        super(user_id , user_name , parentType);
    }

    public boolean DidAdvanceRegistration() {
        return didAdvanceRegistration;
    }

    public void setDidAdvanceRegistration(boolean didAdvanceRegistration) {
        this.didAdvanceRegistration = didAdvanceRegistration;
    }


    public String getMeta(String meta_key) {
        return metas.get(meta_key);
    }

    public void addMeta(String meta_key, String meta_value) {
        metas.put(meta_key,meta_value);
    }

    private class ParentMeta {

        String meta_key;
        String meta_value;

        public ParentMeta(String key  , String value){
            meta_key = key;
            meta_value = value;
        }

        public String getMeta_key() {
            return meta_key;
        }

        public void setMeta_key(String meta_key) {
            this.meta_key = meta_key;
        }

        public String getMeta_value() {
            return meta_value;
        }

        public void setMeta_value(String meta_value) {
            this.meta_value = meta_value;
        }
    }
}
