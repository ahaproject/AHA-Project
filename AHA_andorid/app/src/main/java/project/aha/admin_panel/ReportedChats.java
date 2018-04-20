package project.aha.admin_panel;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.ListAdapter;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.Parent;
import project.aha.models.ReportedChat;
import project.aha.models.User;
import project.aha.parent_panel.ParentSingleView;

public class ReportedChats extends AppCompatActivity implements ReceiveResult{

    // list view
    private ListView listView;
    private ListAdapter listAdapter;
    private final List<ReportedChat> reportedChats = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_chats);

        listView = (ListView) findViewById(R.id.list_view);

        // send request to database to get all reported chats
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.LIST_REPORTED_CHATS + "");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data, Constants.DATABASE_URL);
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);
            switch (result) {
                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    listView.setVisibility(View.VISIBLE);
                    findViewById(R.id.no_records).setVisibility(View.GONE);
                    fill_listView(output);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fill_listView(JSONObject output) {
        try {
            // convert from JSON Array to ArrayList
            JSONArray reportedChatsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < reportedChatsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = reportedChatsJSONArray.getJSONObject(i);

                    int r_id = jsonObject.getInt("rc_id"); // reported chats id
                    User reporter = null , reported = null;
                    JSONArray reporterObj = jsonObject.optJSONArray("reporter");
                    JSONArray reportedObj = jsonObject.optJSONArray("reported");


                    if(reporterObj != null && reporterObj.length() > 0) { // if there are reporter object
                        reporter = Constants.readAndSaveUser(reporterObj.getJSONObject(0), false);
                    }

                    if(reportedObj != null && reportedObj.length() > 0) {  // if there are reported object
                        reported = Constants.readAndSaveUser(reportedObj.getJSONObject(0), false);
                    }


                    String chatKey = jsonObject.getString("chat_key"); // get chat key
                    String reason = jsonObject.optString("report_reason"); // get why th user reported
                    String date = jsonObject.getString("reported_datetime"); // the date
                    int is_solved = jsonObject.getInt("is_solved"); // is it solved or not

                    // add the reported chat object to the arraylist
                    reportedChats.add(new ReportedChat(r_id,reporter,reported,chatKey,date,is_solved,reason , "yes"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************
            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(this, reportedChats);
            //            listAdapter.
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent i = new Intent(ReportedChats.this, SingleViewReportedChat.class);
                    i.putExtra("reported_chat", reportedChats.get(position));
                    startActivity(i);
                }
            });

        } catch (JSONException ex) {
            ex.printStackTrace();
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
