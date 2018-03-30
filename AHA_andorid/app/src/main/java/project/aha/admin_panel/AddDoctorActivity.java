package project.aha.admin_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.*;
import project.aha.R;
import project.aha.models.Diagnose;

public class AddDoctorActivity extends AppCompatActivity implements ReceiveResult{

    EditText mEmailView , mPhoneView , mPasswordView , mNameView ;
    Spinner specializes_dropdown_list;
    TextView error ;
    String specialized ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        Constants.showLogo(this);

        Constants.hideKeyboard(this);


        // get views
        mEmailView = (EditText)findViewById(R.id.email);
        mPhoneView = (EditText)findViewById(R.id.phone);
        mPasswordView = (EditText)findViewById(R.id.password);
        mNameView = (EditText)findViewById(R.id.name);
        error = (TextView) findViewById(R.id.error);


        // get the dropdown of specializes
        specializes_dropdown_list = (Spinner)findViewById(R.id.specialized);
        Constants.add_to_spinner(this,specializes_dropdown_list);
        specializes_dropdown_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Diagnose di = (Diagnose) parent.getItemAtPosition(position);
                specialized = di.getName();
//                specialized_id = di.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specialized = null;
            }
        });


        // add action when the admin clicks on add doctor
        Button formAddDoctorBtn = (Button) findViewById(R.id.form_add_doctor_btn);
        formAddDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_new_doctor();
            }
        });
    }

    public void add_new_doctor(){
        Constants.hideKeyboard(this);
        error.setText("");

        // get the text insides the
        String email = mEmailView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;



        // check if the email is not empty
        if (TextUtils.isEmpty(email)) {
            error.setText(getString((R.string.email_field_required)));
            mEmailView.requestFocus();
            cancel = true;
        }


        else if (password.length() <= 0) {
            error.setText(getString(R.string.password_field_required));
            mPasswordView.requestFocus();
            cancel = true;
        }

        else if (TextUtils.isEmpty(name)) {
            error.setText(getString((R.string.name_field_required)));
            mNameView.requestFocus();
            cancel = true;
        }


        // if all required fields are filled
        if (!cancel) {

            // add data to hash map
            HashMap<String,String> data = new HashMap<>();
            data.put(Constants.CODE,Constants.ADD_DOCTOR+"");
            data.put(Constants.USER_PHONE_META,phone);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);
            data.put(Constants.USER_NAME_META,name);
            data.put(Constants.USER_TYPE_META,Constants.DOCTOR_TYPE+"");
            data.put(Constants.DOCTOR_SPECILIZED_META,specialized);


            // send request to database to save this doctor and pass it a data
            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data , Constants.DATABASE_URL);
        }
    }


    @Override // when the database send response
    public void onReceiveResult(String resultJson) {
        Constants.hideKeyboard(this);
        error.setText("");

        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String resultStr = output.getString(Constants.RESULT);

            if(resultStr == null ){
                Toast.makeText(this, "Null result", Toast.LENGTH_LONG).show();
                return;
            }

            // if the data is duplicated
            switch(resultStr){
                case Constants.ERR_DUPLICATE_ACC :{
                    mEmailView.requestFocus();
                    mPhoneView.requestFocus();
                    error.setText(getString(R.string.duplicate_account));
                    break;
                }

                // if it success
                case Constants.SCF_INSERT_DOCTOR :{
                    Toast.makeText(this, getString(R.string.scf_add_doctor), Toast.LENGTH_SHORT).show();
                    break;
                }

                // if there are an error in inserting doctor
                case Constants.ERR_INSERT_DOCTOR :{
                    error.setText("Error insert doctor");
                    break;
                }

                // if there are an error in inserting user
                case Constants.ERR_INSERT_USER :{
                    error.setText( "Error insert user");
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
