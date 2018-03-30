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
import java.util.Map;
public class DatabasePostConnection{


    // from volley API -> because it's fast , not for heavy load
    private RequestQueueSingleton queue;

    private Activity callerActivity;

    private ProgressDialog processDialog;

    public DatabasePostConnection(Activity callerActivity) {
        this.callerActivity = callerActivity;

        this.processDialog = new ProgressDialog(callerActivity);
        processDialog.setMessage(callerActivity.getString(R.string.please_wait));
        processDialog.setCancelable(false);


        queue = RequestQueueSingleton.getInstance(callerActivity.getApplicationContext());
    }


    public void postRequest(final Map<String, String> data , String url){

        processDialog.show();

        // response when volley finish request
        Response.Listener responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);

                // hide
                if (processDialog.isShowing()) {
                    processDialog.dismiss();
                }


                ReceiveResult activity = (ReceiveResult) callerActivity;
                activity.onReceiveResult(response);
            }
        };

        // error
        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // error
                Log.d("Error.Response", volleyError.toString());
                // hide
                if (processDialog.isShowing()) {
                    processDialog.dismiss();
                }

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
            }
        };


        // send request with data
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,responseListener, errorListener ) {
            @Override
            protected Map<String, String> getParams()
            {
                Log.d("Parameters" , data.toString());
                return data;
            }
        };


        // add the request to the queue
        queue.addToRequestQueue(postRequest);
    }



}




// Fixed
class RequestQueueSingleton {
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
