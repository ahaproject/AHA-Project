package project.aha.admin_panel;

import android.content.DialogInterface;
import android.widget.RelativeLayout.LayoutParams;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.doctor_panel.ListAdapter;
import project.aha.models.Doctor;
import project.aha.models.Parent;

public class DeleteParentActivity extends AppCompatActivity implements ReceiveResult{


    // list view of the parents
    ListView parentsList;

    final ArrayList<Parent> parents_objects = new ArrayList<>();

    TextView choose_parent_title;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_parent);
        Constants.showLogo(this);



        choose_parent_title = (TextView)findViewById(R.id.choose_parent_title);

        // get the view list
        parentsList = (ListView) findViewById(R.id.parents_list);

        // set them to not visible until we ensure that there are records
        choose_parent_title.setVisibility(View.GONE);
        parentsList.setVisibility(View.GONE);


        // send request to database to get all parents
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.LIST_PARENTS+"");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data , Constants.DATABASE_URL);

    }
    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            String result = output.getString(Constants.RESULT);

            switch(result){

                // if there are records -> fill list view
                case Constants.RECORDS :{
                    // set them to visible
                    choose_parent_title.setVisibility(View.VISIBLE);
                    parentsList.setVisibility(View.VISIBLE);
                    fill_listView_with_parents(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_RECORDS:{
                    show_no_records();
                    break;
                }

                // if the admin try to delete parent and there are an error -> print error
                case Constants.ERR_DELETE_PARENT:{
                    Toast.makeText(this, getString(R.string.err_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }

                // if the admin try to delete parent and it success -> show successful text
                case Constants.SCF_DELETE_PARENT:{

                    int user_id = output.getInt(Constants.USER_ID_META);
                    delete_user(user_id,listAdapter);
                    if(parents_objects.size() == 0){
                        show_no_records();
                    }
                    Toast.makeText(this, getString(R.string.scf_delete_parent), Toast.LENGTH_LONG).show();
                    break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void show_no_records() {
        choose_parent_title.setVisibility(View.GONE);
        parentsList.setVisibility(View.GONE);
        TextView no_records_text = (TextView) findViewById(R.id.no_records);
        LayoutParams lp = (LayoutParams) no_records_text.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        no_records_text.setLayoutParams(lp);
        no_records_text.setText(getString(R.string.no_parents_records));
        no_records_text.setVisibility(View.VISIBLE);
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
                    parents_objects.add(new Parent(user_id, user_name, Constants.PARENT_TYPE ));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ********************************************************************************************

            listAdapter = new ListAdapter(this, parents_objects);
//            listAdapter.
            parentsList.setAdapter(listAdapter);

            // add action when the admin clicks on parent record in the listview
            parentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    showConfirmDeleteDialog(position, listAdapter);

                }
            });

            // Create ArrayAdapter which adapt array list to list view.
//            listAdapter = new ArrayAdapter<>(this, R.layout.user_single_view, parents_names);
//            parentsList.setAdapter(listAdapter);
//
//            // add action when the admin clicks on parent record in the listview
//            parentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        final int position, long id) {
//                    showConfirmDeleteDialog(position, listAdapter);
//
//                }
//            });

        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }

    private void showConfirmDeleteDialog(final int parent_record_position, final ListAdapter listAdapter) {

        // create dialog
        AlertDialog.Builder confirm_delete_dialog = new AlertDialog.Builder(this);

        // set title
        confirm_delete_dialog.setTitle(getString(R.string.delete_parent_title));

        // set message
        confirm_delete_dialog.setMessage(getString(R.string.confirm_delete));

        // set icon
        confirm_delete_dialog.setIcon(android.R.drawable.ic_dialog_alert);

        // add action when admin click on yes
        confirm_delete_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                removeParent(parent_record_position , listAdapter);
            }});

        // add action if the admin click on cancel
        confirm_delete_dialog.setNegativeButton(android.R.string.no, null);


        // show the dialog
        confirm_delete_dialog.show();

    }

    private void removeParent(int parent_record_position, ListAdapter listAdapter) {
        Parent parent = parents_objects.get(parent_record_position);
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.DELETE_PARENT+"");
        data.put(Constants.USER_ID_META , parent.getUser_id()+"");

        new DatabasePostConnection(this).postRequest(data , Constants.DATABASE_URL);
    }


    private void delete_user(int user_id, ListAdapter listAdapter) {
        for(Parent p : parents_objects){
            if(p.getUser_id() == user_id){
                parents_objects.remove(p);
                listAdapter.notifyDataSetChanged();
                return;
            }
        }
    }
}
