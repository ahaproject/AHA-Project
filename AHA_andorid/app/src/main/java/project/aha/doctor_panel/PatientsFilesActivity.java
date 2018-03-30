package project.aha.doctor_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.models.Parent;

public class PatientsFilesActivity extends AppCompatActivity implements ReceiveResult{

    private ListView patients_listview;
    private TextView name ;
    private TextView phone ;
    private TextView file_num ;

    private ArrayList<Parent> patientsObjects;
    private ArrayList<String> patientsNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_files);


        patients_listview = (ListView) findViewById(R.id.patients_listview);
        name = (TextView)findViewById(R.id.name);
        phone = (TextView)findViewById(R.id.phone);
        file_num = (TextView)findViewById(R.id.file_num);
        patientsObjects = new ArrayList<>();
        patientsNames = new ArrayList<>();


        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh(){
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.LIST_PARENTS+"");

        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data,Constants.DATABASE_URL);
    }


    @Override
    public void onReceiveResult(String resultJson) {
        try {
            patients_listview.setAdapter(null);
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            Log.d("pat",resultJson);
            String result = output.getString(Constants.RESULT);

            switch(result){

                // if there are records -> fill list view
                case Constants.PARENTS_RECORDS :{
                    // set them to visible
                    fill_listView_with_parents(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_PARENTS_RECORDS:{
                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) no_records_text.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    no_records_text.setLayoutParams(lp);
                    no_records_text.setText(getString(R.string.no_parents_records));
                    no_records_text.setVisibility(View.VISIBLE);
                    break;
                }

                // if the admin try to delete parent and there are an error -> print error
                case Constants.ERR_DELETE_PARENT:{
                    Toast.makeText(this, getString(R.string.err_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }

                // if the admin try to delete parent and it success -> show successful text
                case Constants.SCF_DELETE_PARENT:{
                    Toast.makeText(this, getString(R.string.scf_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fill_listView_with_parents(JSONObject output) {
        try{

            // convert from JSON Array to ArrayList

            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);
                    int user_id = Integer.parseInt((String) jsonObject.get(Constants.USER_ID_META));
                    String user_name = (String) jsonObject.get(Constants.USER_NAME_META);
                    String file_number = (String) jsonObject.get(Constants.PARENT_FILE_NUMBER);
                    patientsObjects.add(new Parent(user_id, user_name, Constants.PARENT_TYPE , file_number));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************
            // add only parents names and specialize to the list view
            for (Parent single_parent : patientsObjects ) {
                String content;
                if(single_parent.getFileNumber().equalsIgnoreCase("0")){
                    content = single_parent.getUser_name()+" [ No File Number ]";
                } else{
                    content = single_parent.getUser_name()+" [ "+getString(R.string.file_number)+" : "+single_parent.getFileNumber()+" ]";
                }
                patientsNames.add(content);
            }
            // ********************************************************************************************

            // Create ArrayAdapter which adapt array list to list view.
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.user_single_view, patientsNames);
            patients_listview.setAdapter(listAdapter);

            // add action when the admin clicks on parent record in the listview
            patients_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {

                    Parent p = patientsObjects.get(position);
                    name.setText(p.getChildName());
                    phone.setText(p.getUser_phone());
                    file_num.setText(p.getFileNumber());

                }
            });

        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }
}
