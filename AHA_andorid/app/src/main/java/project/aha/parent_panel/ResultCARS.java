package project.aha.parent_panel;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.widget.LinearLayout.LayoutParams;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.ExercisesList;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.CARS_Answer;
import project.aha.models.CARS_Question;
import project.aha.models.Diagnose;
import project.aha.models.Parent;

public class ResultCARS extends AppCompatActivity implements ReceiveResult {


    private HashMap<Integer, CARS_Question> all_questions;

    private TextView result_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_cars);
        Constants.showLogo(this);

        Intent i = getIntent();
        if (i != null) {


            all_questions = (HashMap<Integer, CARS_Question>) i.getSerializableExtra(Constants.CARS_RESULTS);


            // sum results
            String diagnoses_ids_string = "";
            double sum = 0;
            for (CARS_Question q : all_questions.values()) {
                CARS_Answer ans = q.getAnswers().get(q.getAnswer_id());
                if (ans.getGrade() > 2) {
                    if(diagnoses_ids_string.length() == 0){
                        diagnoses_ids_string += q.getQ_id();
                    } else{
                        diagnoses_ids_string += Constants.PARENT_DIAGNOSES_SEPARATOR + q.getQ_id();
                    }
                }

                double grade = ans.getGrade();
                sum += grade;

            }
            if(diagnoses_ids_string.length() > 0 ) {
                Parent parent = (Parent) Constants.get_user_object(this);
                parent.setDiagnoses_ids(diagnoses_ids_string);
                Constants.update_user_object(this , parent);
            } else{
                Parent parent = (Parent) Constants.get_user_object(this);
                parent.setDiagnoses_ids(null);
                Constants.update_user_object(this , parent);
            }
            saveResultsDatabase(all_questions, sum, diagnoses_ids_string.trim());

            String resultAutism = Constants.getCARSResult(this, sum);
            result_textview = (TextView) findViewById(R.id.sum_cars_result);
            result_textview.setText(String.valueOf(sum));

            result_textview = (TextView) findViewById(R.id.string_cars_result);
            result_textview.setText(resultAutism);

            LinearLayout ll = (LinearLayout) findViewById(R.id.cars_exer_results);

            Constants.show_exam_results_based_diagnoses(this, all_questions, ll);
        }
    }


    private void saveResultsDatabase(HashMap<Integer, CARS_Question> all_questions, double sum, String diagnoses_ids_string) {

        HashMap<String, String> data = new HashMap<>();
        int parent_id = Constants.get_current_user_id(this);
        data.put(Constants.CODE, Constants.SAVE_CARS_RESULTS + "");
        data.put(Constants.PARENT_ID_META, parent_id + "");
        data.put(Constants.CARS_RESULTS, sum + "");
        if (diagnoses_ids_string.length() > 0) {
            data.put("diagnoses_ids", diagnoses_ids_string);
        }


        int count_data = all_questions.size();
        data.put("count", count_data + "");

        int i = 0;
        for (CARS_Question q : all_questions.values()) {
            data.put("q_" + i, q.getQuestion() + "");
            data.put("a_" + i, q.getAnswers().get(q.getAnswer_id()).getAnswer() + "");
            i++;
        }

        DatabasePostConnection conn = new DatabasePostConnection(this);
        conn.postRequest(data, Constants.DATABASE_URL);

    }


    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.ERR_SAVE_EXAM_RESULT: {
                    Toast.makeText(this, "Error in save exam results", Toast.LENGTH_SHORT).show();
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.SCF_SAVE_EXAM_RESULT: {
                    TextView scf_save_result = (TextView) findViewById(R.id.scf_save_result);
                    scf_save_result.setText(getString(R.string.scf_save_result));
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
