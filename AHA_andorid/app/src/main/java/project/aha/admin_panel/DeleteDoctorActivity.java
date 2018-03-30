package project.aha.admin_panel;

import android.content.DialogInterface;
import android.widget.RelativeLayout.LayoutParams;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import project.aha.ReceiveResult;
import project.aha.models.Doctor;

public class DeleteDoctorActivity extends AppCompatActivity implements ReceiveResult{


    // list view of the doctors
    ListView doctorsList;

    final ArrayList<Doctor> doctors_objects = new ArrayList<>();

    // arraylist that contains all doctors
    ArrayList<String> doctors_names;

    TextView choose_doctor_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_doctor);



        choose_doctor_title = (TextView)findViewById(R.id.choose_doctor_title);

        // get the view list
        doctorsList = (ListView) findViewById(R.id.doctors_list);

        // set them to not visible until we ensure that there are records
        choose_doctor_title.setVisibility(View.GONE);
        doctorsList.setVisibility(View.GONE);


        // create empty array list of doctors
        doctors_names = new ArrayList<>();

        // send request to database to get all doctors
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.LIST_DOCTORS+"");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data , Constants.DATABASE_URL);

    }
    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch(result){

                // if there are records -> fill list view
                case Constants.DOCTORS_RECORDS :{
                    // set them to visible
                    choose_doctor_title.setVisibility(View.VISIBLE);
                    doctorsList.setVisibility(View.VISIBLE);
                    fill_listView_with_doctors(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_DOCTORS_RECORDS:{
                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
                    LayoutParams lp = (LayoutParams) no_records_text.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    no_records_text.setLayoutParams(lp);
                    no_records_text.setText(getString(R.string.no_doctors_records));
                    no_records_text.setVisibility(View.VISIBLE);
                    break;
                }

                // if the admin try to delete doctor and there are an error -> print error
                case Constants.ERR_DELETE_DOCTOR:{
                    Toast.makeText(this, getString(R.string.err_delete_doctor), Toast.LENGTH_LONG).show();
                    break;
                }

                // if the admin try to delete doctor and it success -> show successful text
                case Constants.SCF_DELETE_DOCTOR:{
                    Toast.makeText(this, getString(R.string.scf_delete_doctor), Toast.LENGTH_LONG).show();
                    break;
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fill_listView_with_doctors(JSONObject output) {
        try{

            // convert from JSON Array to ArrayList

            JSONArray doctorsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < doctorsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = doctorsJSONArray.getJSONObject(i);


                    int user_id = Integer.parseInt((String) jsonObject.get("user_id"));
                    String user_email = (String) jsonObject.get(Constants.USER_EMAIL_META);
                    String user_phone = (String) jsonObject.get(Constants.USER_PHONE_META);
                    String user_name = (String) jsonObject.get(Constants.USER_NAME_META);
                    String specialized = (String) jsonObject.get(Constants.DOCTOR_SPECILIZED_META);
                    doctors_objects.add(new Doctor(user_id, user_email, user_name, "", Constants.DOCTOR_TYPE, user_phone , specialized));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************
            // add only doctors names and specialize to the list view
            for (Doctor single_doctor : doctors_objects ) {
                String content = single_doctor.getUser_name()+" [ "+single_doctor.getSpecialized()+" ]";
                doctors_names.add(content);
            }
            // ********************************************************************************************

            // Create ArrayAdapter which adapt array list to list view.
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.user_single_view, doctors_names);
            doctorsList.setAdapter(listAdapter);

            // add action when the admin clicks on doctor record in the listview
            doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    showConfirmDeleteDialog(position, listAdapter);

                }
            });

        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    private void showConfirmDeleteDialog(final int doctor_record_position, final ArrayAdapter<String> listAdapter) {

        // create dialog
        AlertDialog.Builder confirm_delete_dialog = new AlertDialog.Builder(DeleteDoctorActivity.this);

        // set title
        confirm_delete_dialog.setTitle(getString(R.string.delete_doctor_title));

        // set message
        confirm_delete_dialog.setMessage(getString(R.string.confirm_delete));

        // set icon
        confirm_delete_dialog.setIcon(android.R.drawable.ic_dialog_alert);

        // add action when admin click on yes
        confirm_delete_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                removeDoctor(doctor_record_position , listAdapter);
            }});

        // add action if the admin click on cancel
        confirm_delete_dialog.setNegativeButton(android.R.string.no, null);


        // show the dialog
        confirm_delete_dialog.show();

    }

    private void removeDoctor(int doctor_record_position, ArrayAdapter<String> listAdapter) {
        Doctor doctor = doctors_objects.get(doctor_record_position);
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.DELETE_DOCTOR+"");
        data.put(Constants.USER_ID_META , doctor.getUser_id()+"");

        new DatabasePostConnection(DeleteDoctorActivity.this).postRequest(data , Constants.DATABASE_URL);
        doctors_names.remove(doctor_record_position);
        listAdapter.notifyDataSetChanged();
    }
}
