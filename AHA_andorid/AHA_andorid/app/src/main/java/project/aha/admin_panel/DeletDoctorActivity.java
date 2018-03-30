package project.aha.admin_panel;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabaseAsyncTask;
import project.aha.R;
import project.aha.models.Doctor;

public class DeletDoctorActivity extends AppCompatActivity {

    ListView doctorsList;
    ArrayList<String> doctorsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delet_doctor);


        doctorsList = (ListView) findViewById(R.id.doctors_list);
        doctorsArray = new ArrayList<>();

        new DatabaseAsyncTask(this, this, Constants.LIST_DOCTORS, null).execute();



    }


    public void fillDoctorsArray(Object result){
        Log.d("Enter fill array " , "Enetere");

        final ArrayList<Doctor> docList = (ArrayList<Doctor>)result ;
//        if(docList.isEmpty()){
//
//        }


        for (Doctor doc : docList ) {
            String content = doc.getUser_name()+" [ "+doc.getSpecialized()+" ]";
            Log.d("content of doctor list" , content);
            doctorsArray.add(content);
        }
        // Create ArrayAdapter using the planet list.
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.doctor_single_view, doctorsArray);
        doctorsList.setAdapter(listAdapter);

        doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                new AlertDialog.Builder(DeletDoctorActivity.this)
                        .setTitle(getString(R.string.delete_doctor_title))
                        .setMessage(getString(R.string.confirm_delete))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Doctor doctor = docList.get(position);
                                HashMap<Object,Object> data = new HashMap<>();
                                data.put(Constants.USER_ID_META , doctor.getUser_id());
                                new DatabaseAsyncTask(DeletDoctorActivity.this,DeletDoctorActivity.this,Constants.DELETE_DOCTOR ,data).execute();
                                doctorsArray.remove(position);
                                listAdapter.notifyDataSetChanged();

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }

    public void showDeletionResult(Object result) {
        String str = (String)result;

        switch(str){
            case Constants.ERR_DELETE_DOCTOR:{
                Toast.makeText(this, getString(R.string.err_delete_doctor), Toast.LENGTH_LONG).show();
                break;
            }

            case Constants.SCF_DELETE_DOCTOR:{
                Toast.makeText(this, getString(R.string.scf_delete_doctor), Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}
