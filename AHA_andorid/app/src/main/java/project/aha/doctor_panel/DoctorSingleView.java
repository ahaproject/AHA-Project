package project.aha.doctor_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Diagnose;
import project.aha.models.Doctor;

public class DoctorSingleView extends AppCompatActivity implements ReceiveResult {


    Doctor d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_single_view);

        Constants.showLogo(this);

        Intent i = getIntent();
        if (i != null) {
            int id = i.getIntExtra("id", -1);

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
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            Log.d("pat", resultJson);
            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    d = (Doctor) Constants.readAndSaveUser(output);

                    setTitle(d.getUser_name());

                    TextView name = (TextView) findViewById(R.id.p_name);
                    name.setText(getString(R.string.name) + " : " + d.getUser_name());


                    TextView mSpecialized = (TextView) findViewById(R.id.specialized);
                    Diagnose diagnoseObj = Constants.diagnoses.get(d.getDiag_id());
                    if(diagnoseObj != null) mSpecialized.setText(getString(R.string.hint_specialized) + " : " + diagnoseObj.getName());

                    if(Constants.get_current_user_type(this) == Constants.PARENT_TYPE) {
                        final Button assign_doctor = (Button) findViewById(R.id.assign_doctor);
                        assign_doctor.setVisibility(View.VISIBLE);
                        assign_doctor.setText(String.format(getString(R.string.assign_doctor), d.getUser_name()));
                        assign_doctor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                assign_doctor(d.getUser_id());
                            }
                        });
                    }
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_RECORDS: {
                    Toast.makeText(this, "No user found !!!!!", Toast.LENGTH_SHORT).show();
                    break;
                }


                case Constants.SCF_ASSIGN_DOCTOR: {
                    Toast.makeText(this, getString(R.string.scf_assign_doctor), Toast.LENGTH_SHORT).show();
                    break;
                }

                case Constants.ERR_ASSIGN_DOCTOR:{
                    Toast.makeText(this, "ERR assigning doctor !!!", Toast.LENGTH_SHORT).show();
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void assign_doctor(int doctor_id) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.ASSIGN_DOCTOR + "");
        data.put(Constants.PARENT_ID_META, Constants.get_current_user_id(this) + "");
        data.put(Constants.DOCTOR_ID_META, doctor_id + "");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data, Constants.DATABASE_URL);
    }
}
