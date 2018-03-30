package project.aha.admin_panel;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabaseAsyncTask;
import project.aha.R;
import project.aha.models.Doctor;
import project.aha.models.Parent;

public class DeleteParentActivity extends AppCompatActivity {

    ListView parentListView;
    ArrayList<String> parentsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_parent);


        parentListView = (ListView) findViewById(R.id.parents_list);
        parentsArray = new ArrayList<>();

        new DatabaseAsyncTask(this, this, Constants.LIST_PARENTS, null).execute();



    }


    public void fillParentsArray(Object result){
        Log.d("Enter fill array " , "Enetere");

        final ArrayList<Parent> parentArrayList = (ArrayList<Parent>)result ;
        for (Parent parent : parentArrayList ) {
            String content = parent.getUser_name()+" [ "+parent.getFileNumber()+" ]";
            Log.d("content of doctor list" , content);
            parentsArray.add(content);
        }
        // Create ArrayAdapter using the planet list.
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, R.layout.doctor_single_view, parentsArray);
        parentListView.setAdapter(listAdapter);

        parentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                new AlertDialog.Builder(DeleteParentActivity.this)
                        .setTitle(getString(R.string.delete_parent_title))
                        .setMessage(getString(R.string.confirm_delete))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Parent parent = parentArrayList.get(position);
                                HashMap<Object,Object> data = new HashMap<>();
                                data.put(Constants.USER_ID_META , parent.getUser_id());
                                new DatabaseAsyncTask(DeleteParentActivity.this,DeleteParentActivity.this,Constants.DELETE_PARENT ,data).execute();
                                parentsArray.remove(position);
                                listAdapter.notifyDataSetChanged();

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


    }

    public void showDeletionResult(Object result) {
        String str = (String)result;

        switch(str){
            case Constants.ERR_DELETE_PARENT:{
                Toast.makeText(this, getString(R.string.err_delete_parent), Toast.LENGTH_LONG).show();
                break;
            }

            case Constants.SCF_DELETE_PARENT:{
                Toast.makeText(this, getString(R.string.scf_delete_parent), Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}
