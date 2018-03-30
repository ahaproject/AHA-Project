package project.aha;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import project.aha.admin_panel.AdminMainActivity;
import project.aha.doctor_panel.DoctorMainActivity;
import project.aha.models.User;
import project.aha.parent_panel.ParentMainActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView error;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email); // textinput email
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password);

        Button signUpBtn = (Button) findViewById(R.id.signup_link);
        signUpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });

        // Author : AHA team
        Button loginBtn = (Button) findViewById(R.id.email_sign_in_button);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }


    public void showErrors(){
        error.setText(getString(R.string.error_login));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        error.setText("");

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString(); // e@hotmail.cp
        String password = mPasswordView.getText().toString(); // 123456

        boolean cancel = false;
        View focusView = null;



        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_phone_field_required)));
            focusView = mEmailView;
            cancel = true;
        }

        if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            focusView = mPasswordView;
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
            HashMap<Object,Object> data = new HashMap<>();
            data.put(Constants.USER_PHONE_META,email);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);

            new DatabaseAsyncTask(this, this, Constants.LOGIN , data).execute();
        }
    }

    public void signupBtn(View v){

    }

    public void doLogin(Object result) {
        if (result != null) {
            User user = (User)result;
            SharedPreferences prefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constants.PREF_USER_LOGGED_ID, user.getUser_id());
            editor.putInt(Constants.PREF_USER_LOGGED_TYPE, user.getUser_type());
            editor.commit();

            Intent i = null ;
            switch (user.getUser_type()){
                case Constants.ADMIN_TYPE :{
                    // if user admin type 0
                    // intent -> adminMainActivity
                     i = new Intent(this,AdminMainActivity.class);

                    break;
                }

                case Constants.DOCTOR_TYPE:{
                    // if user admin type 1
                    // intent -> DoctorMainActivity
                     i = new Intent(this,DoctorMainActivity.class);
                    break;
                }
                case Constants.PARENT_TYPE:{
                    // if user admin type 2
                    // intent -> ParentMainActivity
                     i = new Intent(this,ParentMainActivity.class);
                    break;
                }
            }
            startActivity(i); // start main activity
            finish(); // stop login activity

        } else {

            showErrors();

        }
    }
}

