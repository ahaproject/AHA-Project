package project.aha.parent_panel;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;

public class CompleteRegister extends AppCompatActivity implements ReceiveResult {


    // date of registration

    private Calendar calender;
    private EditText childBirthDate;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_registration);
        Constants.showLogo(this);


        // calender picker

        calender = Calendar.getInstance();

        childBirthDate = (EditText) findViewById(R.id.child_bdate);
        childBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CompleteRegister.this,
                        new OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                calender.set(Calendar.YEAR, year);
                                calender.set(Calendar.MONTH, monthOfYear);
                                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel();
                            }

                        },
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });


        // save btn
        saveBtn = (Button) findViewById(R.id.save_complete_registr);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

    }

    private void saveData() {


        HashMap<String, String> data = new HashMap<>();

        LinearLayout form = (LinearLayout) findViewById(R.id.complete_registration_form);
        int count = form.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = form.getChildAt(i);


            // deal with edit texts
            if (o instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) o;
                FrameLayout fl = (FrameLayout) til.getChildAt(0);
                EditText et = (EditText) fl.getChildAt(0);
                String text = et.getText().toString();
                if (text != null && text.length() > 0) {
                    data.put(til.getHint().toString(), text);
                }
            }

            // deal with edit texts
            if (o instanceof EditText) {
                EditText et = (EditText) o;
                String text = et.getText().toString();
                if (text != null && text.length() > 0) {
                    data.put(et.getHint().toString(), text);
                }
            }

            // deal with radio buttons
            if (o instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) o;
                if(checkBox.isChecked()){
                    data.put(checkBox.getText().toString() , "yes");
                } else{
                    data.put(checkBox.getText().toString() , "no");
                }
            }



            // deal with radio buttons
            if (o instanceof LinearLayout) {
                LinearLayout lin = (LinearLayout) o;
                int linCount = lin.getChildCount();
                for (int j = 0; j < linCount; j++) {
                    o = lin.getChildAt(j);
                    if (o instanceof RadioGroup) {
                        TextView tv = (TextView) lin.getChildAt(j - 1);
                        RadioGroup rg = (RadioGroup) o;
                        int choice = rg.getCheckedRadioButtonId();
                        if (choice > -1) {
                            View radioButton = rg.findViewById(choice);
                            int radioId = rg.indexOfChild(radioButton);
                            RadioButton btn = (RadioButton) rg.getChildAt(radioId);
                            String selection = (String) btn.getText();
                            data.put(tv.getText().toString(), selection);// hashmap
                        }
                    }
                }
            }
        }

        DatabasePostConnection con = new DatabasePostConnection(this);
        data.put(Constants.CODE , Constants.COMPLETE_REGISTRATION+"");
        data.put(Constants.USER_ID_META , Constants.get_current_user_id(this)+"");
        con.postRequest(data , Constants.DATABASE_URL);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        childBirthDate.setText(sdf.format(calender.getTime()));
    }


    @Override
    public void onReceiveResult(String resultJson) {

        Log.d("RESULT JSON" , resultJson);

        if(resultJson == null || resultJson.length() < 1){
            Log.d("RESULT JSON" , "NULL RESULT");
            return;
        }
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch (result) {
                case Constants.SCF_COMPLETE_REGITER: {
                    Toast.makeText(this, getString(R.string.saved_success), Toast.LENGTH_LONG).show();
                }

                default:{
                    Log.d("error in update " ,result);
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}


