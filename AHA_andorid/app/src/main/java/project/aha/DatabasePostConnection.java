package project.aha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.*;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import project.aha.interfaces.ReceiveResult;
import project.aha.models.Diagnose;

public class DatabasePostConnection {


    // from volley API -> because it's fast , not for heavy load
    private RequestQueueSingleton queue;
    private ProgressDialog progressDialog;
    private Activity callerActivity;

    private boolean showDialog;
    private boolean runOActivity;


    private int code;

    public DatabasePostConnection(Activity callerActivity) {
        this.callerActivity = callerActivity;
        progressDialog = new ProgressDialog(callerActivity);
        progressDialog.setMessage(callerActivity.getString(R.string.please_wait));
        progressDialog.setCancelable(true);
        queue = RequestQueueSingleton.getInstance(callerActivity.getApplicationContext());
        showDialog = true;
        runOActivity = true;
    }

    public DatabasePostConnection(Activity callerActivity, boolean showDialog, int code , boolean runOActivity) {
        this.callerActivity = callerActivity;
        this.showDialog = showDialog;
        this.code = code;
        if (showDialog) {
            progressDialog = new ProgressDialog(callerActivity);
            progressDialog.setMessage(callerActivity.getString(R.string.please_wait));
            progressDialog.setCancelable(true);
        }
        queue = RequestQueueSingleton.getInstance(callerActivity.getApplicationContext());

        this.runOActivity = runOActivity;
    }


    public void postRequest(final Map<String, String> data, String url) {
        if (showDialog)
            progressDialog.show();


        // response when volley finish request
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);

                // hide
                if (showDialog)
                    progressDialog.hide();

                if (code == Constants.LIST_DIAGNOSES) {
                    listDiagnoses(response);
                }

                if (code == Constants.REPORT_ABUSE) {
                    reportAbuse(response);
                }


                if (runOActivity) {
                    ReceiveResult activity = (ReceiveResult) callerActivity;
                    activity.onReceiveResult(response);
                }
            }
        };

        // error
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // error
                Log.d("Error.Response", volleyError.toString());
                // hide
                if (showDialog)
                    progressDialog.hide();


                if (volleyError instanceof NetworkError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof ServerError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof AuthFailureError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.auth_failure_error), Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof ParseError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.parse_error), Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.no_connection_error), Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(callerActivity, callerActivity.getString(R.string.timout_error), Toast.LENGTH_SHORT).show();
                }

                if (callerActivity instanceof SplashScreen) {
                    Constants.logout(callerActivity);
                }
//                callerActivity.startActivity(new Intent(callerActivity , LoginActivity.class));
            }
        };


        // send request with data
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Log.d("Parameters", data.toString());
                return data;
            }
        };


        // add the request to the queue
        queue.addToRequestQueue(postRequest);

    }

    private void reportAbuse(String response) {
        try {
            JSONObject output = new JSONObject(response).getJSONObject("output");
            String resultStr = output.getString(Constants.RESULT);

            if (resultStr == null) {
                Log.d("JSON Result", "Null result");
                return;
            }

            // if the data is duplicated
            switch (resultStr) {

                case Constants.RECORDS: {
                    JSONArray doctorsJSONArray = output.getJSONArray("data");
                    for (int i = 0; i < doctorsJSONArray.length(); i++) {
                        JSONObject jsonObject = doctorsJSONArray.getJSONObject(i);

                        int diag_id = jsonObject.getInt("diag_id");
                        String diag_name = jsonObject.getString("diag_name");
                        Constants.addDiagnose(new Diagnose(diag_id, diag_name));
                    }
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listDiagnoses(String response) {
        try {
            JSONObject output = new JSONObject(response).getJSONObject("output");
            String resultStr = output.getString(Constants.RESULT);

            if (resultStr == null) {
                Log.d("JSON Result", "Null result");
                return;
            }

            // if the data is duplicated
            switch (resultStr) {

                case Constants.RECORDS: {
                    JSONArray doctorsJSONArray = output.getJSONArray("data");
                    for (int i = 0; i < doctorsJSONArray.length(); i++) {
                        JSONObject jsonObject = doctorsJSONArray.getJSONObject(i);

                        int diag_id = jsonObject.getInt("diag_id");
                        String diag_name = jsonObject.getString("diag_name");
                        Constants.addDiagnose(new Diagnose(diag_id, diag_name));
                    }
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Fixed
    static class RequestQueueSingleton {
        private static RequestQueueSingleton mInstance;
        private RequestQueue mRequestQueue;
        private Context mCtx;
        private int MY_SOCKET_TIMEOUT_MS = 15000;

        private RequestQueueSingleton(Context context) {
            mCtx = context;
            mRequestQueue = getRequestQueue();
        }

        public static synchronized RequestQueueSingleton getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new RequestQueueSingleton(context);
            }
            return mInstance;
        }

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            }
            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            getRequestQueue().add(req);
        }
    }


}






