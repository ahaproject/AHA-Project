package project.aha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import java.util.ArrayList;
import project.aha.admin_panel.AdminMainActivity;
import project.aha.doctor_panel.DoctorMainActivity;
import project.aha.models.Diagnose;
import project.aha.models.User;
import project.aha.parent_panel.ParentMainActivity;

public class Constants { // class of constants variables


    /*List of all specialized */
    public static final ArrayList<Diagnose> diagnoses = new ArrayList<Diagnose>() {{
        add(new Diagnose(0, "Speaking"));
        add(new Diagnose(1, "Speacking"));
        add(new Diagnose(2, "Speacking"));
        add(new Diagnose(3, "Speacking"));
    }};
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################
    /* List of users types */
    public static final int ADMIN_TYPE = 0;
    public static final int DOCTOR_TYPE = 1;
    public static final int PARENT_TYPE = 2;

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /*List of all commands to the database */
    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;
    public static final int ADD_DOCTOR = 3;
    public static final int LIST_DOCTORS = 4;
    public static final int DELETE_DOCTOR = 5;
    public static final int LIST_PARENTS = 6;
    public static final int DELETE_PARENT = 7;
    public static final int SELECT_PARENT = 8;
    public static final int CREATE_EXERCISE = 9;
    public static final int LIST_EXERCISES = 10;
    public static final int SELECT_SINGLE_EXERCISE = 11;

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of results */
    public static final String NO_RECORDS = "NO_RECORDS";
    public static final String RECORDS = "RECORDS";


    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################


    /* List of error results */
    public static final String ERR_INSERT_USER = "ERR_INSERT_USER";
    public static final String ERR_INSERT_DOCTOR = "ERR_INSERT_DOCTOR";
    public static final String ERR_INSERT_PARENT = "ERR_INSERT_PARENT";
    public static final String ERR_LOGIN = "ERR_LOGIN";
    public static final String ERR_DUPLICATE_ACC = "ERR_DUPLICAT_ACC";
    public static final String ERR_DELETE_DOCTOR = "ERR_DELETE_DOCTOR";
    public static final String ERR_DELETE_PARENT = "ERR_DELETE_PARENT";
    public static final String ERR_CREATE_EXEC = "ERR_CREATE_EXEC";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of successful results */
    public static final String SCF_INSERT_DOCTOR = "SCF_INSERT_DOCTOR";
    public static final String SCF_INSERT_PARENT = "SCF_INSERT_PARENT";
    public static final String SCF_LOGIN = "SCF_LOGIN";
    public static final String SCF_DELETE_DOCTOR = "SCF_DELETE_DOCTOR";
    public static final String SCF_DELETE_PARENT = "SCF_DELETE_PARENT";
    public static final String SCF_CREATE_EXEC = "SCF_CREATE_EXEC";
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################


    /* List of user table meta */
    public static final String USER_TABLE = "User";
    public static final String USER_ID_META = "user_ID";
    public static final String USER_EMAIL_META = "user_email";
    public static final String USER_PHONE_META = "user_phone";
    public static final String USER_PASSWORD_META = "user_password";
    public static final String USER_NAME_META = "user_name";
    public static final String USER_TYPE_META = "user_type";
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of parent table meta */
    public static final String PARENT_TABLE = "Parent";
    public static final String PARENT_FILE_NUMBER = "parent_file_number";
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of doctor table meta */
    public static final String DOCTOR_TABLE = "Doctor";
    public static final String DOCTOR_ID_META = "doctor_id";
    public static final String DOCTOR_SPECILIZED_META = "doctor_specialized";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of exercise table meta */
    public static final String EXERCISE_TABLE = "Exercise";
    public static final String EXERCISE_ID_META = "ex_id";
    public static final String EXERCISE_SUBJECT = "ex_subject";
    public static final String EXERCISE_DESCRIPTION = "ex_article";
    public static final String EXERCISE_DOCTOR_ID = "doctor_id";
    public static final String EXERCISE_DIAGNOSE = "diag_id";
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of picture table meta */
    public static final String PICTURE_TABLE = "Picture";
    public static final String PICTURE_ID_META = "pic_id";
    public static final String PICTURE_PATH = "pic_path";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################

    /* List of picture table meta */
    public static final String VIDEO_TABLE = "Video";
    public static final String VIDEO__ID_META = "vid_id";
    public static final String VIDEO__PATH = "vid_path";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################



    public static final String CODE = "code";
    public static final String RESULT = "result";

    // ---------- end of constants shared with php -------------


    public static final String PREF_FILE_NAME = "aha_pref_file";
    public static final String PREF_USER_LOGGED_ID = "user_id";
    public static final String PREF_USER_LOGGED_TYPE = "user_type";
    public static final String DATABASE_URL = "https://ahaproject.000webhostapp.com/";
    // ###########################################################################################
    // ###########################################################################################


    public static User currentUser = null;
    // ###########################################################################################
    // ###########################################################################################


    public static void logout(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PREF_USER_LOGGED_ID, -1);
        editor.putInt(Constants.PREF_USER_LOGGED_TYPE, -1);
        editor.commit();
        Constants.currentUser = null;

        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
    // ###########################################################################################

    public static void login(Activity activity, User user) {

        // write usr data to a file -> Shared Prefernences
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PREF_USER_LOGGED_ID, user.getUser_id());
        editor.putInt(Constants.PREF_USER_LOGGED_TYPE, user.getUser_type());
        editor.commit(); /// to save


        Constants.currentUser = user;

        Intent i = null;
        switch (user.getUser_type()) {
            case Constants.ADMIN_TYPE: {
                // if user admin type 0
                // intent -> adminMainActivity
                i = new Intent(activity, AdminMainActivity.class);

                break;
            }

            case Constants.DOCTOR_TYPE: {
                // if user admin type 1
                // intent -> DoctorMainActivity
                i = new Intent(activity, DoctorMainActivity.class);
                break;
            }
            case Constants.PARENT_TYPE: {
                // if user admin type 2
                // intent -> ParentMainActivity
                i = new Intent(activity, ParentMainActivity.class);
                break;
            }
        }
        activity.startActivity(i); // start main activity
        activity.finish(); // stop login activity
    }
    // ###########################################################################################

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    // ###########################################################################################

    public static void add_to_spinner(Activity calledActivity, Spinner spinner) {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(calledActivity, R.layout.single_spinner_item, Constants.diagnoses);
        spinner.setAdapter(spinnerAdapter);

    }

    // ###########################################################################################

    public static int get_current_user_id(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        final int user_id = prefs.getInt(Constants.PREF_USER_LOGGED_ID, -1);
        return user_id;
    }
}
