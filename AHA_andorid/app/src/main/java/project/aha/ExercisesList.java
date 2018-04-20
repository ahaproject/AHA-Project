package project.aha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.aha.interfaces.ReceiveResult;
import project.aha.models.Diagnose;
import project.aha.models.Exercise;
import project.aha.models.Parent;

public class ExercisesList extends AppCompatActivity implements ReceiveResult {
    private ListView listView;
    private ListAdapter listAdapter;
    private List<Exercise> exercisesObjects;
    private int clickedPosition = -1;
    private Spinner diagnoses_filter;
    private String specialized;
    private int specialized_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);
        Constants.showLogo(this);

        Intent i = getIntent();
        if (i != null) {

            int doctor_id = i.getIntExtra(Constants.DOCTOR_ID_META, -1);
            int diagnose_id = i.getIntExtra(Constants.EXERCISE_DIAGNOSE, -1);
            int parent_id = i.getIntExtra(Constants.PARENT_ID_META, -1);

            // if this activity for doctor's execises
            if (doctor_id > -1) {
                setTitle(getString(R.string.my_exercises));
                // call database to get all doctor exercises
                HashMap<String, String> data = new HashMap<>();
                data.put(Constants.CODE, Constants.LIST_EXERCISES + "");
                data.put(Constants.DOCTOR_ID_META, doctor_id + "");

                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data, Constants.DATABASE_URL);
            }// if this activity for specific diagnose exercises
            else if (diagnose_id > -1) {
                // call database to get all doctor exercises
                HashMap<String, String> data = new HashMap<>();
                data.put(Constants.CODE, Constants.LIST_EXERCISES + "");
                data.put(Constants.EXERCISE_DIAGNOSE, diagnose_id + "");

                DatabasePostConnection connection = new DatabasePostConnection(this);
                connection.postRequest(data, Constants.DATABASE_URL);
            } else if (parent_id > -1) {
                Parent parent = (Parent) Constants.get_user_object(this);
                if(parent.getDiagnoses_ids() != null && parent.getDiagnoses_ids().length() > 0){
                    setTitle(getString(R.string.exercises_list));

                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constants.CODE, Constants.LIST_EXERCISES + "");
                    data.put("diagnoses_ids",  parent.getDiagnoses_ids());
                    DatabasePostConnection connection = new DatabasePostConnection(this);
                    connection.postRequest(data, Constants.DATABASE_URL);
                } else{
                    list_all_exercises();
                }
            } else {
                setTitle(getString(R.string.exercises_list));
                list_all_exercises();
            }
            listView = (ListView) findViewById(R.id.list_view);
            exercisesObjects = new ArrayList<>();
        }
    }

    public void list_all_exercises(){

        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.LIST_EXERCISES + "");
        DatabasePostConnection connection = new DatabasePostConnection(this);
        connection.postRequest(data, Constants.DATABASE_URL);
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");

            Log.d("pat", resultJson);
            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
                    no_records_text.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    fill_listView(output);
                    break;
                }

                case Constants.SCF_DELETE_EXERCISE: {
                    Toast.makeText(this, getString(R.string.scf_delete_exercise), Toast.LENGTH_LONG).show();
                    listAdapter.remove();
                    listAdapter.notifyDataSetChanged();

                    break;
                }

                case Constants.ERR_DELETE_EXERCISE: {
                    Toast.makeText(this, getString(R.string.err_delete_exercise), Toast.LENGTH_LONG).show();
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fill_listView(JSONObject output) {
        try {

            ArrayList<Diagnose> diagnoses = new ArrayList<>();
            diagnoses.add(new Diagnose(0, getString(R.string.all)));

            listView.setAdapter(null);

            // convert from JSON Array to ArrayList
            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {
                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);

                    int ex_id = jsonObject.getInt(Constants.EXERCISE_ID_META);
                    String subject = jsonObject.getString(Constants.EXERCISE_SUBJECT);
                    int diagnose_id = jsonObject.getInt(Constants.EXERCISE_DIAGNOSE);

                    Diagnose d = Constants.diagnoses.get(diagnose_id);
                    if (!diagnoses.contains(d)) {
                        diagnoses.add(d);
                    }


                    StringBuilder description = new StringBuilder(jsonObject.optString(Constants.EXERCISE_DESCRIPTION, ""));
                    String added_date = jsonObject.getString("added_date");
                    Object imgsObj = jsonObject.get("img");
                    String img_path = null;

                    // `instanceof` tells us whether the object can be cast to a specific type
                    if (imgsObj instanceof JSONArray) {
                        JSONArray imgs = (JSONArray) imgsObj;
                        if (imgs != null) {
                            JSONObject img = imgs.getJSONObject(0);
                            img_path = img.getString(Constants.PICTURE_PATH);
                        }
                    }

                    Object vidsObj = jsonObject.get("vid");
                    String vid_path = null;

                    // `instanceof` tells us whether the object can be cast to a specific type
                    if (vidsObj instanceof JSONArray) {
                        JSONArray vids = (JSONArray) vidsObj;
                        if (vids != null) {
                            JSONObject vid = vids.getJSONObject(0);
                            vid_path = vid.getString(Constants.VIDEO_PATH);
                        }
                    }

                    String diagnoseName = Constants.getDiagnose(diagnose_id);
                    Exercise ex = new Exercise(ex_id, subject, Constants.get_current_user_id(this),
                            description, diagnoseName, added_date);
                    ex.setImage(img_path);
                    ex.setVideo(vid_path);
                    exercisesObjects.add(ex);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // ********************************************************************************************

            listAdapter = new ListAdapter(ExercisesList.this, exercisesObjects);

            //            listAdapter.
            listView.setAdapter(listAdapter);

            // add action when the admin clicks on parent record in the listview
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Exercise e = exercisesObjects.get(position);
                    Intent intent = new Intent(ExercisesList.this, SingleExerciseView.class);
                    intent.putExtra("exercise", e);
                    startActivity(intent);
                }
            });


            // Create ArrayAdapter which adapt array list to list view.
            diagnoses_filter = (Spinner) findViewById(R.id.diagnoses_filter);
            if (Constants.diagnoses != null && !Constants.diagnoses.isEmpty()) {
                diagnoses_filter.setVisibility(View.VISIBLE);


                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_style, diagnoses);
                diagnoses_filter.setAdapter(spinnerAdapter);

                // set action when user clicks on specialize
                diagnoses_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Diagnose di = (Diagnose) parent.getItemAtPosition(position);
                        specialized = di.getName();
                        specialized_id = di.getId();
                        listAdapter.updateList(di);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        specialized = null;
                    }
                });
            }

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
        return Constants.handleItemChoosed(this, super.onOptionsItemSelected(item), item);
    }
}
