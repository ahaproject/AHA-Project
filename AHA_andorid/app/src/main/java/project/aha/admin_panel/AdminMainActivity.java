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
import project.aha.doctor_panel.DoctorMainActivity;

public class AdminMainActivity extends AppCompatActivity {

    Button addDoctorBtn, deleteDoctorBtn, deleteParentBtn, chatDoctorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        Constants.showLogo(this);


        addDoctorBtn = (Button) findViewById(R.id.add_doctor_btn);
        addDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AddDoctorActivity.class));
            }
        });


        deleteDoctorBtn = (Button) findViewById(R.id.delete_doctor_btn);
        deleteDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminMainActivity.this, ListDoctorsActivity.class);
                i.putExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE, "delete");
                startActivity(i);
            }
        });


        deleteParentBtn = (Button) findViewById(R.id.delete_parent_btn);
        deleteParentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminMainActivity.this, ListParentActivity.class);
                i.putExtra(Constants.LIST_PARENTS_ACTIVTY_CHOICE, "delete");
                startActivity(i);
            }
        });


        chatDoctorBtn = (Button) findViewById(R.id.chat_doctor_btn);
        chatDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminMainActivity.this, ListDoctorsActivity.class);
                i.putExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE, "chat");
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
//        if(Constants.get_current_user_type(this) == Constants.ADMIN_TYPE){
//            menu.findItem(R.id.show_profile).setVisible(false);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);

    }
}
