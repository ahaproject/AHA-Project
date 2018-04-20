package project.aha.parent_panel;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Parent;

public class CompleteRegister extends AppCompatActivity implements ReceiveResult {


    // date of registration

    private Calendar calender;
    private TextView childBirthDate;
    private Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_registration);
        Constants.showLogo(this);

        Parent parent = (Parent) Constants.get_user_object(this);
        if (parent.getMetas() == null || parent.getMetas().size() == 0) {

        } else {
            HashMap<String, String> parentData = parent.getMetas();
            fillData(parentData);
        }

        // calender picker
        if (Build.VERSION.SDK_INT >= 24) {
            calender = Calendar.getInstance();

            childBirthDate = (TextView) findViewById(R.id.child_bdate);
            childBirthDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 24) {

                        new DatePickerDialog(CompleteRegister.this,
                                new OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                          int dayOfMonth) {
                                        if (Build.VERSION.SDK_INT >= 24) {
                                            calender.set(Calendar.YEAR, year);
                                            calender.set(Calendar.MONTH, monthOfYear);
                                            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                            updateLabel();
                                        }
                                    }

                                },

                                calender.get(Calendar.YEAR),
                                calender.get(Calendar.MONTH),
                                calender.get(Calendar.DAY_OF_MONTH)
                        ).show();
                    }
                }
            });

        } else {
            EditText et = (EditText) findViewById(R.id.child_bdate);
            et.setFocusableInTouchMode(true);
            et.setFocusable(true);
        }

        // save btn
        saveBtn = (Button) findViewById(R.id.save_complete_registr);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText childName = (EditText) findViewById(R.id.child_name);
                EditText child_birthdate = (EditText) findViewById(R.id.child_bdate);
                RadioGroup child_sex = (RadioGroup) findViewById(R.id.child_sex);
                int choice = child_sex.getCheckedRadioButtonId();

                if (childName.getText().toString().length() < 1) {
                    Toast.makeText(CompleteRegister.this, getString(R.string.child_name) + " : " + getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                    childName.requestFocus();
                } else if (child_birthdate.getText().toString().length() < 1) {
                    Toast.makeText(CompleteRegister.this, getString(R.string.child_bdate) + " : " + getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                    child_birthdate.callOnClick();
                } else if (choice < 0) {
                    Toast.makeText(CompleteRegister.this, getString(R.string.child_sex) + " : " + getString(R.string.field_required), Toast.LENGTH_SHORT).show();
                    child_sex.requestFocus();
                } else {
                    saveData();
                }
            }
        });

    }

    private void fillData(HashMap<String, String> parentData) {
        LinearLayout form = (LinearLayout) findViewById(R.id.complete_registration_form);
        int count = form.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = form.getChildAt(i);

            // deal with radio buttons
            if (o instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) o;
                String value = parentData.get(Constants.getHashMapKey(this, checkBox));

                if (value != null && value.equalsIgnoreCase("yes")) {
                    checkBox.setChecked(true);
                }
            }


            // deal with edit texts
            else if (o instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) o;
                FrameLayout fl = (FrameLayout) til.getChildAt(0);
                EditText et = (EditText) fl.getChildAt(0);
                String value = parentData.get(Constants.getHashMapKey(this, et));
                if (value != null && value.length() > 0) {
                    et.setText(value);
                }
            }

            // deal with edit texts
            else if (o instanceof EditText) {
                EditText et = (EditText) o;
                String value = parentData.get(Constants.getHashMapKey(this, et));
                if (value != null && value.length() > 0) {
                    et.setText(value);
                }
            }

            // deal with edit texts
            else if (o instanceof TextView) {
                TextView tv = (TextView) o;
                String value = parentData.get(Constants.getHashMapKey(this, tv));
                if (value != null && value.length() > 0) {
                    tv.setText(value);
                }
            }

            // deal with radio buttons
            else if (o instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) o;
                String value = parentData.get(Constants.getHashMapKey(this, checkBox));

                if (value != null && value.equalsIgnoreCase("yes")) {
                    checkBox.setChecked(true);
                }
            }


            // deal with radio buttons
            else if (o instanceof LinearLayout) {
                LinearLayout lin = (LinearLayout) o;
                int linCount = lin.getChildCount();
                for (int j = 0; j < linCount; j++) {
                    o = lin.getChildAt(j);
                    if (o instanceof RadioGroup) {
                        TextView tv = (TextView) lin.getChildAt(j - 1);
                        RadioGroup rg = (RadioGroup) o;
                        if (rg.getId() == R.id.parents_chronic_disease) {
                            Log.d("", "uuu");
                        }
                        int rgCount = rg.getChildCount();
                        for (int z = 0; z < rgCount; z++) {
                            RadioButton rb = (RadioButton) rg.getChildAt(z);
                            String selection = getResources().getResourceEntryName(rb.getId());
                            String value = parentData.get(Constants.getHashMapKey(this, rg));
                            if (value != null && value.equalsIgnoreCase(selection)) {
                                rb.setChecked(true);
                                break;
                            }
                        }
                    }
                }
            }
        }

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
                String text = et.getText().toString().trim();
                String key = Constants.getHashMapKey(this, et);
                if (key == null) continue;

                data.put(key, text);


            }

            // deal with edit texts
            if (o instanceof EditText) {
                EditText et = (EditText) o;
                String text = et.getText().toString().trim();
                String key = Constants.getHashMapKey(this, et);
                if (key == null) continue;
                data.put(key, text);
            }

            // deal with radio buttons
            if (o instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) o;
                String key = Constants.getHashMapKey(this, checkBox);
                if (key == null) continue;
                if (checkBox.isChecked()) {
                    data.put(key, "yes");
                } else {
                    data.put(key, "no");
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
                            String selection = getResources().getResourceEntryName(btn.getId());
                            String key = Constants.getHashMapKey(this, rg);
                            if (key == null) continue;
                            data.put(key, selection);// hashmap
                        }
                    }
                }
            }
        }

        DatabasePostConnection con = new DatabasePostConnection(this);

        // save child bdate
        data.put(Constants.getHashMapKey(this, findViewById(R.id.child_bdate)), ((TextView) findViewById(R.id.child_bdate)).getText().toString());
        data.put(Constants.CODE, Constants.COMPLETE_REGISTRATION + "");
        data.put(Constants.USER_ID_META, Constants.get_current_user_id(this) + "");

        data.remove("null");
        con.postRequest(data, Constants.DATABASE_URL);
    }

    private void updateLabel() {
        if (Build.VERSION.SDK_INT >= 24) {
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            childBirthDate.setText(sdf.format(calender.getTime()));
        }
    }


    @Override
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch (result) {
                case Constants.SCF_COMPLETE_REGITER: {
                    Toast.makeText(this, getString(R.string.saved_success), Toast.LENGTH_LONG).show();
                    Parent p = (Parent) Constants.updateUser(this, output);
                    break;
                }

                default: {
                    Log.d("error in update ", result);
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


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


