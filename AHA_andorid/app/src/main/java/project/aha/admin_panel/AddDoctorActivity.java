package project.aha.admin_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.ArrayAdapter;

import java.util.HashMap;

import project.aha.*;
import project.aha.R;

public class AddDoctorActivity extends AppCompatActivity {

    EditText mEmailView , mPhoneView , mPasswordView , mNameView ;
    Spinner spinnerSpecializes;

    String specialized ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        mEmailView = (EditText)findViewById(R.id.email);
        mPhoneView = (EditText)findViewById(R.id.phone);
        mPasswordView = (EditText)findViewById(R.id.password);
        mNameView = (EditText)findViewById(R.id.name);

        spinnerSpecializes = (Spinner)findViewById(R.id.specialized);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.SPECIALIZES_ARRAY);
        spinnerSpecializes.setAdapter(adapter);
        spinnerSpecializes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialized = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specialized = null;
            }
        });


        Button formAddDoctorBtn = (Button) findViewById(R.id.form_add_doctor_btn);
        formAddDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_new_doctor();
            }
        });
    }

    public void add_new_doctor(){
        String email = mEmailView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;



        if (TextUtils.isEmpty(email)) {
//            error.setText(getString((R.string.email_field_required)));
            focusView = mEmailView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(phone)) {
//            error.setText(getString((R.string.email_field_required)));
            focusView = mPhoneView;
            cancel = true;
        }

        else if (password.length() <= 0) { // need to check for login
//            error.setText(getString((R.string.password_field_required)));
            focusView = mPasswordView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(name)) {
//            error.setText(getString((R.string.email_field_required)));
            focusView = mNameView;
            cancel = true;
        }


        if (cancel) {
            // form field with an error.
            focusView.requestFocus();
        } else {
            // https://ahaproject.000webhostapp.com/?code=1&user_email=doc@aha.com&user_phone=05555&user_password=123&user_name=doctor_ahmed&doctor_specialized=speaking
            HashMap<Object,Object> data = new HashMap<>();
            data.put(Constants.USER_PHONE_META,phone);
            data.put(Constants.USER_EMAIL_META,email);
            data.put(Constants.USER_PASSWORD_META,password);
            data.put(Constants.USER_NAME_META,name);
            data.put(Constants.USER_TYPE_META,Constants.DOCTOR_TYPE);
            data.put(Constants.DOCTOR_SPECILIZED_META,specialized);

            new DatabaseAsyncTask(this, this, Constants.ADD_DOCTOR , data).execute();
        }
    }

    public void printResult(Object result) {
        String resultStr = (String)result;

        if(resultStr == null ){
            Toast.makeText(this, "Null result", Toast.LENGTH_LONG).show();
            return;
        }

        switch(resultStr){
            case Constants.ERR_DUPLICAT_ACC :{
                mEmailView.requestFocus();
                mPhoneView.requestFocus();
                Toast.makeText(this, getString(R.string.duplicate_account), Toast.LENGTH_LONG).show();
                break;
            }

            case Constants.SCF_INSERT_DOCTOR :{
                Toast.makeText(this, getString(R.string.scf_add_doctor), Toast.LENGTH_LONG).show();
                break;
            }

            case Constants.ERR_INSERT_DOCTOR :{
                Toast.makeText(this, "Error insert doctor", Toast.LENGTH_LONG).show();
                break;
            }

            case Constants.ERR_INSERT_USER :{
                Toast.makeText(this, "Error insert user", Toast.LENGTH_LONG).show();
                break;
            }

        }
    }

}
