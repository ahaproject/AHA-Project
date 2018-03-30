package project.aha;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.models.User;


public class LoginActivity extends AppCompatActivity implements ReceiveResult{

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Constants.hideKeyboard(this);


        mEmailView = (EditText) findViewById(R.id.email); // textinput email
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password);


        // add action when the user click on signup
        Button signUpBtn = (Button) findViewById(R.id.signup_link);
        signUpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });

        // add action when the user click on login button
        Button loginBtn = (Button) findViewById(R.id.email_sign_in_button);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void attemptLogin() {
        Constants.hideKeyboard(this);
        // Reset errors.

        error.setText("");

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString(); // email or phone
        String password = mPasswordView.getText().toString(); // password

        boolean cancel = false;



        // check if the email or phone is not empty
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_phone_field_required)));
            mEmailView.requestFocus();
            cancel = true;
        }

        // check if the pass is not empty
        else if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            mPasswordView.requestFocus();;
            cancel = true;
        }


        // if all data are filled
        if (!cancel){

            // store data in hash map -> key and value
            HashMap<String,String> data = new HashMap<>();
            data.put(Constants.CODE,Constants.LOGIN+"");
            data.put(Constants.USER_PHONE_META,email);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);

            // send request to connect with database
            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data , Constants.DATABASE_URL);
        }
    }


    @Override // when receive result from database
    public void onReceiveResult(String resultJson) {
        try {

            // get the response as json object which named output
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            // read result ( successful or error )
            String function_result = output.getString(Constants.RESULT);
            Log.d("func result",function_result);


            switch(function_result) {
                case Constants.SCF_LOGIN: // if successful !

                    // get the user data
                    output = output.getJSONArray("data").getJSONObject(0);

                    // convert from json to Java Object
                    User user = readAndSaveUser(output);
                    if(user == null ){
                        Log.d("NULL LOGIN USER" , output.toString());
                        return ;
                    }
                    // attempt login
                    //  1. write data to file
                    //  2. save a global object of the user
                    Constants.login(this , user);

                    break;
                case Constants.ERR_LOGIN: // if there is no record for the user credential
                    error.setText(getString(R.string.error_login));

                    break;

                default: // if there are another ambiguous error
                    error.setText("Error in LOGIN !!!");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private User readAndSaveUser(JSONObject output) {
        try {
            int user_id = Integer.parseInt((String) output.get(Constants.USER_ID_META));
            String user_email = (String) output.get(Constants.USER_EMAIL_META);
            String user_phone = (String) output.get(Constants.USER_PHONE_META);
            String user_name = (String) output.get(Constants.USER_NAME_META);
            int user_type = Integer.parseInt((String) output.get(Constants.USER_TYPE_META));

            return new User(user_id, user_email, user_name, "", user_type, user_phone);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // if any error occurred -> return null
        return null;
    }
}

