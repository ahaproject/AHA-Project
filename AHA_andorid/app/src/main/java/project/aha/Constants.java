package project.aha;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import project.aha.Chatting.ChatsActivity;
import project.aha.admin_panel.AdminMainActivity;
import project.aha.doctor_panel.DoctorMainActivity;
import project.aha.interfaces.DialogCallBack;
import project.aha.models.Diagnose;
import project.aha.models.Doctor;
import project.aha.models.Parent;
import project.aha.models.User;
import project.aha.parent_panel.ParentMainActivity;

public class Constants { // class of constants variables


    /*List of all specialized */
    public static final HashMap<Integer, Diagnose> diagnoses = new HashMap<>();
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
    public static final int COMPLETE_REGISTRATION = 12;
    public static final int CARS_EXAM = 13;
    public static final int SAVE_CARS_RESULTS = 14;
    public static final int LIST_MEDICAL_HISTORIES = 15;
    public static final int SELECT_TEST_HISTORY = 16;
    public static final int LIST_DIAGNOSES = 17;
    public static final int SELECT_USER = 18;
    public static final int ASSIGN_DOCTOR = 19;
    public static final int REPORT_ABUSE = 20;


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
    public static final String ERR_DUPLICATE_ACC = "ERR_DUPLICATE_ACC";
    public static final String ERR_DELETE_DOCTOR = "ERR_DELETE_DOCTOR";
    public static final String ERR_DELETE_PARENT = "ERR_DELETE_PARENT";
    public static final String ERR_CREATE_EXEC = "ERR_CREATE_EXEC";
    public static final String ERR_SAVE_EXAM_RESULT = "ERR_SAVE_EXAM_RESULT";
    public static final String ERR_ASSIGN_DOCTOR = "ERR_ASSIGN_DOCTOR";


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
    public static final String SCF_COMPLETE_REGITER = "SCF_COMPLETE_REGITER";
    public static final String SCF_SAVE_EXAM_RESULT = "SCF_SAVE_EXAM_RESULT";
    public static final String SCF_ASSIGN_DOCTOR = "SCF_ASSIGN_DOCTOR";

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
    public static final String PARENT_ID_META = "parent_id";
    public static final String PAREN_DID_ADVANCED_REGISTRATION = "did_adv_register";
    public static final String CONSULT_DOCTOR = "consult_doctor";

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
    public static final String VIDEO_ID_META = "vid_id";
    public static final String VIDEO_PATH = "vid_path";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################
        /* List of medical history table meta */
    public static final String MEDICAL_HISTORY_ID_META = "med_id";
    public static final String MEDICAL_HISTORY_SCORE_META = "score";
    public static final String MEDICAL_HISTORY_DATE_META = "date";
    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################
            /* List of test history table meta */
    public static final String TEST_HISTORY = "";

    private static final String CHAT_KEY = "chat_key";

    // ###########################################################################################
    // ###########################################################################################
    // ###########################################################################################
    public static final String CODE = "code";
    public static final String RESULT = "result";

    // ---------- end of constants shared with php -------------


    public static final String PREF_FILE_NAME = "aha_pref_file";
    public static final String PREF_USER_LOGGED_ID = "user_id";
    public static final String PREF_USER_LOGGED_TYPE = "user_type";
    private static final String PREF_USER_LOGGED_OBJECT = "user_object";
    public static final String DATABASE_URL = "https://ahaproject.000webhostapp.com/";
    //    public static final String DATABASE_URL = "http://10.0.2.2";
    public static final String DATA = "data";
    public static final String CARS_RESULTS = "CARS_results";
    public static final String WHAT_IS_AUTISM_URL = DATABASE_URL + "/symptoms.html";
    public static final String LIST_DOCTORS_ACTIVTY_CHOICE = "LIST_DOCTORS_ACTIVTY_CHOICE";
    public static final String LIST_PARENTS_ACTIVTY_CHOICE = "LIST_PARENTS_ACTIVTY_CHOICE";
    public static final String CHAT_USERS_SEPARATOR = ",";
    public static final int HASHMAP_LENGTH = 50;

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


    public static User updateUser(Activity activity, JSONObject output) {
        User user = Constants.readAndSaveUser(output);
// write usr data to a file -> Shared Prefernences
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PREF_USER_LOGGED_ID, user.getUser_id());
        editor.putInt(Constants.PREF_USER_LOGGED_TYPE, user.getUser_type());

        Gson gson = new Gson();
        String user_json = gson.toJson(user);
        editor.putString(Constants.PREF_USER_LOGGED_OBJECT, user_json);


        editor.commit(); /// to save


