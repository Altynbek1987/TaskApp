package com.example.taskapp.interfeces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public interface OnItemClickListener {
    void onItemClick(int position);

    void onLongItemClick(int position);


}
