package project.aha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import project.aha.interfaces.ReceiveResult;
import project.aha.models.MedicalHistory;

public class MedicalHistoryList extends AppCompatActivity implements ReceiveResult {


    private int parent_id;
    private ListAdapter listAdapter;
    private ListView medicalHistoriesListView;
    private List<MedicalHistory> medicalHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history_list);
        Constants.showLogo(this);


        medicalHistoriesListView = (ListView) findViewById(R.id.med_hist_list);
        medicalHistoryList = new ArrayList<>();

        Intent i = getIntent();
        if(i != null){
            parent_id = i.getIntExtra(Constants.PARENT_ID_META , -1);

            // call database to get all medical history list
            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CODE, Constants.LIST_MEDICAL_HISTORIES + "");
            data.put(Constants.PARENT_ID_META, parent_id + "");
            DatabasePostConnection connection = new DatabasePostConnection(this);
            connection.postRequest(data, Constants.DATABASE_URL);
        }

    }

    @Override
    public void onReceiveResult(String resultJson) {
        try {

            JSONObject output = new JSONObject(resultJson).getJSONObject("output");
            String result = output.getString(Constants.RESULT);

            switch (result) {

                // if there are records -> fill list view
                case Constants.RECORDS: {
                    // set them to visible
                    medicalHistoriesListView.setVisibility(View.VISIBLE);
                    fill_listView(output);
                    break;
                }

                // if there are no records -> show text view with no records text
                case Constants.NO_RECORDS: {
//                    medicalHistoriesListView.setVisibility(View.GONE);
//                    TextView no_records_text = (TextView) findViewById(R.id.no_records);
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) no_records_text.getLayoutParams();
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//                    no_records_text.setLayoutParams(lp);
//                    no_records_text.setText(getString(R.string.no_records));
//                    no_records_text.setVisibility(View.VISIBLE);
                    break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fill_listView(JSONObject output) {
        try {
            medicalHistoriesListView.setAdapter(null);

            // convert from JSON Array to ArrayList
            JSONArray parentsJSONArray = output.getJSONArray("data");
            for (int i = 0; i < parentsJSONArray.length(); i++) {
                try {

                    JSONObject jsonObject = parentsJSONArray.getJSONObject(i);

                    int med_hist_id = jsonObject.getInt(Constants.MEDICAL_HISTORY_ID_META);


                    int diag_id = jsonObject.optInt("diag_id", -1);
                    int doctor_id = jsonObject.optInt(Constants.DOCTOR_ID_META, -1);

                    double score = jsonObject.getDouble(Constants.MEDICAL_HISTORY_SCORE_META);
                    String added_date = jsonObject.getString(Constants.MEDICAL_HISTORY_DATE_META);
                    medicalHistoryList.add(new MedicalHistory(parent_id, med_hist_id, score, added_date, diag_id, doctor_id));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // ********************************************************************************************

            // Create ArrayAdapter which adapt array list to list view.
            listAdapter = new ListAdapter(this, medicalHistoryList);
//            listAdapter.
            medicalHistoriesListView.setAdapter(listAdapter);

            // add action when the admin clicks on parent record in the listview
            medicalHistoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    MedicalHistory m = medicalHistoryList.get(position);
                    Intent intent = new Intent(MedicalHistoryList.this, SingleMedicalHistory.class);
                    intent.putExtra("med_hist", m);
                    startActivity(intent);
                }
            });

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

}
