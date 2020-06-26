package com.example.taskapp.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;
import com.example.taskapp.ui.FormFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.ViewHolder> {

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;


    private ArrayList<Task> list;
    private boolean aBoolean = false;

    public TaskAdapter(ArrayList<Task> list) {
        this.list = list;
    }


    public void add(Task task) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.textTitle.setText("rec");   //?
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
        if (position % 2 == 0){
            holder.linear_layout.setBackgroundColor(Color.LTGRAY);
        }else {
            holder.linear_layout.setBackgroundColor(Color.WHITE);
        }
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        private TextView textDesc;
        private View linear_layout;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            linear_layout = itemView.findViewById(R.id.linear_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongItemClick(getAdapterPosition());
                    return false;
                }
            });

            textTitle = itemView.findViewById(R.id.textTitle);
            textDesc = itemView.findViewById(R.id.textDescription);
        }

        public void bind(Task task) {
            textTitle.setText(task.getTitle());
            textDesc.setText(task.getDescription());

        }
    }
}
