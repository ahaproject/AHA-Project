package project.aha.doctor_panel;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.*;
import project.aha.R;
import java.util.ArrayList;
import java.util.List;

import project.aha.models.Parent;
import project.aha.parent_panel.ParentSingleView;

public class ListAdapter extends BaseAdapter {

    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<Parent> allParents = null;
    private ArrayList<Parent> arraylist;

    public ListAdapter(Context context, List<Parent> worldpopulationlist) {
        mContext = context;
        this.allParents = worldpopulationlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Parent>();
        this.arraylist.addAll(worldpopulationlist);
    }


    @Override
    public int getCount() {
        return allParents.size();
    }

    @Override
    public Parent getItem(int position) {
        return allParents.get(position);
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


        Parent p = allParents.get(position);

        TextView tt = (TextView) convertView.findViewById(R.id.name);
        TextView tt1 = (TextView) convertView.findViewById(R.id.child_name);
        TextView tt3 = (TextView) convertView.findViewById(R.id.parent_file_number);

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
                Parent p = allParents.get(position);
                Intent intent = new Intent(mContext, ParentSingleView.class);
                intent.putExtra("id", p.getUser_id());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();
        allParents.clear();
        if (charText.length() == 0) {
            allParents.addAll(arraylist);
        }
        else
        {
            for (Parent parent : arraylist)
            {
                if (String.valueOf(parent.getUser_id()).contains(charText.toString()) ||
                        ( parent.getChildName() != null && parent.getChildName().toLowerCase().contains(charText) )||
                        ( parent.getUser_name() != null && parent.getUser_name().toLowerCase().contains(charText)) ||
                        ( parent.getUser_phone()!= null && parent.getUser_phone().toLowerCase().contains(charText.toString()) ))  {
                    allParents.add(parent);
                }
            }
        }
        notifyDataSetChanged();
    }
}