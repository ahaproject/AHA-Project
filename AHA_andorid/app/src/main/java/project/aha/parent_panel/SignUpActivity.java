
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

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.LoginActivity;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.User;

/**
 * A signup screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements ReceiveResult {

    // UI references.
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
//        mPhone = (IntlPhoneInput) findViewById(R.id.phone); // textinput phone
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password); // password textinput
        mConfPasswordView = (EditText) findViewById(R.id.conf_password); // confirm textinput


//        String swissNumberStr = "044 668 18 00";
//        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//        try {
//            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, "CH");
//            mPhone.setNumber(phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
//        } catch (NumberParseException e) {
//            System.err.println("NumberParseException was thrown: " + e.toString());
//        }


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
        String conf_password = mConfPasswordView.getText().toString(); // e@hotmail.cp
        String password = mPasswordView.getText().toString(); // 123456



        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            mEmailView.requestFocus();
        }

        else if(TextUtils.isEmpty(mPhone.getText())) {
            // Check for a empty phone .
            error.setText(getString((R.string.field_required)));
            mPasswordView.requestFocus();

        }

        else if(!mPhone.isValid()) {
            // Check for a valid phone .
            error.setText(getString((R.string.enter_valid_phone)));
            mPhone.requestFocus();

        }
        else if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            mPasswordView.requestFocus();
        } else if (conf_password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.conf_password_field_required)));
            mConfPasswordView.requestFocus();
        } else if (conf_password.equalsIgnoreCase(password) == false) { // need to check for login
            error.setText(getString((R.string.passwords_dont_match)));
            mPasswordView.requestFocus();
            mConfPasswordView.requestFocus();

        } else {
            String phone = mPhone.getNumber(); // e@hotmail.cp

            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CODE, String.valueOf(Constants.SIGNUP));
//            data.put(key,value);
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
            Log.d("response", resultJson.toString());
            if (resultJson != null) {

                JSONObject output = new JSONObject(resultJson).getJSONObject("output");
                String function_result = output.getString(Constants.RESULT);
                Log.d("func result", function_result);


                switch (function_result) {
                    case Constants.SCF_LOGIN:
//                        output = output.getJSONArray("data").getJSONObject(0);

//                        int user_id = Integer.parseInt((String) output.get(Constants.USER_ID_META));
//                        String user_email = (String) output.get(Constants.USER_EMAIL_META);
//                        String user_phone = output.optString(Constants.USER_PHONE_META, "");
//                        String user_name = output.optString(Constants.USER_NAME_META, "");
//                        int user_type = Integer.parseInt((String) output.get(Constants.USER_TYPE_META));
//                        User user = Constants.currentUser = new User(user_id, user_email, user_name, user_type, user_phone);
                        User user = Constants.updateUser(this,output);

                        String ACCOUNT_SID = "AC73fb94393f2250f0d9e6f72da99bd409";
                         String AUTH_TOKEN = "3aaaa64fe9b73c8e98c1fff24fc460cb";
//
//                        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//                        Message message = Message.creator(
//                                new PhoneNumber(user.getUser_phone()),  // To number
//                                new PhoneNumber("+16037829547"),  // From number
//                                "Hi "+user.getUser_name()+" id is "+user.getUser_id()                    // SMS body
//                        ).create();
//
//                        Log.i("trilio" , "msg id : "+message.getSid());
//                        Constants.login(this , user);
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
