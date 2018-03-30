package project.aha;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import project.aha.models.Diagnose;

public class SpinnerAdapter extends ArrayAdapter<Diagnose> {

    private Activity context ;
    private List<Diagnose>  objects;
    public SpinnerAdapter(Activity context, int resource, List<Diagnose> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // Inflating the layout for the custom Spinner
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.spinner_style, parent, false);

// Declaring and Typecasting the textview in the inflated layout
        TextView diagnose = (TextView) layout.findViewById(R.id.item);

        diagnose.setText(objects.get(position).getName());

        return layout;

    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}


