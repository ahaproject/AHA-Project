
package project.aha.parent_panel;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.LoginActivity;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.User;

public class SignUpActivity extends AppCompatActivity implements ReceiveResult {

    private EditText mNameView;
    private EditText mEmailView;
    private IntlPhoneInput mPhone;
    private EditText mPasswordView;
    private EditText mConfPasswordView;
    private TextView error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Constants.showLogo(this);


        mNameView = (EditText) findViewById(R.id.name); // textinput name
        mEmailView = (EditText) findViewById(R.id.email); // textinput email
        mPhone = (IntlPhoneInput) findViewById(R.id.phone); // textinput phone
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password); // password textinput
        mConfPasswordView = (EditText) findViewById(R.id.conf_password); // confirm textinput


        // add action when click on already have account
        Button loginBtn = (Button) findViewById(R.id.login_link);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

//        add action when user click on signup
        Button register = (Button) findViewById(R.id.email_sign_up_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }


    /**
     * Attempts to register the account specified by the signup form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void createAccount() {

        // Reset errors.
        error.setText("");

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString(); // e@hotmail.cp
        String email = mEmailView.getText().toString(); // e@hotmail.cp
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);


        String conf_password = mConfPasswordView.getText().toString(); // e@hotmail.cp
        String password = mPasswordView.getText().toString(); // 123456


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            mEmailView.requestFocus();
        } else if (!matcher.matches()) {
            error.setText(getString((R.string.incorrect_email)));
            mEmailView.requestFocus();
        } else if (TextUtils.isEmpty(mPhone.getText())) {
            // Check for a empty phone .
            error.setText(getString((R.string.field_required)));
            mPasswordView.requestFocus();

        } else if (!mPhone.isValid()) {
            // Check for a valid phone .
            error.setText(getString((R.string.enter_valid_phone)));
            mPhone.requestFocus();

        } else if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            mPasswordView.requestFocus();
        } else if (conf_password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.conf_password_field_required)));
            mConfPasswordView.requestFocus();
        } else if (password.length() < 8) { // need to check for login
            error.setText(getString((R.string.pwd_8_char)));
            mConfPasswordView.requestFocus();
        } else if (conf_password.equalsIgnoreCase(password) == false) { // need to check for login
            error.setText(getString((R.string.passwords_dont_match)));
            mPasswordView.requestFocus();
            mConfPasswordView.requestFocus();

        } else {
            String phone = mPhone.getNumber(); // e@hotmail.cp

            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CODE, String.valueOf(Constants.SIGNUP));
            data.put(Constants.USER_NAME_META, name);
            data.put(Constants.USER_PHONE_META, phone);
            data.put(Constants.USER_EMAIL_META, email);
            data.put(Constants.USER_PASSWORD_META, password);

            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data, Constants.DATABASE_URL);
        }
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {
            if (resultJson != null) {

                JSONObject output = new JSONObject(resultJson).getJSONObject("output");
                String function_result = output.getString(Constants.RESULT);
                Log.d("func result", function_result);


                switch (function_result) {
                    case Constants.SCF_LOGIN:
                        User user = Constants.updateUser(this, output);
                        Constants.login(this, user);

                        break;
                    case Constants.ERR_DUPLICATE_ACC:
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
