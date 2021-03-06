package project.aha.parent_panel;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import project.aha.Constants;
import project.aha.DatabasePostConnection;
import project.aha.R;
import project.aha.interfaces.ReceiveResult;
import project.aha.models.CARS_Answer;
import project.aha.models.CARS_Question;

public class CARS_Exam extends AppCompatActivity implements ReceiveResult {


    private LinkedHashMap<Integer, CARS_Question> all_questions;

    private Button finishBtn;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_exam);

        Constants.showLogo(this);



        // list all q and a
        HashMap<String, String> data = new HashMap<>();
        data.put(Constants.CODE, Constants.CARS_EXAM + "");
        DatabasePostConnection con = new DatabasePostConnection(this);
        con.postRequest(data, Constants.DATABASE_URL);


        all_questions = new LinkedHashMap<>();
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
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                    Log.d("Checked "+radioGroup.getId() , "id :"+i);
                }
            });


//            RadioButton rb;
            for (CARS_Answer answers : q.getAnswers().values()) {
                RadioButton rb = new RadioButton(this);
                rb.setText(answers.getAnswer());
                rb.setId(answers.getA_id());

                rg.addView(rb);
            }

            section.addView(rg);//you add the whole RadioGroup to the layout
            form.addView(section);
        }

        printViewHierarchy(form , "VIEW : ");
    }

    public static void printViewHierarchy(ViewGroup vg, String prefix) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            String desc = prefix + " | " + "[" + i + "/" + (vg.getChildCount()-1) + "] "+ v.getClass().getSimpleName() + " " + v.getId();
            Log.v("x", desc);

            if (v instanceof ViewGroup) {
                printViewHierarchy((ViewGroup)v, desc);
            }
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
        return Constants.handleItemChoosed(this ,super.onOptionsItemSelected(item),item);
    }
}
