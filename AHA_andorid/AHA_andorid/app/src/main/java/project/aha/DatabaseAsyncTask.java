package project.aha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import project.aha.admin_panel.AddDoctorActivity;
import project.aha.admin_panel.DeletDoctorActivity;
import project.aha.admin_panel.DeleteParentActivity;
import project.aha.models.Doctor;
import project.aha.models.Parent;
import project.aha.models.User;

public class DatabaseAsyncTask extends AsyncTask<Void, Void, Object> { // class to connect to database in background


    private String apiPath = Constants.DATABASE_URL;

    //status of success / 0 failed, 1 success
    private int success = 0;


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
    public DatabaseAsyncTask(Context context, Activity activity, int function_code, HashMap<Object, Object> function_data) {
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
        processDialog.setMessage(mContext.getString(R.string.please_wait));
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
        apiPath += getFunctionURL(function_code);

        // just print the url to see the result in the log
        Log.d("URL Path", apiPath);

        // connect to the database
        HttpConnectionService service = new HttpConnectionService();

        // send the url we want and post meta
        response = service.sendRequest(apiPath, postDataParams);



        try {
            // set the result to success
            success = 1;

            // get the json array from the response
            JSONObject resultJsonObject = new JSONObject(response);


            switch(function_code){

                case Constants.LOGIN : {
                    // get object named output
                    JSONArray restulJsonArray = resultJsonObject.getJSONArray("output");

                    if (null != restulJsonArray) {
                        for (int i = 0; i < restulJsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                                int user_id = Integer.parseInt((String) jsonObject.get(Constants.USER_ID_META));
                                String user_email = (String) jsonObject.get(Constants.USER_EMAIL_META);
                                String user_phone = (String) jsonObject.get(Constants.USER_PHONE_META);
                                String user_password = (String) jsonObject.get(Constants.USER_PASSWORD_META);
                                String user_name = (String) jsonObject.get(Constants.USER_NAME_META);
                                int user_type = Integer.parseInt((String) jsonObject.get(Constants.USER_TYPE_META));
                                //     public User(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone) {
                                Constants.currentUser = new User(user_id, user_email, user_name, user_password, user_type, user_phone);
                                return Constants.currentUser;


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                case Constants.ADD_DOCTOR :{
                    JSONObject obj = resultJsonObject.getJSONObject("output");
                    String result = (String) obj.get(Constants.RESULT);
                    return result;
                }

                case Constants.LIST_DOCTORS :{
                    ArrayList<Doctor> doctors = new ArrayList<>();
                    JSONArray restulJsonArray = resultJsonObject.getJSONArray("output");
                    if (null != restulJsonArray) {
                        for (int i = 0; i < restulJsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                                int user_id = Integer.parseInt((String) jsonObject.get(Constants.USER_ID_META));
                                String user_email = (String) jsonObject.get(Constants.USER_EMAIL_META);
                                String user_phone = (String) jsonObject.get(Constants.USER_PHONE_META);
                                String user_name = (String) jsonObject.get(Constants.USER_NAME_META);
                                String specialized = (String) jsonObject.get(Constants.DOCTOR_SPECILIZED_META);
                                doctors.add(new Doctor(user_id, user_email, user_name, "", Constants.DOCTOR_TYPE, user_phone , specialized));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return doctors;
                }

                case Constants.DELETE_DOCTOR :{
                    JSONObject obj = resultJsonObject.getJSONObject("output");
                    String result = (String) obj.get(Constants.RESULT);
                    return result;
                }

                case Constants.LIST_PARENTS :{
                    ArrayList<Parent> parents = new ArrayList<>();
                    JSONArray restulJsonArray = resultJsonObject.getJSONArray("output");
                    if (null != restulJsonArray) {
                        for (int i = 0; i < restulJsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                                int user_id = Integer.parseInt((String) jsonObject.get(Constants.USER_ID_META));
                                String user_name = (String) jsonObject.get(Constants.USER_NAME_META);
                                String fileNumber = (String) jsonObject.get(Constants.PARENT_FILE_NUMBER);
                                parents.add(new Parent(user_id, user_name, Constants.PARENT_TYPE,fileNumber));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return parents;
                }

                case Constants.DELETE_PARENT :{
                    JSONObject obj = resultJsonObject.getJSONObject("output");
                    String result = (String) obj.get(Constants.RESULT);
                    return result;
                }

            }
        } catch (JSONException e) {
            // if any error set to failed
            success = 0;
            e.printStackTrace();
        }
        return null;
    }



    @Override // after
    protected void onPostExecute(Object result) { // login -> User
        super.onPostExecute(result);

        // dismiss progress dialog
        if (processDialog.isShowing()) {
            processDialog.dismiss();
        }

        switch(function_code){

            case Constants.LOGIN : {
                LoginActivity loginActivity = ((LoginActivity)mActivity);
                loginActivity.doLogin(result);
                break;
            }

            case Constants.ADD_DOCTOR :{
                AddDoctorActivity ada = ((AddDoctorActivity)mActivity);
                ada.printResult(result);
                break;
            }

            case Constants.LIST_DOCTORS :{
                DeletDoctorActivity dda = ((DeletDoctorActivity)mActivity);
                dda.fillDoctorsArray(result);
                break;
            }

            case Constants.DELETE_DOCTOR :{
                DeletDoctorActivity dda = ((DeletDoctorActivity)mActivity);
                dda.showDeletionResult(result);
                break;
            }


            case Constants.LIST_PARENTS :{
                DeleteParentActivity dda = ((DeleteParentActivity)mActivity);
                dda.fillParentsArray(result);
                break;
            }

            case Constants.DELETE_PARENT :{
                DeleteParentActivity dda = ((DeleteParentActivity)mActivity);
                dda.showDeletionResult(result);
                break;
            }

        }

    }


    private String getFunctionURL(int function_code) {
        String url = "?code="+function_code;
        switch(function_code){

            case Constants.LOGIN :{
                String user_email = (String)function_data.get(Constants.USER_EMAIL_META);// admin@aha.com
                String user_phone = (String)function_data.get(Constants.USER_PHONE_META);// admin@aha.com
                String user_password = (String)function_data.get(Constants.USER_PASSWORD_META); // 123

                url += "&"+Constants.USER_EMAIL_META+"="+user_email+"&"+Constants.USER_PHONE_META+"="+user_phone+
                        "&"+Constants.USER_PASSWORD_META+"="+user_password;
                break;
            }

            case Constants.ADD_DOCTOR:{
                String user_email = (String)function_data.get(Constants.USER_EMAIL_META);// admin@aha.com
                String user_phone = (String)function_data.get(Constants.USER_PHONE_META);// admin@aha.com
                String user_password = (String)function_data.get(Constants.USER_PASSWORD_META); // 123
                String user_name = (String)function_data.get(Constants.USER_NAME_META); // 123
                String doctor_specialized = (String)function_data.get(Constants.DOCTOR_SPECILIZED_META); // 123


                url += "&"+Constants.USER_EMAIL_META+"="+user_email+"&"+Constants.USER_PHONE_META+"="+user_phone+
                        "&"+Constants.USER_PASSWORD_META+"="+user_password+"&"+Constants.USER_NAME_META+"="+user_name+
                        "&"+Constants.DOCTOR_SPECILIZED_META+"="+doctor_specialized;
                break;
            }

            case Constants.LIST_DOCTORS:
            case Constants.LIST_PARENTS:
                break;


            case Constants.DELETE_DOCTOR:
            case Constants.DELETE_PARENT:
                int user_id = (Integer)function_data.get(Constants.USER_ID_META);
                url += "&"+Constants.USER_ID_META+"="+user_id;
                break;


        }

        return url ;
    }





    // -----------------------------------------------------------------------------
    // ---------------      CLASS OF HTTP CONNECTION       -------------------------
    // -----------------------------------------------------------------------------
    private class HttpConnectionService {
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

}//end of async task




