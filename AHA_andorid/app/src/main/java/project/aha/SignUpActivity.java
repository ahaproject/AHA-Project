
package project.aha;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import project.aha.models.User;
import project.aha.parent_panel.ParentMainActivity;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements ReceiveResult{

    // UI references.
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPhone;
    private EditText mPasswordView;
    private EditText mConfPasswordView;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the login form.
        mNameView = (EditText) findViewById(R.id.name); // textinput name
        mEmailView = (EditText) findViewById(R.id.email); // textinput email
        mPhone = (EditText) findViewById(R.id.phone); // textinput phone
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfPasswordView = (EditText) findViewById(R.id.conf_password);

        Button loginBtn = (Button) findViewById(R.id.login_link);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });

        // Author : AHA team
        Button register = (Button) findViewById(R.id.email_sign_up_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void createAccount() {

        // Reset errors.
        mEmailView.setError(null);
        mNameView.setError(null);
        mPhone.setError(null);
        mPasswordView.setError(null);
        mConfPasswordView.setError(null);
        error.setText("");

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString(); // e@hotmail.cp
        String email = mEmailView.getText().toString(); // e@hotmail.cp
        String phone = mPhone.getText().toString(); // e@hotmail.cp
        String conf_password = mConfPasswordView.getText().toString(); // e@hotmail.cp
        String password = mPasswordView.getText().toString(); // 123456

        boolean cancel = false;
        View focusView = null;



        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            focusView = mEmailView;
            cancel = true;
        }

        if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            focusView = mPasswordView;
            cancel = true;
        }

        if (conf_password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.conf_password_field_required)));
            focusView = mConfPasswordView;
            cancel = true;
        }


        if (conf_password.equalsIgnoreCase(password) == false) { // need to check for login
            error.setText(getString((R.string.passwords_dont_match)));
            focusView = mConfPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Author : AHA team

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            HashMap<String,String> data = new HashMap<>();
            data.put("code" , String.valueOf(Constants.SIGNUP));
            data.put(Constants.USER_NAME_META,name);
            data.put(Constants.USER_PHONE_META,phone);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);

//            DatabasePostConnection connect  = new DatabasePostConnection(this);
            connect(data , Constants.DATABASE_URL);

        }
    }
    String dbResponse = "";
    public String connect(final Map<String, String> data , String url){
        RequestQueueSingleton queue = RequestQueueSingleton.getInstance(this.getApplicationContext());

        Response.Listener responseListener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                dbResponse = response;
                receive(response);

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

    @Override
    public void receive(Object result) {
        try {
            if(result == null) {
                Log.d("response", "null value");
                return;
            }
            Log.d("response",result.toString());
            if (result != null) {
                String str = (String) result;

                JSONObject resultJsonObject = new JSONObject(str);
                JSONObject output = resultJsonObject.getJSONObject("output");
               str = (String) output.get(Constants.RESULT);

                switch(str){
                    case Constants.SCF_INSERT_PARENT:
//                        SharedPreferences prefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putInt(Constants.PREF_USER_LOGGED_ID, user.getUser_id());
//                        editor.putInt(Constants.PREF_USER_LOGGED_TYPE, user.getUser_type());
//                        editor.commit();
                        Intent i = new Intent(this,ParentMainActivity.class);
                        startActivity(i); // start main activity
                        finish(); // stop login activity
                        break;
                    case Constants.ERR_DUPLICAT_ACC:
                        error.setText(getString(R.string.duplicate_account));

                        break;
                    
                    default:
                        error.setText("Error in insert parent");
                        break;
                }
                    

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
