package com.example.taskapp.ui.firestore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DatabaseId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

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

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onLongItemClick(int position) {
                showAlert(list.get(position));
            }

        });

    }
    private void initList() {
        adapter = new TaskAdapter(list);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private void showAlert(final Task tasks) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Вы хотите удалить?" + tasks.getTitle()+tasks.getDescription())
                .setNegativeButton("Нет", null)
                .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    FirebaseFirestore
                            .getInstance()
                            .collection("tasks")
                            .document(tasks.getDocId())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    if (task.isSuccessful()){
                                        list.remove(tasks);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(requireActivity(), "успешно", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                    }
                });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void loadData() {
        FirebaseFirestore.getInstance().collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                list.clear();
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    String docId = snapshot.getId();
                                    Task mTask = snapshot.toObject(Task.class);
                                    mTask.setDocId(docId);
                                    list.add(mTask);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    //    @Override
//    public void onResume() {
//        super.onResume();
//         //loadData2();
//    }
    private void loadData2() {
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
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (listenerRegistration != null)listenerRegistration.remove();
//    }
}
