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
import project.aha.R;

public class DoctorMainActivity extends AppCompatActivity {

    private Button patients_files_btn;
    private Button create_exercise;
    private Button my_exercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        patients_files_btn = (Button)findViewById(R.id.patients_files_btn);
        patients_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this , PatientsFilesActivity.class));
            }
        });


        create_exercise = (Button) findViewById(R.id.create_exercise_btn);
        create_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this , CreateExercise.class));
            }
        });

        my_exercises = (Button) findViewById(R.id.my_exercise);
        my_exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this , DoctorExercises.class));
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                Constants.logout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
