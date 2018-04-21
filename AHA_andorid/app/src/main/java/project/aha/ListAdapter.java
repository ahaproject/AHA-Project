package project.aha;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.*;

import project.aha.Chatting.SingleChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.aha.models.Chat;
import project.aha.models.Diagnose;
import project.aha.models.Doctor;
import project.aha.models.Exercise;
import project.aha.models.MedicalHistory;
import project.aha.models.Parent;
import project.aha.models.ReportedChat;
import project.aha.parent_panel.ParentSingleView;

public class ListAdapter<T> extends BaseAdapter {

    // Declare Variables
    private Activity mContext;
    private List<T> allObjects = null;
    private ArrayList<T> arraylist;
    private int clickedPosition;

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
        if (convertView == null) {
            LayoutInflater lInflater = (LayoutInflater) mContext.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);

            convertView = lInflater.inflate(R.layout.itemlistrow, null);
        }


        T type = allObjects.get(position);

        TextView first_row = convertView.findViewById(R.id.first_row);
        TextView second_row = convertView.findViewById(R.id.second_row);
        TextView third_row = convertView.findViewById(R.id.third_row);


        if (type instanceof Parent) {
            process_for_parent_object(type, first_row, second_row, third_row, convertView, position);

        } else if (type instanceof Exercise) {
            process_for_exercise(type, first_row, second_row, third_row, convertView, position);
        } else if (type instanceof Doctor) {
            process_for_doctor(type, first_row, second_row, third_row, convertView, position);
        } else if (type instanceof MedicalHistory) {
            process_for_medical_history(type, first_row, second_row, third_row, convertView, position);
        } else if (type instanceof ReportedChat) {
            process_for_reported_chats(type, first_row, second_row, third_row, convertView, position);
        }


        return convertView;
    }

    private void process_for_reported_chats(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, int position) {
        ReportedChat m = (ReportedChat) type;
        if (first_row != null) {
            first_row.setText(m.getReported_date());
        }
        if (second_row != null && m.getReporter() != null) {
            second_row.setText(mContext.getString(R.string.reporter) + " : " + m.getReporter().getUser_name());
        } else {
            second_row.setText(mContext.getString(R.string.reporter) + " : " + mContext.getString(R.string.deleted));
        }
        if (third_row != null && m.getReported() != null) {
            third_row.setText(mContext.getString(R.string.reported) + " : " + m.getReported().getUser_name());
        } else {
            third_row.setText(mContext.getString(R.string.reported) + " : " + mContext.getString(R.string.deleted));
        }

    }

    private void process_for_medical_history(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, int position) {
        MedicalHistory m = (MedicalHistory) type;
        if (first_row != null) {
            first_row.setText(m.getDate());
        }
        if (second_row != null) {
            second_row.setText(mContext.getString(R.string.score) + " : " + m.getScore());
        }
        if (third_row != null) {
            third_row.setText(mContext.getString(R.string.cars_result_title) + " : " + Constants.getCARSResult(mContext, m.getScore()));
        }

    }

    private void process_for_doctor(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        Doctor d = (Doctor) type;
        if (first_row != null) {
            first_row.setText(d.getUser_name());
        }
        if (second_row != null) {
            Diagnose diagnoseObj = Constants.diagnoses.get(d.getDiag_id());
            if (diagnoseObj != null)
                second_row.setText(mContext.getString(R.string.hint_specialized) + " : " + diagnoseObj.getName());
            else{
                second_row.setText("");
            }
        }
        if (third_row != null) {
            third_row.setText(mContext.getString(R.string.hint_email) + " : " + d.getUser_email());
        }
    }

    private void process_for_exercise(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        final Exercise e = (Exercise) type;
        if (first_row != null) {
            first_row.setText(e.getSubject());
        }
        if (second_row != null) {

            second_row.setText(mContext.getString(R.string.diagnose) + " : " + e.getDiagnose());
        }
        if (third_row != null) {

            third_row.setText(mContext.getString(R.string.date) + " : " + e.getAdded_date());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercise e = (Exercise) allObjects.get(position);
                Intent intent = new Intent(mContext, SingleExerciseView.class);
                intent.putExtra("exercise", e);
                mContext.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new AdapterView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Exercise e = (Exercise) allObjects.get(position);
                if (e.getDoctor_id() == Constants.get_current_user_id(mContext)) {
                    clickedPosition = position;
                    // create dialog
                    AlertDialog.Builder confirm_delete_dialog = new AlertDialog.Builder(mContext);

                    // set title
                    confirm_delete_dialog.setTitle(mContext.getString(R.string.delete));

                    // set message
                    confirm_delete_dialog.setMessage(mContext.getString(R.string.confirm_delete));

                    // set icon
                    confirm_delete_dialog.setIcon(android.R.drawable.ic_dialog_alert);

                    // add action when admin click on yes
                    confirm_delete_dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            HashMap<String, String> data = new HashMap<String, String>();
                            data.put(Constants.CODE, Constants.DELETE_EXERCISE + "");
                            data.put(Constants.EXERCISE_ID_META, e.getEx_id() + "");
                            new DatabasePostConnection(mContext).postRequest(data, Constants.DATABASE_URL);
                        }
                    });

                    // add action if the admin click on cancel
                    confirm_delete_dialog.setNegativeButton(android.R.string.no, null);


                    // show the dialog
                    confirm_delete_dialog.show();

                }
                return true;
            }
        });

    }

    private void process_for_parent_object(T type, TextView first_row, TextView second_row, TextView third_row, View convertView, final int position) {
        Parent p = (Parent) type;
        if (first_row != null) {
            first_row.setText(p.getUser_name());
        }
        if (second_row != null) {

            second_row.setText(mContext.getString(R.string.file_number) + " : " + p.getUser_id());
        }
        if (third_row != null) {
            if (p.getMeta("child_name") != null) {
                third_row.setText(mContext.getString(R.string.child_name) + " : " + p.getMeta("child_name"));
            }else{
                third_row.setText("");
            }
        }

        if (Constants.get_current_user_type(mContext) == Constants.ADMIN_TYPE)
            return;

    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();
        allObjects.clear();
        if (charText.length() == 0) {
            allObjects.addAll(arraylist);
        } else {
            for (T type : arraylist) {
                if (type instanceof Parent) {
                    filter_parent(type, allObjects, charText);
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

    public void updateDataSet(List<T> chats) {
        this.allObjects = chats;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(chats);
    }

    public void remove() {
        if (clickedPosition > -1)
            allObjects.remove(clickedPosition);
    }

    public void remove(int clickedPosition) {
        allObjects.remove(clickedPosition);
    }

    public void updateList(Diagnose di) {
        allObjects.clear();
        if (di.getId() == 0) {
            allObjects.addAll(arraylist);
        } else {
            for (T type : arraylist) {
                if (type instanceof Exercise) {
                    Exercise p = (Exercise) type;
                    String diagnoseName = Constants.getDiagnose(di.getId());
                    if (p.getDiagnose().equalsIgnoreCase(diagnoseName)) {
                        allObjects.add(type);
                    }
                }
            }
        }
        notifyDataSetChanged();

    }
}