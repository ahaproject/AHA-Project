package project.aha.parent_panel;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.MedicalHistoryList;
import project.aha.R;
import project.aha.admin_panel.ListDoctorsActivity;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Parent;

public class ParentSingleView extends AppCompatActivity implements ReceiveResult {


    private Parent parent;

    private int consult_doctor = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_single_view);
        Constants.showLogo(this);

        Intent i = getIntent();
        if (i != null) {
            int id = i.getIntExtra("id", -1);
            parent = (Parent) i.getSerializableExtra("parent");

            if (parent != null) {
                consult_doctor = parent.getConsult_doctor();
                id = parent.getUser_id();
            }
            if (id > -1) {
                HashMap<String, String> data = new HashMap<>();
                data.put(Constants.CODE, Constants.SELECT_USER + "");
                data.put(Constants.USER_ID_META, id + "");

                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data, Constants.DATABASE_URL);
            }
        }
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output").getJSONArray("data").getJSONObject(0);


            parent = (Parent) Constants.readAndSaveUser(output, true);

            consult_doctor = parent.getConsult_doctor();
            setTitle(parent.getUser_name());

            TextView name = (TextView) findViewById(R.id.p_name);
            name.setText(getString(R.string.name) + " : " + parent.getUser_name());

            Button p_med_hist = (Button) findViewById(R.id.p_med_hist);
            p_med_hist.setVisibility(View.VISIBLE);
            p_med_hist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ParentSingleView.this, MedicalHistoryList.class);
                    i.putExtra(Constants.PARENT_ID_META, parent.getUser_id());
                    startActivity(i);
                }
            });


            if (parent.getMetas().size() > 0) {
                HashMap<String, String> parentData = parent.getMetas();
                View form = findViewById(R.id.p_informations_include);
                form.setVisibility(View.VISIBLE);
                TextView parent_info_title = (TextView) findViewById(R.id.parent_info_title);
                parent_info_title.setVisibility(View.VISIBLE);
                fillData(parentData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fillData(HashMap<String, String> parentData) {
        LinearLayout form = (LinearLayout) findViewById(R.id.complete_registration_form);
        int count = form.getChildCount();
        for (int i = 0; i < count; i++) {
            View o = form.getChildAt(i);

            // deal with radio checkbox
            if (o instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) o;
                String value = parentData.get(Constants.getHashMapKey(this, checkBox));
                checkBox.setFocusable(false);
                checkBox.setFocusableInTouchMode(false);
                checkBox.setClickable(false);
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
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
                et.setClickable(false);
                if (value != null && value.length() > 0) {
                    et.setText(value);
                }
            }

            // deal with edit texts
            else if (o instanceof EditText) {
                EditText et = (EditText) o;
                String value = parentData.get(Constants.getHashMapKey(this, et));
                et.setFocusable(false);
                et.setFocusableInTouchMode(false);
                et.setClickable(false);
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
            else if (o instanceof LinearLayout) {
                LinearLayout lin = (LinearLayout) o;

                int linCount = lin.getChildCount();
                for (int j = 0; j < linCount; j++) {
                    o = lin.getChildAt(j);
                    if (o instanceof RadioGroup) {

                        TextView tv = (TextView) lin.getChildAt(j - 1);
                        RadioGroup rg = (RadioGroup) o;

                        int rgCount = rg.getChildCount();
                        boolean checked = false;
                        for (int z = 0; z < rgCount; z++) {
                            RadioButton rb = (RadioButton) rg.getChildAt(z);
                            String selection = getResources().getResourceEntryName(rb.getId());
                            String value = parentData.get(Constants.getHashMapKey(this, rg));
                            if (value != null && value.equalsIgnoreCase(selection)) {
                                rb.setChecked(true);
                            }
                            rb.setClickable(false);
                            rb.setFocusable(false);
                            rb.setFocusableInTouchMode(false);
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        parent = (Parent) getIntent().getSerializableExtra("parent");
        consult_doctor = (parent == null) ? -1 : parent.getConsult_doctor();
        int current_user_type = Constants.get_current_user_type(this);
        int current_user_id = Constants.get_current_user_id(this);
        if (current_user_type == Constants.DOCTOR_TYPE && current_user_id == consult_doctor) {
            inflater.inflate(R.menu.user_info_bar, menu);
            menu.findItem(R.id.switch_consult).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null)
            return false;

        switch (item.getItemId()) {
            case R.id.switch_consult: {
                Intent i = new Intent(this, ListDoctorsActivity.class);
                i.putExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE, "switch_consult");
                i.putExtra(Constants.PARENT_ID_META, parent.getUser_id());
                startActivity(i);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
