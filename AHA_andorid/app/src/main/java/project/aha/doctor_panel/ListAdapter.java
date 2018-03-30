package project.aha.doctor_panel;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import project.aha.R;
import java.util.ArrayList;
import java.util.List;

import project.aha.SingleExerciseView;
import project.aha.models.Exercise;
import project.aha.models.Parent;
import project.aha.parent_panel.ParentSingleView;

public class ListAdapter<T> extends BaseAdapter {

    // Declare Variables
    private Context mContext;
    private List<T> allObjects = null;
    private ArrayList<T> arraylist;

    public ListAdapter(Context context, List<T> list) {
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

        TextView tt = (TextView) convertView.findViewById(R.id.full_row);
        TextView tt1 = (TextView) convertView.findViewById(R.id.left_row);
        TextView tt3 = (TextView) convertView.findViewById(R.id.right_row);

        if(type instanceof Parent){
            Parent p = (Parent) type;
            if (tt != null) {
                tt.setText(p.getUser_name());
            }
            if (tt1 != null) {

                tt1.setText(p.getChildName());
            }
            if (tt3 != null) {

                tt3.setText(mContext.getString(R.string.file_number)+" : "+p.getUser_id());
            }

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

        else if(type instanceof Exercise){
            Exercise e = (Exercise) type;
            if (tt != null) {
                tt.setText(e.getSubject());
            }
            if (tt1 != null) {

                tt1.setText(e.getDiagnose());
            }
            if (tt3 != null) {

                tt3.setText(e.getAdded_date());
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


        return convertView;
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