package project.aha.doctor_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.ListAdapter;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Parent;
import project.aha.parent_panel.ParentSingleView;

public class PatientsFilesActivity extends AppCompatActivity implements ReceiveResult {

    private ListView patients_listview;
    private EditText file_num;
    private ListAdapter listAdapter;

    // Search EditText
    private List<Parent> patientsObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_files);
        Constants.showLogo(this);


        patients_listview = (ListView) findViewById(R.id.patients_listview);
        file_num = (EditText) findViewById(R.id.search_file);


        patientsObjects = new ArrayList<>();


        file_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                PatientsFilesActivity.this.listAdapter.filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        refresh();
    }

    public void refresh() {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.LIST_PARENTS + "");

        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data, Constants.DATABASE_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onReceiveResult(String resultJson) {
        try {
            patients_listview.setAdapter(null);
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            Log.d("pat", resultJson);
            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    patients_listview.setVisibility(View.VISIBLE);
                    file_num.setVisibility(View.VISIBLE);
                    findViewById(R.id.no_records).setVisibility(View.GONE);
                    fill_listView_with_parents(output);
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fill_listView_with_parents(JSONObject output) {
        try {
            patients_listview.setAdapter(null);
            patientsObjects.clear();

            // convert from JSON Array to ArrayList
            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);


                    int user_id = jsonObject.getInt(Constants.USER_ID_META);
                    String user_email = jsonObject.getString(Constants.USER_EMAIL_META);
                    String user_phone = jsonObject.optString(Constants.USER_PHONE_META, "");
                    String user_name = jsonObject.optString(Constants.USER_NAME_META, "");
                    int user_type = jsonObject.getInt(Constants.USER_TYPE_META);
                    int consult_doctor = jsonObject.optInt(Constants.CONSULT_DOCTOR, -1);

                    Parent parent = new Parent(user_id, user_email, user_name, user_type, user_phone, true, consult_doctor);
                    JSONArray metas = jsonObject.optJSONArray("metas");
                    if (metas != null && metas.length() > 0) {
                        for (int j = 0; j < metas.length(); j++) {
                            JSONObject meta = metas.getJSONObject(j);
                            String meta_key = meta.getString("meta_key");
                            String meta_value = meta.getString("meta_value");
                            parent.addMeta(meta_key, meta_value);
                        }
                    }


                    patientsObjects.add(parent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // ********************************************************************************************

            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(PatientsFilesActivity.this, patientsObjects);
//            listAdapter.
            patients_listview.setAdapter(listAdapter);
            patients_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent i = new Intent(PatientsFilesActivity.this, ParentSingleView.class);
                    i.putExtra("parent", patientsObjects.get(position));
                    startActivity(i);
                }
            });


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
