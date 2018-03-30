package project.aha.doctor_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.SingleExerciseView;
import project.aha.models.Exercise;

public class DoctorExercises extends AppCompatActivity implements ReceiveResult{


    ListView listView;

    private ListAdapter listAdapter;

    // Search EditText

    private List<Exercise> exercisesObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_exercises);
        Constants.showLogo(this);


        listView = (ListView) findViewById(R.id.list_view);
        exercisesObjects = new ArrayList<>();

        // call database to get all doctor exercises
        HashMap<String,String> data = new HashMap<>();
        data.put(Constants.CODE , Constants.LIST_EXERCISES+"");
        data.put(Constants.DOCTOR_ID_META , Constants.get_current_user_id(this)+"");

        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data,Constants.DATABASE_URL);
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            Log.d("pat",resultJson);
            String result = output.getString(Constants.RESULT);

            switch(result){

                // if there are records -> fill list view
                case Constants.RECORDS :{
                    // set them to visible
                    fill_listView(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_RECORDS:{
                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) no_records_text.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    no_records_text.setLayoutParams(lp);
                    no_records_text.setText(getString(R.string.no_records));
                    no_records_text.setVisibility(View.VISIBLE);
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fill_listView(JSONObject output) {
        try{
            listView.setAdapter(null);
//            exercisesObjects.clear();

            // convert from JSON Array to ArrayList

            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);

                    int ex_id = jsonObject.getInt(Constants.EXERCISE_ID_META);
                    String subject = jsonObject.getString(Constants.EXERCISE_SUBJECT);
                    int diagnose_id = jsonObject.getInt(Constants.EXERCISE_DIAGNOSE);
                    StringBuilder description = new StringBuilder(jsonObject.getString(Constants.EXERCISE_DESCRIPTION));
                    String added_date = jsonObject.getString("added_date");
                    Object imgsObj = jsonObject.get("img");
                    String img_path = null;

                    // `instanceof` tells us whether the object can be cast to a specific type
                    if (imgsObj instanceof JSONArray)
                    {
                        JSONArray imgs = (JSONArray)imgsObj;
                        if(imgs != null){
                            JSONObject img = imgs.getJSONObject(0);
                            img_path = img.getString(Constants.PICTURE_PATH);
                        }
                    }

                    Object vidsObj = jsonObject.get("vid");
                    String vid_path = null;

                    // `instanceof` tells us whether the object can be cast to a specific type
                    if (vidsObj instanceof JSONArray)
                    {
                        JSONArray vids = (JSONArray)vidsObj;
                        if(vids != null){
                            JSONObject vid = vids.getJSONObject(0);
                            vid_path = vid.getString(Constants.VIDEO__PATH);
                        }
                    }

                    Exercise ex = new Exercise(ex_id,subject , Constants.get_current_user_id(this) ,
                            description , Constants.diagnoses.get(diagnose_id).getName(),added_date);
                    ex.setImage(img_path);
                    ex.setVideo(vid_path);
                    exercisesObjects.add(ex);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // ********************************************************************************************

            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(DoctorExercises.this, exercisesObjects);
//            listAdapter.
            listView.setAdapter(listAdapter);

            // add action when the admin clicks on parent record in the listview
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Exercise e = exercisesObjects.get(position);
                    Intent intent = new Intent(DoctorExercises.this, SingleExerciseView.class);
                    intent.putExtra("exercise", e);
                    startActivity(intent);
                }
            });

        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }

}
