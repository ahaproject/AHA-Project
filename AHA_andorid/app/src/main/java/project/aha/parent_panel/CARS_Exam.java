package project.aha.parent_panel;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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


    HashMap<Integer, CARS_Question> all_questions;

    private Button finishBtn;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_exam);




        // list all q and a
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.CARS_EXAM + "");
        DatabasePostConnection con = new DatabasePostConnection(this);
        con.postRequest(data, Constants.DATABASE_URL);


        all_questions = new HashMap<>();


        error = (TextView) findViewById(R.id.error);

        finishBtn = (Button) findViewById(R.id.finish_exam);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compute_results();
            }


        });
    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            JSONArray questionsJSON = output.getJSONArray("data");
            for (int i = 0; i < questionsJSON.length(); i++) {
                JSONObject questionObj = questionsJSON.getJSONObject(i);

                int q_id = questionObj.getInt("q_id");
                String question = questionObj.getString("question");

                CARS_Question q = new CARS_Question(q_id, question);

                JSONArray answersJSON = questionObj.getJSONArray("ans");
                for (int j = 0; j < answersJSON.length(); j++) {
                    JSONObject ansObj = answersJSON.getJSONObject(j);

                    int a_id = ansObj.getInt("a_id");
                    String answer = ansObj.getString("answer");
                    double grade = ansObj.getDouble("grade") - 0.5;

                    q.addAnswer(a_id, answer, grade);

//                    Log.d("Res", "Q : "+q_id+" | A : "+a_id +" | G:"+grade);

                }

                all_questions.put(q_id, q);
            }


            showExam();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showExam() {


        LinearLayout form = (LinearLayout) findViewById(R.id.exam_layout);

        for (CARS_Question q : all_questions.values()) {


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
            question.setText(q.getQ_id()+". "+q.getQuestion());
//            Log.d("Res" , q.getQ_id()+". "+q.getQuestion());
            // size
            question.setTextSize(23);
            question.setTypeface(question.getTypeface(), Typeface.BOLD);


            section.addView(question);


            RadioGroup rg = new RadioGroup(this); //create the RadioGroup
            rg.setOrientation(RadioGroup.VERTICAL);//RadioGroup.VERTICAL
            rg.setId(q.getQ_id());

            RadioButton rb;
            for (CARS_Answer answers : q.getAnswers().values()) {
                rb = new RadioButton(this);
                rb.setText(answers.getAnswer());
                rb.setId(answers.getA_id());
//                Log.d("Res" , answers.getA_id()+". "+answers.getAnswer());


                rg.addView(rb);
            }

            section.addView(rg);//you add the whole RadioGroup to the layout
            form.addView(section);
        }
    }


    private void compute_results() {
        error.setText("");
        error.setVisibility(View.GONE);

        LinearLayout form = (LinearLayout) findViewById(R.id.exam_layout);
        int count = form.getChildCount();

        int linCount;
        RadioGroup rg;
        LinearLayout section;


        // **** Loop through exam linear layout
        for (int i = 0; i < count; i++) {

            View o = form.getChildAt(i);

            // **** if this is a section
            if (o instanceof LinearLayout) {
                section = (LinearLayout) o;
                linCount = section.getChildCount();


                // **** Loop through section linear layout
                for (int j = 0; j < linCount; j++) {
                    o = section.getChildAt(j);

                    // if this is a radio group
                    if (o instanceof RadioGroup) {
                        rg = (RadioGroup) o;

                        int selectedAnswerID = rg.getCheckedRadioButtonId();
                        if (selectedAnswerID == -1) {
                            error.setText(getString(R.string.answer_all_exam_questions));
                            error.setVisibility(View.VISIBLE);
                            return;
                        }

                        all_questions.get(rg.getId()).setAnswer_id(selectedAnswerID);
                        break;
                    }
                }
            }
        }

        Intent i = new Intent(this , ResultCARS.class);
        i.putExtra(Constants.CARS_RESULTS , all_questions);
        startActivity(i);
        finish();
    }
}
