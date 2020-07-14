package com.example.taskapp.ui.firestore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;
import com.example.taskapp.ui.home.TaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DatabaseId;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FirestoreFragment extends Fragment {
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Task> list = new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private NavController navController;
    private OnItemClickListener onItemClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_firestore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       recyclerView = view.findViewById(R.id.recyclerView);
        initList();
        loadData();

//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.formFragment);
//            }
//        });

    }

    private void initList() {
        adapter = new TaskAdapter(list);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }
    private void showAlert(final int tasks) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Вы хотите удалить?" + tasks)
                .setNegativeButton("Нет",null)
                .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference db_node = FirebaseDatabase.getInstance().getReference()
                                .getRoot().child("locations").child(String.valueOf(getId()));
                        db_node.setValue(null);
                        //FirebaseFirestore.getInstance().collection("tasks").add(isRemoving());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void loadData() {
        FirebaseFirestore.getInstance().collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null) {
                                list.clear();
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    String docId = snapshot.getId();
                                    Task mTask = snapshot.toObject(Task.class);
                                    mTask.setDocId(docId);
                                    list.add(mTask);
                                }
                                //list.addAll(task.getResult().toObjects(Task.class));
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                });
    }
    private void deleteData(int id){
        FirebaseFirestore.getInstance().collection("tasks").document("id").delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
         //loadData2();
    }

    private void loadData2(){
        listenerRegistration = FirebaseFirestore.getInstance().collection("tasks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (snapshots != null) {
                            list.clear();
                            list.addAll(snapshots.toObjects(Task.class));
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
    }
    @Override
    public void onPause() {
        super.onPause();
        if (listenerRegistration != null)listenerRegistration.remove();
    }
}
