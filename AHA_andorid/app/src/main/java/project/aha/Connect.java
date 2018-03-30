package project.aha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;


class Constants{ // class of constants variables
    public static final int LOGIN = 1 ;
    public static final int SIGNUP = 2 ;

    public static final String PREF_FILE_NAME = "aha_pref_file";
    public static final String PREF_USER_LOGGED_KEY = "cred";


    public static final String DATABASE_URL = "https://ahaproject.000webhostapp.com/";


    public static User currentUser = null;



}

class ServiceStubAsyncTask extends AsyncTask<Void, Void, Object> { // class to connect to database in background


    private String apiPath = Constants.DATABASE_URL;

    //status of success / 0 failed, 1 success
    private int success = 0;

    // json arrays which hold the result from php
    private JSONArray restulJsonArray;


    // variable of current activity
    private Activity mActivity;
    private Context mContext;


    // what the user want to , if 1 -> login , 2 -> signup , ...
    private int function_code; // 1 -> login

    // data from the function , for login , will be hold email or phone number , password
    HashMap<Object, Object> function_data; // email , password , phone



    String response = "";

    // post data from php
    HashMap<String, String> postDataParams;


    private ProgressDialog processDialog;


    // accept the context and the Activity
    public ServiceStubAsyncTask(Context context, Activity activity,int function_code, HashMap<Object, Object> function_data) {
        this.function_data = function_data;
        mContext = context;
        this.function_code = function_code;
        mActivity = activity;
    }

    // before complete the task -> show progress dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        processDialog = new ProgressDialog(mContext);
        processDialog.setMessage("Please  Wait ...");
        processDialog.setCancelable(false);
        processDialog.show();
    }

    // what the task do
    @Override
    protected Object doInBackground(Void... arg0) {
        // put post meta to database
        postDataParams = new HashMap<String, String>();
        postDataParams.put("HTTP_ACCEPT", "application/json");


        // attach code function and data to url
        apiPath += "?code="+function_code ;


        // login
        if(function_code == Constants.LOGIN){
            apiPath += login(function_data);
        }

        // https://ahaproject.000webhostapp.com/?code=1&user_email=admin@aha.com&user_phone=admin@aha.com&user_password=123

        // just print the url to see the result in the log
        Log.d("URL Path", apiPath);

        // connect to the database
        HttpConnectionService service = new HttpConnectionService();

        // send the url we want and post meta
        response = service.sendRequest(apiPath, postDataParams);

//        for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
//            Log.d("HttpConnectionService", "POST entry.Key: " + entry.getKey());
//            Log.d("HttpConnectionService", "POST entry.Value: " + entry.getValue());
//        }


        try {
            // set the result to success
            success = 1;

            // get the json array from the response
            JSONObject resultJsonObject = new JSONObject(response);

            // get object named output
            restulJsonArray = resultJsonObject.getJSONArray("output");

            if (null != restulJsonArray) {
                for (int i = 0; i < restulJsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = restulJsonArray.getJSONObject(i);


                        if(function_code == Constants.LOGIN){
                            int user_id = Integer.parseInt((String) jsonObject.get(User.USER_ID_META));
                            String user_email = (String) jsonObject.get(User.USER_EMAIL_META) ;
                            String user_phone = (String) jsonObject.get(User.USER_PHONE_META) ;
                            String user_password = (String) jsonObject.get(User.USER_PASSWORD_META) ;
                            String user_name = (String) jsonObject.get(User.USER_NAME_META) ;
                            int user_type = Integer.parseInt((String) jsonObject.get(User.USER_TYPE_META)) ;
                            //     public User(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone) {
                            Constants.currentUser = new User( user_id, user_email, user_name,user_password,user_type,user_phone);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            // if any error set to failed
            success = 0;
            e.printStackTrace();
        }
        return Constants.currentUser;
    }


    @Override // after
    protected void onPostExecute(Object result) { // login -> User

        super.onPostExecute(result);

        Object obj = null ;
        // dismiss progress dialog
        if (processDialog.isShowing()) {
            processDialog.dismiss();
        }


        // login
        if(function_code == Constants.LOGIN){
//            Log.d("output" , result.toString());
            if (result != null) {
                // if user admin type 0
                // intent -> adminMainActivity
                Intent i = new Intent(mActivity,MainActivity.class);
                mContext.startActivity(i); // start main activity
                mActivity.finish(); // stop login activity
            } else {
                LoginActivity la = ((LoginActivity)mActivity);
                la.showErrors();

            }
        }

        else if(function_code == Constants.SIGNUP){

            // ........

        }



        // ....
    }







    // AsyncTask to work in the background
    private String login(HashMap<Object, Object> function_data) {
        String user_email = (String)function_data.get("user_email");// admin@aha.com
        String user_phone = (String)function_data.get("user_phone");// admin@aha.com
        String user_password = (String)function_data.get("user_password"); // 123

        return "&user_email="+user_email+"&user_phone="+user_phone+"&user_password="+user_password;

    }

}//end of async task




class HttpConnectionService {
    String response = "";
    URL url;
    HttpURLConnection conn = null;
    int responseCode = 0;

    public String sendRequest(String path, HashMap<String, String> params) {
        try {
            Log.d("HttpConnectionService", "Starting process to connect path: " + path);

            // connect tot the url
            url = new URL(path);

            // connect to url
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("connection", "close");//Jellybean is having an issue on "Keep-Alive" connections

            // set timeout to 15 second
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (IOException ioe) {
            Log.d("HttpConnectionService", "Problem in getting connection. => "+ ioe.toString());
            ioe.printStackTrace();
        } catch (Exception e) {
            Log.d("HttpConnectionService", "Problem in getting connection. Safegaurd catch.. => "+ e.toString());
            e.printStackTrace();
        }

        OutputStream os = null;
        try {
            if (null != conn) {
                // get output
                os = conn.getOutputStream();

                // write to the hash map
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();
                responseCode = conn.getResponseCode();
            }
        } catch (IOException e) {
            Log.d("HttpConnectionService", "Problem in getting outputstream and passing parameter.");
            e.printStackTrace();
        }



        // if the connection is fine
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            Log.d("HttpConnectionService", "Connection success to path: " + path);
            String line;
            BufferedReader br = null;

            //getting the reader instance from connection
            try {
                if (null != conn) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }
            } catch (IOException e) {
                Log.d("HttpConnectionService", "Problem with opening reader.");
                e.printStackTrace();
            }

            //reading the response from stream
            try {
                if (null != br) {
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("HttpConnectionService", "output: " + line);
                    }
                }
            } catch (IOException e) {
                response = "";
                Log.d("HttpConnectionService", "Problem in extracting the result.");
                e.printStackTrace();
            }
        } else {
            response = "";
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                Log.d("HttpConnectionService", "entry.Key: " + entry.getKey());
                Log.d("HttpConnectionService", "entry.Value: " + entry.getValue());

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            Log.d("HttpConnectionService", "Problem in getPostDataString while handling params.");
            e.printStackTrace();
            return "";
        }

        return result.toString();
    }
}
