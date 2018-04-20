package project.aha.admin_panel;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout.LayoutParams;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.interfaces.DialogCallBack;
import project.aha.interfaces.ReceiveResult;
import project.aha.ListAdapter;
import project.aha.Chatting.SingleChatActivity;
import project.aha.models.Parent;
import project.aha.models.User;
import project.aha.parent_panel.ParentSingleView;

public class ListParentActivity extends AppCompatActivity implements ReceiveResult, DialogCallBack {


    // list view of the doctors
    private ListView parentsList;
    private ListAdapter listAdapter;
    final List<Parent> parents_objects = new ArrayList<>();
    private TextView choose_parent_title;
    private String choice;
    private int presse_item_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_parent);
        Constants.showLogo(this);

        choose_parent_title = (TextView) findViewById(R.id.choose_parent_title);

        // get the view list
        parentsList = (ListView) findViewById(R.id.parents_list);


        Intent i = getIntent();
        if (i != null) {

            choice = i.getStringExtra(Constants.LIST_PARENTS_ACTIVTY_CHOICE);
            switch (choice) {

                case "doctor_patients": {
                    setTitle(R.string.doctor_patients_list);
                    // add action when the admin clicks on parent record in the listview
                    parentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            Intent i = new Intent(ListParentActivity.this, ParentSingleView.class);
                            i.putExtra("parent", parents_objects.get(position));
                            startActivity(i);
                        }
                    });

                    // send request to database to get all parents of specific doctor
                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constants.CODE, Constants.LIST_PARENTS + "");
                    data.put(Constants.DOCTOR_ID_META, Constants.get_current_user_id(this) + "");

                    DatabasePostConnection connection = new DatabasePostConnection(this);
                    connection.postRequest(data, Constants.DATABASE_URL);
                    break;
                }
                case "chat": {
                    setTitle(R.string.contact_patient);
                    // add action when the admin clicks on parent record in the listview
                    parentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            Intent i = new Intent(ListParentActivity.this, SingleChatActivity.class);
                            i.putExtra("other_user_id", parents_objects.get(position).getUser_id());
                            i.putExtra("other_user_type", parents_objects.get(position).getUser_type());
                            startActivity(i);
                        }
                    });

                    // send request to database to get all parents
                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constants.CODE, Constants.LIST_PARENTS + "");
                    DatabasePostConnection connection = new DatabasePostConnection(this);
                    connection.postRequest(data, Constants.DATABASE_URL);
                    break;
                }

                case "delete": {
                    setTitle(R.string.delete_parent_title);

                    // add action when the admin clicks on parent record in the listview
                    parentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            presse_item_position = position;
                            Constants.showConfirmDeleteDialog(ListParentActivity.this, getString(R.string.confirm_delete), getString(R.string.delete_parent_title));
                        }
                    });

                    // send request to database to get all parents
                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constants.CODE, Constants.LIST_PARENTS + "");
                    DatabasePostConnection connection = new DatabasePostConnection(this);
                    connection.postRequest(data, Constants.DATABASE_URL);
                    break;
                }
            }


        }


    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    choose_parent_title.setVisibility(View.VISIBLE);
                    parentsList.setVisibility(View.VISIBLE);
                    findViewById(R.id.no_records).setVisibility(View.GONE);
                    fill_listView_with_parents(output);
                    break;
                }


                // if the admin try to delete doctor and there are an error -> print error
                case Constants.ERR_DELETE_PARENT: {
                    Toast.makeText(this, getString(R.string.err_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }

                // if the admin try to delete doctor and it success -> show successful text
                case Constants.SCF_DELETE_PARENT: {
                    int user_id = output.getInt(Constants.USER_ID_META);
                    delete_parent(user_id, listAdapter);

                    // if there are no records remain in the list view
                    if (parents_objects.size() == 0) {
                        show_no_records();
                    }
                    Toast.makeText(this, getString(R.string.scf_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void show_no_records() {
        choose_parent_title.setVisibility(View.GONE);
        parentsList.setVisibility(View.GONE);
        findViewById(R.id.no_records).setVisibility(View.VISIBLE);
    }

    private void fill_listView_with_parents(JSONObject output) {
        try {

            // convert from JSON Array to ArrayList

            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);


                    int user_id = jsonObject.getInt(Constants.USER_ID_META);
                    String user_email = jsonObject.getString(Constants.USER_EMAIL_META);
                    String user_phone = jsonObject.optString(Constants.USER_PHONE_META, "");
                    String user_name = jsonObject.optString(Constants.USER_NAME_META, "");
                    int consult_doctor = jsonObject.optInt(Constants.CONSULT_DOCTOR, -1);

                    parents_objects.add(new Parent(user_id, user_email, user_name, Constants.PARENT_TYPE, user_phone, true, consult_doctor));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************
            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(this, parents_objects);
            //            listAdapter.
            parentsList.setAdapter(listAdapter);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    private void delete_parent(int p_id, ListAdapter listAdapter) {
        for (Parent d : parents_objects) {
            if (d.getUser_id() == p_id) {
                parents_objects.remove(d);
                listAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void dialogCallBack() {
        User parent = (User) parents_objects.get(presse_item_position);
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.DELETE_PARENT + "");
        data.put(Constants.USER_ID_META, parent.getUser_id() + "");

        new DatabasePostConnection(this).postRequest(data, Constants.DATABASE_URL);
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
