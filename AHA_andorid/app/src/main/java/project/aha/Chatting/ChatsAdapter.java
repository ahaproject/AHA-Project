package project.aha.Chatting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import project.aha.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import project.aha.models.Chat;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>  {

    private LinkedHashMap<String, Chat> mData;
    private LayoutInflater mInflater;
    private Activity activity;

    public ChatsAdapter(Activity activity,Context context, LinkedHashMap<String, Chat> data) {
        this.activity = activity;
        this.mInflater = LayoutInflater.from(context);
        if (data != null) this.mData = data;
        else this.mData = new LinkedHashMap<>();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemlistrow, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatsAdapter.ChatViewHolder holder, int position) {
        Chat ch = new ArrayList<>(mData.values()).get(position);

        if (holder.first_row != null) {
            holder.first_row.setText(ch.getOther_user().getUser_name());
        }
        if (holder.second_row != null) {
            holder.second_row.setText(ch.getLast_chat_content());
        }
        if (holder.third_row != null) {
            holder.third_row.setText(ch.getLast_chat_time());
        }

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(Chat item) {
        mData.put(item.getChatKey(), item);
    }

    public void update(String key, String content, String time) {
        Chat ch = mData.get(key);
        ch.setLast_chat_content(content);
        ch.setLast_chat_time(time);
    }



    // stores and recycles views as they are scrolled off screen
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView first_row, second_row, third_row;

        public ChatViewHolder(View itemView) {
            super(itemView);
            first_row = itemView.findViewById(R.id.first_row);
            second_row = itemView.findViewById(R.id.second_row);
            third_row = itemView.findViewById(R.id.third_row);
        }

    }

    // convenience method for getting data at click position
    Chat getItem(int pos) {
        return new ArrayList<>(mData.values()).get(pos);
    }

    Chat getByKey(String key) {
        return mData.get(key);
    }
}




class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}