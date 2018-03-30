package project.aha;


import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import project.aha.Constants;
import project.aha.R;
import java.util.ArrayList;
import java.util.List;

import project.aha.SingleExerciseView;
import project.aha.models.Doctor;
import project.aha.models.Exercise;
import project.aha.models.MedicalHistory;
import project.aha.models.Parent;
import project.aha.parent_panel.ParentSingleView;

public class ListAdapter<T> extends BaseAdapter {

    // Declare Variables
    private Activity mContext;
    private List<T> allObjects = null;
    private ArrayList<T> arraylist;

    public ListAdapter(Activity context, List<T> list) {
        mContext = context;
        this.allObjects = list;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(list);
    }


    @Override
    public int getCount() {
        return allObjects.size();
    }

    @Override
    public T getItem(int position) {
        return allObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater lInflater = (LayoutInflater) mContext.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            convertView = lInflater.inflate(R.layout.itemlistrow, null);
        }


        T type = allObjects.get(position);

        TextView first_row = (TextView) convertView.findViewById(R.id.first_row);
        TextView second_row = (TextView) convertView.findViewById(R.id.second_row);
        TextView third_row = (TextView) convertView.findViewById(R.id.third_row);


        if(type instanceof Parent){
            process_for_parent_object(type ,first_row,second_row,third_row, convertView, position);

        }

        else if(type instanceof Exercise) {
            process_for_exercise(type, first_row, second_row, third_row, convertView, position);
        }


        else if(type instanceof Doctor) {
            process_for_doctor(type, first_row, second_row, third_row, convertView, position);
        }

        else if(type instanceof MedicalHistory){
            process_for_medical_history(type, first_row, second_row, third_row, convertView, position);
        }


        return convertView;
    }

    private void process_for_medical_history(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, int position) {
        MedicalHistory m = (MedicalHistory) type;
        if (first_row != null) {
            first_row.setText(m.getDate());
        }
        if (second_row != null) {
            second_row.setText(mContext.getString(R.string.score)+" : "+m.getScore());
        }
        if (third_row != null) {
            third_row.setText(mContext.getString(R.string.cars_result_title)+" : "+Constants.getCARSResult(mContext,m.getScore()));
        }

    }

    private void process_for_doctor(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        Doctor e = (Doctor) type;
        if (first_row != null) {
            first_row.setText(e.getUser_name());
        }
        if (second_row != null) {
            second_row.setText(mContext.getString(R.string.hint_specialized)+" : "+e.getSpecialized());
        }
        if (third_row != null) {
            third_row.setText(mContext.getString(R.string.hint_email)+" : "+e.getUser_email());
        }
    }

    private void process_for_exercise(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        Exercise e = (Exercise) type;
        if (first_row != null) {
            first_row.setText(e.getSubject());
        }
        if (second_row != null) {

            second_row.setText(mContext.getString(R.string.diagnose)+" : "+e.getDiagnose());
        }
        if (third_row != null) {

            third_row.setText(mContext.getString(R.string.date)+" : "+e.getAdded_date());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercise e = (Exercise)allObjects.get(position);
                Intent intent = new Intent(mContext, SingleExerciseView.class);
                intent.putExtra("exercise", e);
                mContext.startActivity(intent);
            }
        });
    }

    private void process_for_parent_object(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        Parent p = (Parent) type;
        if (first_row != null) {
            first_row.setText(p.getUser_name());
        }
        if (second_row != null) {

            second_row.setText(mContext.getString(R.string.file_number)+" : "+p.getUser_id());
        }
        if (third_row != null) {
            if(p.getMeta("child_name") != null){
                third_row.setText(mContext.getString(R.string.child_name)+" : "+p.getMeta("child_name"));
            }
        }

        if(Constants.get_current_user_type(mContext) == Constants.ADMIN_TYPE)
            return;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parent p = (Parent)allObjects.get(position);
                Intent intent = new Intent(mContext, ParentSingleView.class);
                intent.putExtra("id", p.getUser_id());
                mContext.startActivity(intent);
            }
        });
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();
        allObjects.clear();
        if (charText.length() == 0) {
            allObjects.addAll(arraylist);
        }
        else
        {
            for (T type : arraylist)
            {
                if(type instanceof Parent) {
                    filter_parent(type , allObjects , charText);
                }


            }
        }
        notifyDataSetChanged();
    }

    private void filter_parent(T type, List<T> allObjects, String charText) {
        Parent p = (Parent) type;
        if (String.valueOf(p.getUser_id()).contains(charText.toString()) ||
                (p.getMeta("child_name") != null && p.getMeta("child_name").toLowerCase().contains(charText)) ||
                (p.getUser_name() != null && p.getUser_name().toLowerCase().contains(charText)) ||
                (p.getUser_phone() != null && p.getUser_phone().toLowerCase().contains(charText.toString()))) {
            allObjects.add(type);
        }
    }
}