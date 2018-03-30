package project.aha;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
public class DatabasePostConnection extends AsyncTask<Object, Void, Object> {

    private RequestQueueSingleton queue;

    private Activity callerActivity;

    private String dbResponse ;

    private ProgressDialog processDialog;

    public DatabasePostConnection(Activity callerActivity) {
        this.callerActivity = callerActivity;
        queue = RequestQueueSingleton.getInstance(callerActivity.getApplicationContext());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        processDialog = new ProgressDialog(callerActivity);
        processDialog.setMessage(callerActivity.getString(R.string.please_wait));
        processDialog.setCancelable(false);
        processDialog.show();
    }
    @Override
    protected Object doInBackground(Object... params) {
        HashMap<String,String> data = (HashMap<String,String>)params[0];
        String url = (String )params[1];
        String result = connect(data,url);

        return result;
    }
    @Override // after
    protected void onPostExecute(Object result) { // login -> User
        super.onPostExecute(result);

        // dismiss progress dialog
        if (processDialog.isShowing()) {
            processDialog.dismiss();
        }


        ReceiveResult activity = (ReceiveResult) callerActivity;
        activity.receive(result);
    }






    public String connect(final Map<String, String> data , String url){
        Response.Listener responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.d("Error.Response", response);
                dbResponse = response;
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());
            }
        };


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,responseListener, errorListener ) {
            @Override
            protected Map<String, String> getParams()
            {
                Log.d("Parameters" , data.toString());
                return data;
            }
        };
        queue.addToRequestQueue(postRequest);

        return dbResponse;
    }



}


class RequestQueueSingleton {
    private static RequestQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

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
        getRequestQueue().add(req);
    }
}
