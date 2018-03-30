
package project.aha;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.models.User;

/**
 * A signup screen that offers login via email/password.
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
        Constants.hideKeyboard(this);



        mNameView = (EditText) findViewById(R.id.name); // textinput name
        mEmailView = (EditText) findViewById(R.id.email); // textinput email
        mPhone = (EditText) findViewById(R.id.phone); // textinput phone
        error = (TextView) findViewById(R.id.error); // error message text view
        mPasswordView = (EditText) findViewById(R.id.password); // password textinput
        mConfPasswordView = (EditText) findViewById(R.id.conf_password); // confirm textinput


        // add action when click on already have account
        Button loginBtn = (Button) findViewById(R.id.login_link);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
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
        Constants.hideKeyboard(this);

        // Reset errors.
        error.setText("");

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString(); // e@hotmail.cp
        String email = mEmailView.getText().toString(); // e@hotmail.cp
        String phone = mPhone.getText().toString(); // e@hotmail.cp
        String conf_password = mConfPasswordView.getText().toString(); // e@hotmail.cp
        String password = mPasswordView.getText().toString(); // 123456

        boolean cancel = false;




        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            mEmailView.requestFocus();
            cancel = true;
        }

        else if (password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.password_field_required)));
            mPasswordView.requestFocus();
            cancel = true;
        }

        else if (conf_password.length() <= 0) { // need to check for login
            error.setText(getString((R.string.conf_password_field_required)));
            mConfPasswordView.requestFocus();
            cancel = true;
        }


        else if (conf_password.equalsIgnoreCase(password) == false) { // need to check for login
            error.setText(getString((R.string.passwords_dont_match)));
            mPasswordView.requestFocus();
            mConfPasswordView.requestFocus();

            cancel = true;
        }



        if (!cancel){

            HashMap<String,String> data = new HashMap<>();
            data.put(Constants.CODE , String.valueOf(Constants.SIGNUP));
//            data.put(key,value);
            data.put(Constants.USER_NAME_META,name);
            data.put(Constants.USER_PHONE_META,phone);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);
            
            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data , Constants.DATABASE_URL);

        }
    }
    
    @Override
    public void onReceiveResult(String resultJson) {
        try {
            Log.d("response",resultJson.toString());
            if (resultJson != null) {

                JSONObject output = new JSONObject( resultJson).getJSONObject("output");
                String function_result = output.getString(Constants.RESULT);
                Log.d("func result",function_result);


                switch(function_result){
                    case Constants.SCF_INSERT_PARENT:
                        output = output.getJSONArray("data").getJSONObject(0);

                        int user_id = Integer.parseInt((String) output.get(Constants.USER_ID_META));
                        String user_email = (String) output.get(Constants.USER_EMAIL_META);
                        String user_phone = (String) output.get(Constants.USER_PHONE_META);
                        String user_password = (String) output.get(Constants.USER_PASSWORD_META);
                        String user_name = (String) output.get(Constants.USER_NAME_META);
                        int user_type = Integer.parseInt((String) output.get(Constants.USER_TYPE_META));
                        //     public User(int user_id, String user_email, String user_name, String user_password, int user_type, String user_phone) {
                        User user = Constants.currentUser = new User(user_id, user_email, user_name, user_password, user_type, user_phone);

                        Constants.login(this,user);

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
