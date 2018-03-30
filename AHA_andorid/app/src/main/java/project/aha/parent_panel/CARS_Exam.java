package project.aha.parent_panel;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.ReceiveResult;
import project.aha.models.CARS_Answer;
import project.aha.models.CARS_Question;
import project.aha.models.Doctor;

public class CARS_Exam extends AppCompatActivity implements ReceiveResult {


    ArrayList<CARS_Question> all_questions ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_exam);


        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.CARS_EXAM + "");
        DatabasePostConnection con = new DatabasePostConnection(this);
        con.postRequest(data, Constants.DATABASE_URL);


        all_questions = new ArrayList<>();
    }

    @Override
    public void onReceiveResult(String resultJson) {
        Log.d("RESULT JSON", resultJson);

        if (resultJson == null || resultJson.length() < 1) {
            Log.d("RESULT JSON", "NULL RESULT");
            return;
        }
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            JSONArray questionsJSON = output.getJSONArray("data");
            for (int i = 0; i < questionsJSON.length(); i++) {
                JSONObject questionObj = questionsJSON.getJSONObject(i);

                int q_id = questionObj.getInt("q_id");
                String question = questionObj.getString("question");

                CARS_Question q = new CARS_Question(q_id , question);

                JSONArray answersJSON = questionObj.getJSONArray("ans");
                for(int j = 0 ; j < answersJSON.length() ; j++){
                    JSONObject ansObj = answersJSON.getJSONObject(j);

                    int a_id = ansObj.getInt("a_id");
                    String answer = ansObj.getString("answer");
                    double grade = ansObj.getDouble("grade");

                    q.addAnswer(a_id , answer , grade);

                }

                all_questions.add(q);
            }



            showExam();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showExam() {

        CARS_Question qq = all_questions.get(0);
        all_questions.set(0, all_questions.get(1));
        all_questions.set(1 , qq);




        LinearLayout form = (LinearLayout) findViewById(R.id.exam_layout);

        for(CARS_Question q : all_questions){


            // section -> question(TextView) , Radion buttons
            LinearLayout section = new LinearLayout(this);

            section.setOrientation(LinearLayout.VERTICAL);


            // width , height
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // margin -> separator
            layoutParams.setMargins(0, 60, 0, 60);
            section.setLayoutParams(layoutParams);





            // textview - > question
            TextView question = new TextView(this);
            // question
            question.setText(q.getQuestion());
            // size
            question.setTextSize(23);
            question.setTypeface(question.getTypeface(), Typeface.BOLD);



            section.addView(question);



            ArrayList<CARS_Answer> answers = q.getAnswers();
            final RadioButton [] rb = new RadioButton[q.getAnswers().size()];
            RadioGroup rg = new RadioGroup(this); //create the RadioGroup
            rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
            rg.setId(q.getQ_id());
            for(int i = 0 ; i < rb.length ; i++){
                rb[i]  = new RadioButton(this);
                rb[i].setText((i+1)+". " + answers.get(i).getAnswer());
                rb[i].setId(answers.get(i).getA_id());


                rg.addView(rb[i]);
            }



            section.addView(rg);//you add the whole RadioGroup to the layout



            form.addView(section);
        }
    }
}
