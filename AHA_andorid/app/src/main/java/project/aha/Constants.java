package project.aha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import project.aha.models.User;

public class Constants{ // class of constants variables
    public static final String [] SPECIALIZES_ARRAY = new String[]{"sp1","sp2","sp3"};

    public static final int ADMIN_TYPE = 0 ;
    public static final int DOCTOR_TYPE = 1 ;
    public static final int PARENT_TYPE = 2 ;

    public static final int LOGIN = 1 ;
    public static final int SIGNUP = 2 ;
    public static final int ADD_DOCTOR = 3 ;
    public static final int LIST_DOCTORS = 4;
    public static final int DELETE_DOCTOR = 5 ;
    public static final int LIST_PARENTS = 6;
    public static final int DELETE_PARENT = 7 ;

    public static final String RESULT = "result";

    public static final String ERR_INSERT_DOCTOR = "err1";
    public static final String ERR_INSERT_USER = "err2";
    public static final String ERR_DUPLICAT_ACC = "err3";

    public static final String ERR_DELETE_DOCTOR = "err4";
    public static final String ERR_DELETE_PARENT = "err5";

    public static final String SCF_INSERT_DOCTOR = "scf1";
    public static final String SCF_DELETE_DOCTOR = "scf2";
    public static final String SCF_DELETE_PARENT = "scf3";
    public static final String SCF_INSERT_PARENT = "scf4";

    public static final String USER_TABLE = "User";
    public static final String USER_ID_META = "user_ID";
    public static final String USER_EMAIL_META = "user_email";
    public static final String USER_PHONE_META = "user_phone";
    public static final String USER_PASSWORD_META = "user_password";
    public static final String USER_NAME_META = "user_name";
    public static final String USER_TYPE_META = "user_type";


    public static final String DOCTOR_TABLE = "Doctor";
    public static final String DOCTOR_SPECILIZED_META = "doctor_specialized";


    public static final String PARENT_TABLE = "Parent";
    public static final String PARENT_FILE_NUMBER = "parent_file_number";



    public static final String PREF_FILE_NAME = "aha_pref_file";
    public static final String PREF_USER_LOGGED_ID = "user_id";
    public static final String PREF_USER_LOGGED_TYPE = "user_type";


    public static final String DATABASE_URL = "https://ahaproject.000webhostapp.com/";


    public static User currentUser = null;


    public static void logout(Activity activity){
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PREF_USER_LOGGED_ID, -1);
        editor.putInt(Constants.PREF_USER_LOGGED_TYPE, -1);
        editor.commit();

        activity.startActivity(new Intent(activity , LoginActivity.class));
        activity.finish();
    }
}