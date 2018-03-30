package project.aha.parent_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import project.aha.Constants;
import project.aha.R;
import project.aha.ExercisesList;
import project.aha.models.Parent;

public class ParentMainActivity extends AppCompatActivity {


    private Button advance_registration;
    private Button cars_exam;
    private Button med_hist;
    private Button what_is_autism;
    private Button exercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main);
        Constants.showLogo(this);

        final Parent p  = (Parent) Constants.get_user_object(this);
        final String must_did_adv_reg = getString(R.string.must_did_adv_reg);
        advance_registration  = (Button) findViewById(R.id.advance_registration);
        advance_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentMainActivity.this, CompleteRegister.class));
            }
        });


        what_is_autism  = (Button) findViewById(R.id.what_is_autism);
        what_is_autism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentMainActivity.this, WhatAutism.class));
            }
        });



        cars_exam  = (Button) findViewById(R.id.cars_exam);
        cars_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p.DidAdvanceRegistration()){
                    startActivity(new Intent(ParentMainActivity.this, CARS_Exam.class));
                } else{
                    Toast.makeText(ParentMainActivity.this, must_did_adv_reg, Toast.LENGTH_SHORT).show();
                }
            }
        });


        med_hist  = (Button) findViewById(R.id.med_hist);
        med_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p.DidAdvanceRegistration()){
                    startActivity(new Intent(ParentMainActivity.this, MedicalHistoryList.class));
                } else{
                    Toast.makeText(ParentMainActivity.this, must_did_adv_reg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        exercises  = (Button) findViewById(R.id.exercises_list);
        exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentMainActivity.this, ExercisesList.class));
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
