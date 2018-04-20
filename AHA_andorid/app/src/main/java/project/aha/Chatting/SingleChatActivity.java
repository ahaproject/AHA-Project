package project.aha.Chatting;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.admin_panel.AdminMainActivity;
import project.aha.admin_panel.ListDoctorsActivity;
import project.aha.admin_panel.ReportedChats;
import project.aha.admin_panel.SingleViewReportedChat;
import project.aha.interfaces.DialogCallBack;
import project.aha.interfaces.ReceiveResult;
import project.aha.doctor_panel.DoctorSingleView;
import project.aha.models.Chat;
import project.aha.models.ChatMessage;
import project.aha.models.ReportedChat;
import project.aha.models.User;
import project.aha.parent_panel.ParentSingleView;

public class SingleChatActivity extends AppCompatActivity implements ReceiveResult, DialogCallBack {


    private TextView conversation;
    private EditText edit_message;
    private DatabaseReference databaseReference = Constants.getChatsRef();
    private DatabaseReference root;
    private DatabaseReference messages;
    private Button send_btn;


    private Chat chat;
    private String chat_key;
    private int other_user_type;
    private int other_user_id;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Constants.showLogo(this);


        // force the scroller to go bottom
        final ScrollView scrollView = (ScrollView) findViewById(R.id.chat_scrollview);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });



        // get current user
        current_user = Constants.get_user_object(this);



        Intent i = getIntent();

        // get data from called intent
        chat = (Chat) i.getSerializableExtra("chat");
        other_user_id = i.getIntExtra("other_user_id", -1);
        other_user_type = i.getIntExtra("other_user_type", -1);
        chat_key = i.getStringExtra("chat_key");

        // if the chat is exist then resume chat
        if (chat != null) {
            resumeChat(chat);
        } else if (other_user_id > -1) { // if the chat is null and there is the other user id the start new chat
            newChat(other_user_id, current_user.getUser_id(), current_user);
        } else if (chat_key != null) {
            fetchedChat(chat_key);
        }


        //
        messages = root.child("messages");
        messages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                String message = cm.getContent();
                String username = cm.getAuthor();
                String time = cm.getTime();

                // add the chat content to the screen
                if (Build.VERSION.SDK_INT >= 24) {
                    conversation.append(Html.fromHtml("<strong>" + username +
                            " : </strong><br>\t" + message + "<br>" + time + "<br><br>", Html.FROM_HTML_MODE_COMPACT));

                } else {
                    conversation.append(username + " : \r\n\t" + message + "\r\n" + time + "\r\n\r\n");
                }
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });


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

        edit_message = (EditText) findViewById(R.id.edit_message);
        conversation = (TextView) findViewById(R.id.conversation);
        send_btn = (Button) findViewById(R.id.send_button);
        // when the user clicks on send button
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = edit_message.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    final DatabaseReference chat_obj = messages.push();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    chat_obj.setValue(new ChatMessage(other_user_id, current_user.getUser_name(), message, currentDateTimeString, "no"));
                    edit_message.setText("");
                }
            }
        });
    }

    private void fetchedChat(String chat_key) {
        root = databaseReference.child(chat_key);
        String[] id_string = chat_key.split(Constants.CHAT_USERS_SEPARATOR);
        int id_1 = Integer.parseInt(id_string[0]), id_2 = Integer.parseInt(id_string[1]);
        other_user_id = (id_1 == current_user.getUser_id()) ? id_2 : id_1;
    }

    private void newChat(final int other_user_id, int current_user_id, User current_user) {
        int maxID = Math.max(other_user_id, current_user_id), minID = Math.min(other_user_id, current_user_id);
        chat_key = minID + Constants.CHAT_USERS_SEPARATOR + maxID;
        root = databaseReference.child(chat_key);
        root.child(String.valueOf(current_user_id)).setValue(current_user);

        // get other user object
        new DatabasePostConnection(this, false, Constants.SELECT_USER, true).postRequest(
                new HashMap<String, String>() {{
                    put(Constants.CODE, String.valueOf(Constants.SELECT_USER));
                    put(Constants.USER_ID_META, other_user_id + "");
                }},
                Constants.DATABASE_URL);

    }

    private void resumeChat(Chat chat) {
        root = databaseReference.child(chat.getChatKey());

        chat_key = chat.getChatKey();
        String[] id_string = chat_key.split(Constants.CHAT_USERS_SEPARATOR);

        int id_1 = Integer.parseInt(id_string[0]), id_2 = Integer.parseInt(id_string[1]);

        other_user_id = (id_1 == current_user.getUser_id()) ? id_2 : id_1;
    }

    @Override // when receive result from database
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String function_result = output.getString(Constants.RESULT);
            switch (function_result) {
                case Constants.RECORDS: // if successful !
                    output = output.getJSONArray("data").getJSONObject(0);

                    other_user_id = output.getInt(Constants.USER_ID_META);
                    String user_email = output.getString(Constants.USER_EMAIL_META);
                    String user_phone = output.optString(Constants.USER_PHONE_META, "");
                    String user_name = output.optString(Constants.USER_NAME_META, "");
                    other_user_type = output.getInt(Constants.USER_TYPE_META);
                    User user = new User(other_user_id, user_email, user_name, other_user_type, user_phone);
                    root.child(String.valueOf(user.getUser_id())).setValue(user);
                    setTitle(user.getUser_name());
                    send_btn.setEnabled(true);
                    break;


                case Constants.SCF_REPORTING: {
                    output = output.getJSONArray("data").getJSONObject(0);

                    int r_id = output.getInt("rc_id");
                    User reporter = null, reported = null;
                    JSONArray reporterObj = output.optJSONArray("reporter");
                    JSONArray reportedObj = output.optJSONArray("reported");


                    if (reporterObj != null && reporterObj.length() > 0) {
                        reporter = Constants.readAndSaveUser(reporterObj.getJSONObject(0), false);
                    }

                    if (reportedObj != null && reportedObj.length() > 0) {
                        reported = Constants.readAndSaveUser(reportedObj.getJSONObject(0), false);
                    }


                    String chatKey = output.getString("chat_key");
                    String reason = output.optString("report_reason");
                    String date = output.getString("reported_datetime");
                    int is_solved = output.getInt("is_solved");


                    ReportedChat rch = new ReportedChat(r_id, reporter, reported, chatKey, date, is_solved, reason, "no");
                    DatabaseReference databaseReference = Constants.getReportsRef();
                    DatabaseReference newReport = databaseReference.push();
                    newReport.setValue(rch);
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (other_user_type < 0) {
            other_user_type = (chat != null) ? chat.getOther_user().getUser_type() : -1;
        }

        if (other_user_type > Constants.ADMIN_TYPE) {
            inflater.inflate(R.menu.user_info_bar, menu);
            menu.findItem(R.id.user_info).setVisible(true);
            menu.findItem(R.id.report_abuse).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null)
            return false;


        switch (item.getItemId()) {
            case R.id.user_info:
                if (other_user_type == Constants.PARENT_TYPE) {
                    Intent intent = new Intent(this, ParentSingleView.class);
                    intent.putExtra("id", other_user_id);
                    startActivity(intent);
                }
                if (other_user_type == Constants.DOCTOR_TYPE) {
                    //                     add button for parent to choose this doctor to be the medical history
                    Intent intent = new Intent(this, DoctorSingleView.class);
                    intent.putExtra("id", other_user_id);
                    startActivity(intent);
                }

                return true;


            case R.id.report_abuse: {
                Constants.showConfirmDeleteDialog(this, getString(R.string.confirm_report_abuse), getString(R.string.report_abuse));

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override // the called function when the show dialog clicks on OK
    public void dialogCallBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.report_reason));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String report_reason = input.getText().toString();
                Constants.report_abuse(SingleChatActivity.this, (User) current_user, other_user_id, chat_key, report_reason);
                Toast.makeText(SingleChatActivity.this, getString(R.string.scf_report_abuse), Toast.LENGTH_LONG).show();

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

}


