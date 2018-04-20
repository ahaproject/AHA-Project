package project.aha.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.LinkedHashMap;

import project.aha.Constants;
import project.aha.R;
import project.aha.admin_panel.AdminMainActivity;
import project.aha.models.Chat;
import project.aha.models.ChatMessage;
import project.aha.models.ReportedChat;
import project.aha.models.User;

public class ChatsActivity extends AppCompatActivity{

    DatabaseReference databaseReference = Constants.getChatsRef();

    RecyclerView chats_listview;
    ChatsAdapter listAdapter;
    LinkedHashMap<String,Chat> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Constants.showLogo(this);


        setTitle(R.string.title_chat);
        final int current_user_id = Constants.get_current_user_id(this);

        list = new LinkedHashMap<>();
        chats_listview = (RecyclerView) findViewById(R.id.chats_list);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chats_listview.getContext(),
                DividerItemDecoration.HORIZONTAL);
        chats_listview.addItemDecoration(dividerItemDecoration);


        chats_listview.setLayoutManager(new LinearLayoutManager(this));
        chats_listview.addOnItemTouchListener(
                new RecyclerItemClickListener(this, chats_listview ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                            Chat ch = listAdapter.getItem(position);
                            Intent intent = new Intent(ChatsActivity.this, SingleChatActivity.class);
                            intent.putExtra("chat", ch);
                            startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever

                    }
                })
        );

        listAdapter = new ChatsAdapter(this,this, list);
        chats_listview.setAdapter(listAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                upChatsListView(dataSnapshot , current_user_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    ///TODO
    // copy this method to the main class
    public void upChatsListView(DataSnapshot dataSnapshot , final int current_user_id){
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            final  String key = ds.getKey();
            if(!key.contains(Constants.CHAT_USERS_SEPARATOR)){
                continue;
            }
            String [] id_string = ds.getKey().split(Constants.CHAT_USERS_SEPARATOR);

            int id_1 = Integer.parseInt(id_string[0]) , id_2 = Integer.parseInt(id_string[1]);

            if(current_user_id != id_1 && current_user_id != id_2)
                continue;


            final int other_user_id = (id_1 == current_user_id)? id_2 : id_1 ;
            final User other_user = ds.child(String.valueOf(other_user_id)).getValue(User.class);


            if(ds.hasChild("messages")) {
                Log.i("hasChild" , "yes key "+key+" has messages");
                Query q = ds.child("messages").getRef().orderByKey().limitToLast(1);

                ChildEventListener valueEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);

                        Chat chat = new Chat(key, other_user, cm.getContent(), cm.getTime());
                        if(listAdapter.getByKey(key) == null) {
                            listAdapter.add(chat);
                        }else{
                            listAdapter.update(key, cm.getContent(), cm.getTime());
                        }
                        listAdapter.notifyDataSetChanged();
                        chats_listview.setVisibility(View.VISIBLE);
                        findViewById(R.id.no_chats).setVisibility(View.GONE);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                };
                q.addChildEventListener(valueEventListener);
            }
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