        Constants.currentUser = user;
        return user;
    }

    public static void login(Activity activity, User user) {

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

    public static int get_current_user_id(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        final int user_id = prefs.getInt(Constants.PREF_USER_LOGGED_ID, -1);
        return user_id;
    }

    public static int get_current_user_type(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);
        final int user_type = prefs.getInt(Constants.PREF_USER_LOGGED_TYPE, -1);
        return user_type;
    }


    public static void showLogo(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher_round);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public static User get_user_object(Activity activity) {
         /*

        to retrive
        */
        Gson gson = new Gson();
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_FILE_NAME, activity.MODE_PRIVATE);

        String json = prefs.getString(Constants.PREF_USER_LOGGED_OBJECT, "");

        int type = get_current_user_type(activity);
        if (type == Constants.PARENT_TYPE) {
            return gson.fromJson(json, Parent.class);
        }

        if (type == Constants.DOCTOR_TYPE) {
            return gson.fromJson(json, Doctor.class);
        }


        User user = gson.fromJson(json, User.class);
        return user;

    }


    public static String getCARSResult(Activity activity, double sum) {

        if (sum < 26) {
            return activity.getString(R.string.normal_kid);
        } else if (sum >= 26 && sum <= 29) {
            return activity.getString(R.string.autism_spectrum);
        } else if (sum >= 30 && sum <= 33) {
            return activity.getString(R.string.simple_autism);
        } else if (sum >= 34 && sum <= 36) {
            return activity.getString(R.string.intermediate_autism);
        } else if (sum >= 37) {
            return activity.getString(R.string.severe_autism);
        }

        return "";
    }

    public static boolean handleItemChoosed(Activity activity, boolean defaultChoice, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout(activity);
                return true;

            case R.id.chat_activity:
                activity.startActivity(new Intent(activity, ChatsActivity.class));
                return true;

//            case R.id.show_profile:{
//                int current_user_type = get_current_user_type(activity);
//                if(current_user_type == Constants.DOCTOR_TYPE){
//                    new Intent()
//                }
//                if(current_user_type == Constants.ADMIN_TYPE){
//
//                }
//                break;
//            }
            default:
                return defaultChoice;
        }
    }

    public static void addDiagnose(Diagnose diagnose) {
        diagnoses.put(diagnose.getId(), diagnose);
    }


    @Nullable
    public static User readAndSaveUser(JSONObject output) {
        try {
            output = output.getJSONArray("data").getJSONObject(0);

            int user_id = output.optInt(Constants.USER_ID_META, output.optInt("user_ID"));
            String user_email = output.getString(Constants.USER_EMAIL_META);
            String user_phone = output.optString(Constants.USER_PHONE_META, "");
            String user_name = output.optString(Constants.USER_NAME_META, "");
            int user_type = output.getInt(Constants.USER_TYPE_META);


            if (user_type == Constants.PARENT_TYPE) {
                boolean did_adv_reg = (output.getInt(Constants.PAREN_DID_ADVANCED_REGISTRATION) == 0) ? false : true;
                int consult_doctor = output.optInt(Constants.CONSULT_DOCTOR, -1);
                Parent parent = new Parent(user_id, user_email, user_name, user_type, user_phone, did_adv_reg, consult_doctor);
                JSONArray metas = output.optJSONArray("metas");
                if (metas != null) {
                    for (int i = 0; i < metas.length(); i++) {
                        JSONObject meta = metas.getJSONObject(i);
                        String meta_key = meta.getString("meta_key");
                        String meta_value = meta.getString("meta_value");
                        parent.addMeta(meta_key, meta_value);
                    }
                }
                return parent;

            }

            if (user_type == Constants.DOCTOR_TYPE) {
                int diagn = output.getInt(Constants.DOCTOR_SPECILIZED_META);
                Doctor doctor = new Doctor(user_id, user_email, user_name, user_type, user_phone, diagn);
                return doctor;
            }


            return new User(user_id, user_email, user_name, user_type, user_phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // if any error occurred -> return null
        return null;
    }


    public static String getHashMapKey(Activity a, View view) {
        if (view == null)
            return null;
        try {
            String id = a.getResources().getResourceEntryName(view.getId());


            int endIndex = (id.toString().length() < Constants.HASHMAP_LENGTH) ? id.toString().length() : Constants.HASHMAP_LENGTH;
            String key = id.replaceAll("[^a-zA-Z]", "_").substring(0, endIndex).toLowerCase();
            return key;
        } catch (Resources.NotFoundException e) {
            Log.w("ERROR", e.getMessage());
        }

        return null;
    }

    public static void updateDiagnoses(Activity activity) {
        // get list of all diagnoses
        DatabasePostConnection conn = new DatabasePostConnection(activity, false, Constants.LIST_DIAGNOSES, false);
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.LIST_DIAGNOSES + "");
        conn.postRequest(data, Constants.DATABASE_URL);
    }

    public static void report_abuse(Activity activity, int parent_id, int doctor_id, String chat_key) {
        DatabasePostConnection conn = new DatabasePostConnection(activity, false, Constants.REPORT_ABUSE, false);
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.REPORT_ABUSE + "");
        data.put(Constants.DOCTOR_ID_META, doctor_id + "");
        data.put(Constants.PARENT_ID_META, parent_id + "");
        data.put(Constants.CHAT_KEY, chat_key);
        conn.postRequest(data, Constants.DATABASE_URL);
    }

    public static void showConfirmDeleteDialog(final DialogCallBack callBack, String message, String title) {
        if (!(callBack instanceof AppCompatActivity))
            return;
        // create dialog
        AlertDialog.Builder confirm_delete_dialog = new AlertDialog.Builder((Activity) callBack);

        // set title
        confirm_delete_dialog.setTitle(title);

        // set message
        confirm_delete_dialog.setMessage(message);

        // set icon
        confirm_delete_dialog.setIcon(android.R.drawable.ic_dialog_alert);

        // add action when admin click on yes
        confirm_delete_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                callBack.dialogCallBack();
            }
        });

        // add action if the admin click on cancel
        confirm_delete_dialog.setNegativeButton(android.R.string.no, null);


        // show the dialog
        confirm_delete_dialog.show();
    }

}
