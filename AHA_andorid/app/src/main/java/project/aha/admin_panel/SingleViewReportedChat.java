package project.aha.admin_panel;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import project.aha.Constants;
import project.aha.R;
import project.aha.models.ChatMessage;
import project.aha.models.ReportedChat;
import project.aha.models.User;

public class SingleViewReportedChat extends AppCompatActivity {

    private ReportedChat reportedChat;
    private DatabaseReference databaseReference = Constants.getChatsRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view_reported_chat);


        // get the called intent
        Intent i = getIntent();

        if (i != null) {

            // get reported chat object
            reportedChat = (ReportedChat) i.getSerializableExtra("reported_chat");


            setTitle(reportedChat.getReported_date());


            // set the date
            TextView mDate = (TextView) findViewById(R.id.reporting_date);
            mDate.setText(reportedChat.getReported_date());


            // show the reason if there
            TextView mReason = (TextView) findViewById(R.id.reason);
            if (reportedChat.getReason() == null) {
                mReason.setVisibility(View.GONE);
            } else {
                mReason.setText(getString(R.string.reason)+" : "+reportedChat.getReason());
            }


            // get linear layout of the reporter
            LinearLayout reporterLin = (LinearLayout) findViewById(R.id.reporter_ll);

            // if there are a reporter object then show the details, otherwise hide the linear layout
            if (reportedChat.getReporter() == null) {
                reporterLin.setVisibility(View.GONE);
            } else {
                TextView reporter = new TextView(this);
                reporter.setTextSize(25);
                reporter.setTextColor(Color.BLACK);
                reporter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                reporter.setText(getString(R.string.reporter));
                reporterLin.addView(reporter);

                show_user_data(reportedChat.getReporter(), reporterLin);
            }


            // get linear layout of the reported
            LinearLayout reportedLin = (LinearLayout) findViewById(R.id.reported_ll);
            // if there are a reported object then show the details, otherwise hide the linear layout
            if (reportedChat.getReported() == null) {
                reportedLin.setVisibility(View.GONE);
            } else {
                TextView reported = new TextView(this);
                reported.setTextSize(25);
                reported.setTextColor(Color.BLACK);
                reported.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                reported.setText(getString(R.string.reported));
                reportedLin.addView(reported);
                show_user_data(reportedChat.getReported(), reportedLin);
            }


            // text view to show he chat history
            final TextView chatHistory = (TextView) findViewById(R.id.chat_history);

            // get the node of this chat and get the messages
            DatabaseReference chat = databaseReference.child(reportedChat.getChatKey()).child("messages");
            chat.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                    String message = cm.getContent();
                    String username = cm.getAuthor();
                    String time = cm.getTime();

                    Log.d("Chat ", message+" , "+username);
                    if (Build.VERSION.SDK_INT >= 24) {
                        chatHistory.append(Html.fromHtml("<strong>" + username +
                                " : </strong><br>\t" + message + "<br>" + time + "<br><br>", Html.FROM_HTML_MODE_COMPACT));

                    } else {
                        chatHistory.append(username + " : \r\n\t" + message + "\r\n" + time + "\r\n\r\n");
                    }
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
    }

    private void show_user_data(User user, LinearLayout ll) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


        TextView name = new TextView(this);
        name.setLayoutParams(params);
        name.setTextSize(16);
        name.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        name.setText(getString(R.string.name) + " : " + user.getUser_name());

        TextView phone = new TextView(this);
        phone.setTextSize(16);
        phone.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        phone.setText(getString(R.string.hint_phone) + " : " + user.getUser_phone());

        TextView email = new TextView(this);
        email.setTextSize(16);
        email.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        email.setText(getString(R.string.hint_email) + " : " + user.getUser_email());


        ll.addView(name);
        ll.addView(phone);
        ll.addView(email);
    }
}
