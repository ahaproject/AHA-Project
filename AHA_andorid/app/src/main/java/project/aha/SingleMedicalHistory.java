package project.aha;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import project.aha.interfaces.ReceiveResult;
import project.aha.models.MedicalHistory;
public class SingleMedicalHistory extends AppCompatActivity implements ReceiveResult {
    private TextView date, score, result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_medical_history);
        Constants.showLogo(this);

        date = (TextView) findViewById(R.id.date);
        score = (TextView) findViewById(R.id.score);
        result = (TextView) findViewById(R.id.result);


        Intent i = getIntent();
        if (i != null ) {
            MedicalHistory m = (MedicalHistory) i.getSerializableExtra("med_hist");
            date.setText(m.getDate());
            score.setText(m.getScore()+"");
            result.setText(Constants.getCARSResult(this,m.getScore()));

            DatabasePostConnection conn = new DatabasePostConnection(this);
            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CODE, Constants.SELECT_TEST_HISTORY + "");
            data.put(Constants.MEDICAL_HISTORY_ID_META, m.getMedical_hist_id() + "");
            conn.postRequest(data, Constants.DATABASE_URL);
        }
    }
    @Override
    public void onReceiveResult(String resultJson) {
        Log.d("RESULT JSON", resultJson);
        try {
            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch (result) {
                case Constants.RECORDS: {
                    showTestHistory(output);
                    break;
                }

                default: {
                    Log.d("error in update ", result);
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showTestHistory(JSONObject output) {
        try {
            // get linear layout to add all history
            LinearLayout lin = (LinearLayout) findViewById(R.id.history);
            TextView mQuestion , mAnswer ;

            JSONArray data = output.getJSONArray("data");

            for(int i = 0 ; i < data.length() ; i++){
                JSONObject obj = data.getJSONObject(i);

                String question = obj.getString("question");
                mQuestion = new TextView(this);
                if(Build.VERSION.SDK_INT >= 23) {
                    mQuestion.setTextAppearance(R.style.QuestionText);
                }
                mQuestion.setText(question);




                String answer = obj.getString("answer");
                mAnswer = new TextView(this);
                mAnswer.setText(answer);


                View divider = new View(this);
                divider.setBackgroundColor(getResources().getColor(R.color.aha_basic));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 40, 0, 40);
                divider.setLayoutParams(params);

                lin.addView(mQuestion);
                lin.addView(mAnswer);
                lin.addView(divider);
//                divider.getLayoutParams().setMargins(0, 60, 0, 60);

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
        return Constants.handleItemChoosed(this ,super.onOptionsItemSelected(item),item);
    }
}