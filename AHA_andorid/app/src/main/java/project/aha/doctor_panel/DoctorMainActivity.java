package project.aha.doctor_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import project.aha.Constants;
import project.aha.ExercisesList;
import project.aha.R;
import project.aha.admin_panel.ListParentActivity;
import project.aha.admin_panel.ListDoctorsActivity;

public class DoctorMainActivity extends AppCompatActivity {

    private Button patients_files_btn;
    private Button create_exercise;
    private Button my_exercises;
    private Button chats_doctors;
    private Button chats_parents;
    private Button doctor_patients_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);
        Constants.showLogo(this);

        patients_files_btn = (Button) findViewById(R.id.patients_files_btn);
        patients_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this, PatientsFilesActivity.class));
            }
        });


        create_exercise = (Button) findViewById(R.id.create_exercise_btn);
        create_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this, CreateExercise.class));
            }
        });

        my_exercises = (Button) findViewById(R.id.my_exercise);
        my_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorMainActivity.this, ExercisesList.class);
                i.putExtra(Constants.DOCTOR_ID_META, Constants.get_current_user_id(DoctorMainActivity.this));
                startActivity(i);
            }
        });

        chats_doctors = (Button) findViewById(R.id.chat_doc_btn);
        chats_doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorMainActivity.this, ListDoctorsActivity.class);
                i.putExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE, "doctor_chat");
                startActivity(i);
            }
        });


        chats_parents = (Button) findViewById(R.id.chat_patient_btn);
        chats_parents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorMainActivity.this, ListParentActivity.class);
                i.putExtra(Constants.LIST_PARENTS_ACTIVTY_CHOICE, "chat");
                startActivity(i);
            }
        });

        doctor_patients_list = (Button) findViewById(R.id.doctor_patients_list);
        doctor_patients_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorMainActivity.this, ListParentActivity.class);
                i.putExtra(Constants.LIST_PARENTS_ACTIVTY_CHOICE, "doctor_patients");
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);

    }
}
