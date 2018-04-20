package project.aha.admin_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import project.aha.Constants;
import project.aha.R;
import project.aha.models.ReportedChat;
import project.aha.models.User;

public class AdminMainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "channel_id";
    private Button addDoctorBtn, deleteDoctorBtn, deleteParentBtn, chatDoctorBtn, reportedChatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        Constants.showLogo(this);


        // add actions to buttons
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


//        chatDoctorBtn = (Button) findViewById(R.id.chat_doctor_btn);
//        chatDoctorBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(AdminMainActivity.this, ListDoctorsActivity.class);
//                i.putExtra(Constants.LIST_DOCTORS_ACTIVTY_CHOICE, "chat");
//                startActivity(i);
//            }
//        });


        reportedChatsBtn = (Button) findViewById(R.id.reported_chats);
        reportedChatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminMainActivity.this, ReportedChats.class);
                startActivity(i);
            }
        });


        // check chats for this user
//        Constants.checkChats(Constants.get_user_object(this), this);

        // check reported chats
        checkReports();
    }


    private void checkReports() {
        // get the database reference
        final DatabaseReference databaseReference = Constants.getReportsRef();

        // add action if any child added to the database
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                doNotification(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void doNotification(DataSnapshot dataSnapshot) {
        // get the reported chat details from the database
        ReportedChat rch = dataSnapshot.getValue(ReportedChat.class);

        // if this user is admin
        if (Constants.get_current_user_type(AdminMainActivity.this) == Constants.ADMIN_TYPE) {
            // and this notification not seen before
            if (rch.getSeen().equals("no")) {

                // get the reporter object
                User reporter = rch.getReporter();
                if(reporter != null) {
                    // set the title and message for the notification
                    String title = getString(R.string.reported_chat);
                    String message = String.format(getString(R.string.user_reporter), reporter.getUser_name());

                    // set the intent when the user clicks on the notification
                    Intent resultIntent = new Intent(this, SingleViewReportedChat.class);
                    resultIntent.putExtra("reported_chat", rch);

                    // and then display it to the user
                    Constants.sendNotification(AdminMainActivity.this, title, message, true, resultIntent);

                    // then set the notification to seen to not be pushed again !
                    dataSnapshot.getRef().child("seen").setValue("yes");
                }
            }
        }
    }

    // top bar

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
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);
    }

}



