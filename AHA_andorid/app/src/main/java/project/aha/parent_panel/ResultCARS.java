package project.aha.parent_panel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.ExercisesList;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.CARS_Answer;
import project.aha.models.CARS_Question;

public class ResultCARS extends AppCompatActivity implements ReceiveResult{


    private HashMap<Integer , CARS_Question> all_questions ;

    private TextView result_textview;

    private Button go_exercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_cars);
        Constants.showLogo(this);

        Intent i = getIntent();
        if(i != null) {


            go_exercises = (Button) findViewById(R.id.go_exercises);
            go_exercises.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ResultCARS.this , ExercisesList.class));
                }
            });



            result_textview = (TextView) findViewById(R.id.show_cars_result);

            all_questions = (HashMap<Integer, CARS_Question>) i.getSerializableExtra(Constants.CARS_RESULTS);



            // sum results
            double sum = 0;
            for (CARS_Question q : all_questions.values()) {
                int id = q.getAnswer_id();
                CARS_Answer ans = q.getAnswers().get(id);
                double grade = ans.getGrade();
                sum += grade;

//                Log.d("CARS", "Q : " + q.getQ_id() + " | A : " + id + " | grade : " + grade);


            }
            saveResultsDatabase(all_questions , sum);

            String resultAutism = Constants.getCARSResult(this,sum);

            result_textview.setText(sum+"\r\n"+resultAutism);

        }
    }

    private void saveResultsDatabase(HashMap<Integer, CARS_Question> all_questions , double sum) {

//        INSERT INTO `Med_Hist`(`parent_id`, `med_id`, `score`, `date`, `diag_id`, `doctor_id`)
//        VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6])

        // INSERT INTO `Test_Hist`(`med_id`, `a_q_id`, `question`, `answer`) VALUES ([value-1],[value-2],[value-3],[value-4])
        HashMap<String , String> data = new HashMap<>();
        int parent_id = Constants.get_current_user_id(this);
        data.put(Constants.CODE , Constants.SAVE_CARS_RESULTS+"");
        data.put(Constants.PARENT_ID_META , parent_id+"");
        data.put(Constants.CARS_RESULTS , sum+"");

        int count_data = all_questions.size() ;
        data.put("count" , count_data+"");

        int i = 0 ;
        for(CARS_Question q : all_questions.values()){
            data.put("q_"+i ,q.getQuestion()+"");
            data.put("a_"+i ,q.getAnswers().get(q.getAnswer_id()).getAnswer()+"");
            i++;
        }

        DatabasePostConnection conn = new DatabasePostConnection(this);
        conn.postRequest(data , Constants.DATABASE_URL);

    }



    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch(result){

                // if there are records -> fill list view
                case Constants.ERR_SAVE_EXAM_RESULT :{
                    Toast.makeText(this, "Error in save exam results", Toast.LENGTH_SHORT).show();
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.SCF_SAVE_EXAM_RESULT:{
                    TextView scf_save_result = (TextView)findViewById(R.id.scf_save_result);
                    scf_save_result.setText(getString(R.string.scf_save_result));
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
