package project.aha.doctor_panel;


import android.app.Activity;
import android.content.Context;
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

        TextView full_row = (TextView) convertView.findViewById(R.id.full_row);
        TextView left_row = (TextView) convertView.findViewById(R.id.left_row);
        TextView right_row = (TextView) convertView.findViewById(R.id.right_row);


        if(type instanceof Parent){
            process_for_parent_object(type ,full_row,left_row,right_row, convertView, position);

        }

        else if(type instanceof Exercise) {
            process_for_exercise(type, full_row, left_row, right_row, convertView, position);
        }


        else if(type instanceof Doctor) {
            process_for_doctor(type, full_row, left_row, right_row, convertView, position);
        }


        return convertView;
    }

    private void process_for_doctor(T type, TextView full_row, TextView left_row, TextView right_row, View convertView, final int position) {
        Doctor e = (Doctor) type;
        if (full_row != null) {
            full_row.setText(e.getUser_name());
        }
        if (left_row != null) {

            left_row.setText(e.getSpecialized());
        }
        if (right_row != null) {

            right_row.setText(e.getUser_email());
        }
    }

    private void process_for_exercise(T type, TextView full_row, TextView left_row, TextView right_row, View convertView, final int position) {
        Exercise e = (Exercise) type;
        if (full_row != null) {
            full_row.setText(e.getSubject());
        }
        if (left_row != null) {

            left_row.setText(e.getDiagnose());
        }
        if (right_row != null) {

            right_row.setText(e.getAdded_date());
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

    private void process_for_parent_object(T type, TextView full_row, TextView left_row, TextView right_row, View convertView, final int position) {
        Parent p = (Parent) type;
        if (full_row != null) {
            full_row.setText(p.getUser_name());
        }
        if (left_row != null) {

            left_row.setText(p.getChildName());
        }
        if (right_row != null) {

            right_row.setText(mContext.getString(R.string.file_number)+" : "+p.getUser_id());
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
                    Parent p = (Parent) type;
                    if (String.valueOf(p.getUser_id()).contains(charText.toString()) ||
                            (p.getChildName() != null && p.getChildName().toLowerCase().contains(charText)) ||
                            (p.getUser_name() != null && p.getUser_name().toLowerCase().contains(charText)) ||
                            (p.getUser_phone() != null && p.getUser_phone().toLowerCase().contains(charText.toString()))) {
                        allObjects.add(type);
                    }
                }


            }
        }
        notifyDataSetChanged();
    }
}