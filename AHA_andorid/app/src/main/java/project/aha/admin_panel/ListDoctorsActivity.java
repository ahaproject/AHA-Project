package project.aha.admin_panel;

import android.content.DialogInterface;
import android.content.Intent;
import android.widget.RelativeLayout.LayoutParams;

import android.support.v7.app.AlertDialog;
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

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.interfaces.DialogCallBack;
import project.aha.interfaces.ReceiveResult;
import project.aha.ListAdapter;
import project.aha.Chatting.SingleChatActivity;
import project.aha.models.Doctor;

public class ListDoctorsActivity extends AppCompatActivity implements ReceiveResult ,DialogCallBack{


    // list view of the doctors
    ListView doctorsList;

    private ListAdapter listAdapter;
    final ArrayList<Doctor> doctors_objects = new ArrayList<>();


    TextView choose_doctor_title;

    String choice;

    int clicked_position;
    int parent_id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_doctor);
        Constants.showLogo(this);

        choose_doctor_title = (TextView) findViewById(R.id.choose_doctor_title);

        // get the view list
        doctorsList = (ListView) findViewById(R.id.doctors_list);

        // set them to not visible until we ensure that there are records
        choose_doctor_title.setVisibility(View.GONE);
        doctorsList.setVisibility(View.GONE);


        Intent i = getIntent();
        if (i != null) {

            choice = i.getStringExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE);
            switch (choice) {


                case "switch_consult" :{
                    setTitle(R.string.switch_consult);
                    parent_id = i.getIntExtra(Constants.PARENT_ID_META , -1);
                    // add action when the admin clicks on parent record in the listview
                    doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            clicked_position = position;
                            Constants.showConfirmDeleteDialog(ListDoctorsActivity.this , getString(R.string.confirm_switch_consult),getString(R.string.switch_consult));
                        }
                    });


                    break;
                }

                case "doctor_chat": {
                    setTitle(R.string.chat_doctors);
                    // add action when the admin clicks on parent record in the listview
                    doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            Intent i = new Intent(ListDoctorsActivity.this, SingleChatActivity.class);
                            i.putExtra("other_user_id", doctors_objects.get(position).getUser_id());
                            i.putExtra("other_user_type", doctors_objects.get(position).getUser_type());
                            startActivity(i);
                        }
                    });
                    break;
                }
                case "chat": {
                    setTitle(R.string.chat_doctors);
                    // add action when the admin clicks on parent record in the listview
                    doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            Intent i = new Intent(ListDoctorsActivity.this, SingleChatActivity.class);
                            i.putExtra("other_user_id", doctors_objects.get(position).getUser_id());
                            i.putExtra("other_user_type", doctors_objects.get(position).getUser_type());
                            startActivity(i);
                        }
                    });
                    break;
                }

                case "delete": {
                    setTitle(R.string.delete_doctor_title);

                    // add action when the admin clicks on parent record in the listview
                    doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            showConfirmDeleteDialog(position, listAdapter);

                        }
                    });
                    break;
                }
            }


        }

        // send request to database to get all doctors
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.LIST_DOCTORS + "");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data, Constants.DATABASE_URL);

    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch (result) {

                case Constants.SCF_ASSIGN_DOCTOR:{
                    Toast.makeText(this, getString(R.string.scf_assign_doctor), Toast.LENGTH_LONG).show();
                    break;
                }
                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    choose_doctor_title.setVisibility(View.VISIBLE);
                    doctorsList.setVisibility(View.VISIBLE);
                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
                    no_records_text.setVisibility(View.GONE);
                    fill_listView_with_doctors(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_RECORDS: {

                    break;
                }

                // if the admin try to delete doctor and there are an error -> print error
                case Constants.ERR_DELETE_DOCTOR: {
                    Toast.makeText(this, getString(R.string.err_delete_doctor), Toast.LENGTH_LONG).show();
                    break;
                }

                // if the admin try to delete doctor and it success -> show successful text
                case Constants.SCF_DELETE_DOCTOR: {
                    int user_id = output.getInt(Constants.USER_ID_META);
                    delete_doctor(user_id, listAdapter);

                    if (doctors_objects.size() == 0) {
                        show_no_records();
                    }
                    Toast.makeText(this, getString(R.string.scf_delete_doctor), Toast.LENGTH_LONG).show();
                    break;
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void show_no_records() {
        choose_doctor_title.setVisibility(View.GONE);
        doctorsList.setVisibility(View.GONE);
        TextView no_records_text = (TextView) findViewById(R.id.no_records);
        no_records_text.setVisibility(View.VISIBLE);
    }

    private void fill_listView_with_doctors(JSONObject output) {
        try {

            // convert from JSON Array to ArrayList

            JSONArray doctorsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < doctorsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = doctorsJSONArray.getJSONObject(i);


                    int user_id = jsonObject.getInt(Constants.USER_ID_META);
                    String user_email = jsonObject.getString(Constants.USER_EMAIL_META);
                    String user_phone = jsonObject.optString(Constants.USER_PHONE_META, "");
                    String user_name = jsonObject.optString(Constants.USER_NAME_META, "");
                    int diagn = jsonObject.getInt(Constants.DOCTOR_SPECILIZED_META);

                    if ((choice.equalsIgnoreCase("doctor_chat") || choice.equalsIgnoreCase("switch_consult"))
                            && user_id == Constants.get_current_user_id(this)) {

                    } else {
                        doctors_objects.add(new Doctor(user_id, user_email, user_name, Constants.DOCTOR_TYPE, user_phone, diagn));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************
            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(this, doctors_objects);
            //            listAdapter.
            doctorsList.setAdapter(listAdapter);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void showConfirmDeleteDialog(final int doctor_record_position, final ListAdapter listAdapter) {

        // create dialog
        AlertDialog.Builder confirm_delete_dialog = new AlertDialog.Builder(ListDoctorsActivity.this);

        // set title
        confirm_delete_dialog.setTitle(getString(R.string.delete_doctor_title));

        // set message
        confirm_delete_dialog.setMessage(getString(R.string.confirm_delete));

        // set icon
        confirm_delete_dialog.setIcon(android.R.drawable.ic_dialog_alert);

        // add action when admin click on yes
        confirm_delete_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sendRequestDelete(doctor_record_position, listAdapter);
            }
        });

        // add action if the admin click on cancel
        confirm_delete_dialog.setNegativeButton(android.R.string.no, null);


        // show the dialog
        confirm_delete_dialog.show();

    }

    private void sendRequestDelete(int doctor_record_position, ListAdapter listAdapter) {
        Doctor doctor = doctors_objects.get(doctor_record_position);
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.DELETE_DOCTOR + "");
        data.put(Constants.USER_ID_META, doctor.getUser_id() + "");

        new DatabasePostConnection(ListDoctorsActivity.this).postRequest(data, Constants.DATABASE_URL);

    }

    private void delete_doctor(int doctor_id, ListAdapter listAdapter) {
        for (Doctor d : doctors_objects) {
            if (d.getUser_id() == doctor_id) {
                doctors_objects.remove(d);
                listAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void dialogCallBack() {
        Doctor doctor = doctors_objects.get(clicked_position);
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.ASSIGN_DOCTOR + "");
        data.put(Constants.DOCTOR_ID_META, doctor.getUser_id() + "");
        data.put(Constants.PARENT_ID_META, parent_id + "");
        new DatabasePostConnection(ListDoctorsActivity.this).postRequest(data, Constants.DATABASE_URL);


    }
}
