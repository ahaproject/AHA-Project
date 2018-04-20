package project.aha.admin_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.aha.*;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Diagnose;

public class AddDoctorActivity extends AppCompatActivity implements ReceiveResult {

    private EditText mEmailView, mPasswordView, mNameView;
    private Spinner specializes_dropdown_list;
    private TextView error;
    private String specialized;
    private int specialized_id;
    private IntlPhoneInput mPhoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        Constants.showLogo(this);

        // get views
        mEmailView = (EditText) findViewById(R.id.email);
        mPhoneView = (IntlPhoneInput) findViewById(R.id.phone);
        mPasswordView = (EditText) findViewById(R.id.password);
        mNameView = (EditText) findViewById(R.id.name);
        error = (TextView) findViewById(R.id.error);


        // get the dropdown of specializes
        specializes_dropdown_list = (Spinner) findViewById(R.id.specialized);
        if(Constants.diagnoses != null && !Constants.diagnoses.isEmpty()){
            ArrayList<Diagnose> diagnoses = new ArrayList<>(Constants.diagnoses.values());
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_style, diagnoses );
            specializes_dropdown_list.setAdapter(spinnerAdapter);

            // set action when user clicks on specialize
            specializes_dropdown_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Diagnose di = (Diagnose) parent.getItemAtPosition(position);
                    specialized = di.getName();
                    specialized_id = di.getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    specialized = null;
                }
            });
        }



        // add action when the admin clicks on add doctor
        Button formAddDoctorBtn = (Button) findViewById(R.id.form_add_doctor_btn);
        formAddDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_new_doctor();
            }
        });
    }

    public void add_new_doctor() {
        error.setText("");

        // get the text inside the email
        String email = mEmailView.getText().toString();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);

        // get the text insides the password
        String password = mPasswordView.getText().toString();

        // get the text insides the name
        String name = mNameView.getText().toString();


        // check if the email is not empty
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            mEmailView.requestFocus();
        } else if (!matcher.matches()) { // check if the emails format is correct
            error.setText(getString((R.string.incorrect_email)));
            mEmailView.requestFocus();
        } else if (TextUtils.isEmpty(mPhoneView.getText())) { // check if the phone is not empty
            // Check for a empty phone .
            error.setText(getString((R.string.field_required)));
            mPasswordView.requestFocus();
        } else if (password.length() <= 0) { // check if the password is not empty
            error.setText(getString(R.string.password_field_required));
            mPasswordView.requestFocus();
        } else if (password.length() < 8) { // check if the password is more than 8 characters
            error.setText(getString((R.string.pwd_8_char)));
            mPasswordView.requestFocus();
        } else if (!mPhoneView.isValid()) { // check if the phone is valid
            // Check for a valid phone .
            error.setText(getString((R.string.enter_valid_phone)));
            mPhoneView.requestFocus();
        } else if (TextUtils.isEmpty(name)) { // check if the name is not empty
            error.setText(getString((R.string.name_field_required)));
            mNameView.requestFocus();
        }


        // if all required fields are filled
        else {
            String phone = mPhoneView.getText().toString();


            // add data to hash map
            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CODE, Constants.ADD_DOCTOR + "");
            data.put(Constants.USER_PHONE_META, phone);
            data.put(Constants.USER_EMAIL_META, email);
            data.put(Constants.USER_PASSWORD_META, password);
            data.put(Constants.USER_NAME_META, name);
            data.put(Constants.USER_TYPE_META, Constants.DOCTOR_TYPE + "");
            data.put(Constants.DOCTOR_SPECILIZED_META, specialized_id + "");


            // send request to database to save this doctor and pass it a data
            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data, Constants.DATABASE_URL);
        }
    }


    @Override // when the database send response
    public void onReceiveResult(String resultJson) {
        error.setText("");

        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String resultStr = output.getString(Constants.RESULT);

            switch (resultStr) {

                case Constants.ERR_DUPLICATE_ACC: {  // if the data is exist before

                    mEmailView.requestFocus();
                    mPhoneView.requestFocus();
                    error.setText(getString(R.string.duplicate_account));
                    break;
                }

                // if it success
                case Constants.SCF_INSERT_DOCTOR: {
                    Toast.makeText(this, getString(R.string.scf_add_doctor), Toast.LENGTH_SHORT).show();
                    break;
                }

                // if there are an error in inserting doctor
                case Constants.ERR_INSERT_DOCTOR: {
                    error.setText("Error insert doctor");
                    break;
                }

                // if there are an error in inserting user
                case Constants.ERR_INSERT_USER: {
                    error.setText("Error insert user");
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // top bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        if(Constants.get_current_user_type(this) == Constants.ADMIN_TYPE){
            menu.findItem(R.id.chat_activity).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);
    }
}
