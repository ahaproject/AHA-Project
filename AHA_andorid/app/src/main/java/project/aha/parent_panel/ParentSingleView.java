package project.aha.parent_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.models.Parent;

public class ParentSingleView extends AppCompatActivity implements ReceiveResult{

    private Parent p ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_single_view);

        Intent i = getIntent() ;
        if(i != null){
             int id = i.getIntExtra("id" , -1);

            if(id > -1){
                HashMap<String , String> data = new HashMap<>();
                data.put(Constants.CODE , Constants.SELECT_PARENT+"");
                data.put(Constants.USER_ID_META , id+"");
                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data , Constants.DATABASE_URL);
            }
        }
    }

    @Override
    public void onReceiveResult(String resultJson) {

    }
}
