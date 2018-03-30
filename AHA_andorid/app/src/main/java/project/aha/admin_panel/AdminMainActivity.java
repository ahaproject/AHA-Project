package project.aha.admin_panel;

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

public class AdminMainActivity extends AppCompatActivity {

    Button addDoctorBtn , deleteDoctorBtn , deleteParentBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);


        addDoctorBtn = (Button) findViewById(R.id.add_doctor_btn);
        addDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,AddDoctorActivity.class));
            }
        });


        deleteDoctorBtn = (Button) findViewById(R.id.delete_doctor_btn);
        deleteDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,DeleteDoctorActivity.class));
            }
        });


        deleteParentBtn = (Button) findViewById(R.id.delete_parent_btn);
        deleteParentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,DeleteParentActivity.class));
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
