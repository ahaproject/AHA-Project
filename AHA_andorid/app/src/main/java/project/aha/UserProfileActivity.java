package project.aha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import project.aha.models.Doctor;
import project.aha.models.Parent;
import project.aha.models.User;

public class UserProfileActivity extends AppCompatActivity {


    private User user;

    private TextView name, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        int user_type = Constants.get_current_user_type(this);
        if (user_type == Constants.ADMIN_TYPE) {
            user = Constants.get_user_object(this);
        } else if (user_type == Constants.DOCTOR_TYPE) {
            user = (Doctor) Constants.get_user_object(this);
        } else {
            user = (Parent) Constants.get_user_object(this);
        }


        name = (TextView) findViewById(R.id.name);
        name.setText(getString(R.string.name) + " : " + user.getUser_name());

        phone = (TextView) findViewById(R.id.phone);
        phone.setText(getString(R.string.hint_phone) + " : " + user.getUser_phone());

        email = (TextView) findViewById(R.id.email);
        email.setText(getString(R.string.hint_email) + " : " + user.getUser_email());


        View extra = findViewById(R.id.extra);
        TextView specialized = (TextView) findViewById(R.id.dr_specialized);
        if (user instanceof Parent) {
            Parent parent = (Parent) user;
            Doctor doctor = parent.getDoctor_obj();
            if(doctor != null) {
                extra.setVisibility(View.VISIBLE);

                TextView dr_name, dr_phone, dr_email;
                dr_name = (TextView) findViewById(R.id.dr_name);
                dr_name.setText(getString(R.string.name) + " : " + doctor.getUser_name());

                dr_phone = (TextView) findViewById(R.id.dr_phone);
                dr_phone.setText(getString(R.string.hint_phone) + " : " + doctor.getUser_phone());

                dr_email = (TextView) findViewById(R.id.dr_email);
                dr_email.setText(getString(R.string.hint_email) + " : " + doctor.getUser_email());


                specialized.setVisibility(View.VISIBLE);
                specialized.setText(getString(R.string.hint_specialized) + " : " + Constants.getDiagnose(doctor.getDiag_id()));
            }
        }

        if (user instanceof Doctor) {

            Doctor doctor = (Doctor) user;
            specialized.setVisibility(View.VISIBLE);
            specialized.setText(getString(R.string.hint_specialized) + " : " + Constants.getDiagnose(doctor.getDiag_id()));

        }

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
        return Constants.handleItemChoosed(this ,super.onOptionsItemSelected(item),item);
    }
}
