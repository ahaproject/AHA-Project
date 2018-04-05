package project.aha.Chatting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
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
import project.aha.interfaces.DialogCallBack;
import project.aha.interfaces.ReceiveResult;
import project.aha.doctor_panel.DoctorSingleView;
import project.aha.models.Chat;
import project.aha.models.ChatMessage;
import project.aha.models.User;
import project.aha.parent_panel.ParentSingleView;

public class SingleChatActivity extends AppCompatActivity implements ReceiveResult, DialogCallBack {


    TextView conversation;
    EditText edit_message;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
    DatabaseReference root;
    DatabaseReference messages;
    Button send_btn;


    Chat chat;
    String chat_key;
    int other_user_type;
    int other_user_id;
    int current_user_id;
    User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Constants.showLogo(this);


        final ScrollView scrollView = (ScrollView) findViewById(R.id.chat_scrollview);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


        current_user_id = Constants.get_current_user_id(this);
        current_user = Constants.get_user_object(this);


        Intent i = getIntent();
        chat = (Chat) i.getSerializableExtra("chat");
        other_user_id = i.getIntExtra("other_user_id", -1);
        other_user_type = i.getIntExtra("other_user_type", -1);


        User user;
        // resume chat
        if (chat != null) {
            resumeChat(chat);
        }

        if (other_user_id > -1) {
            newChat(other_user_id, current_user_id, current_user);
        }


        messages = root.child("messages");
        messages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
                String message = cm.getContent();
                String username = cm.getAuthor();
                String time = cm.getTime();

                if (Build.VERSION.SDK_INT >= 24) {
                    conversation.append(Html.fromHtml("<strong>" + username +
                            " : </strong><br>\t" + message + "<br>"+time+"<br>", Html.FROM_HTML_MODE_COMPACT));

                } else {
                    conversation.append(username + " : \r\n\t" + message + "\r\n"+time+"\r\n\r\n");
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
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = edit_message.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    final DatabaseReference chat_obj = messages.push();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    chat_obj.setValue(new ChatMessage(current_user.getUser_name(), message,currentDateTimeString));
                    edit_message.setText("");
                }
            }
        });
    }

    private void newChat(final int other_user_id, int current_user_id, User current_user) {
        int maxID = Math.max(other_user_id, current_user_id), minID = Math.min(other_user_id, current_user_id);
        chat_key = minID + Constants.CHAT_USERS_SEPARATOR + maxID;
        root = databaseReference.child(chat_key);
        root.child(String.valueOf(current_user_id)).setValue(current_user);

        // get other user object
        new DatabasePostConnection(this, false, Constants.SELECT_USER , true).postRequest(
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

        other_user_id = (id_1 == current_user_id) ? id_2 : id_1;
    }

    @Override // when receive result from database
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String function_result = output.getString(Constants.RESULT);
            switch (function_result) {
                case Constants.RECORDS: // if successful !
                    output = output.getJSONArray("data").getJSONObject(0);

                    int user_id = output.getInt(Constants.USER_ID_META);
                    String user_email = output.getString(Constants.USER_EMAIL_META);
                    String user_phone = output.optString(Constants.USER_PHONE_META, "");
                    String user_name = output.optString(Constants.USER_NAME_META, "");
                    int user_type = output.getInt(Constants.USER_TYPE_META);
                    User user = new User(user_id, user_email, user_name, user_type, user_phone);
                    root.child(String.valueOf(user.getUser_id())).setValue(user);
                    setTitle(user.getUser_name());
                    break;

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

            if (current_user.getUser_type() == Constants.DOCTOR_TYPE) {
                menu.findItem(R.id.report_abuse).setVisible(true);
            }
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

    @Override
    public void dialogCallBack() {
        Constants.report_abuse(this, other_user_id, current_user_id, chat_key);
        Toast.makeText(this, getString(R.string.scf_report_abuse), Toast.LENGTH_LONG).show();
    }
}


