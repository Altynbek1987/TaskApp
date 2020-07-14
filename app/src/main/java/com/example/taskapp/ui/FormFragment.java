package com.example.taskapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.models.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import petrov.kristiyan.colorpicker.ColorPicker;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FormFragment extends Fragment {
    private Button btnColor;
    private EditText editTitle;
    private EditText editDesc;
    private Task task;
    private int position;
    private String requestKey = "form";
    private CheckBox checkBox;
    //private FirebaseFirestore db;

    public FormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("FormFragment","Redactor");
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        btnColor = view.findViewById(R.id.btn_color);
        editTitle = view.findViewById(R.id.editTitle);
        editDesc = view.findViewById(R.id.editDesc);
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            if (task != null) {
                editTitle.setText(task.getTitle());
                editDesc.setText(task.getDescription());
            }
        }
        if (getArguments() != null) {
            Task task = (Task) getArguments().getSerializable("task");
            position = getArguments().getInt("position");
            assert task != null;
            editTitle.setText(task.getTitle());
            editDesc.setText(task.getDescription());
            requestKey = "formRed";
        }

        final int[] colorForTask = new int[1];
        view.findViewById(R.id.btn_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker colorPicker = new ColorPicker(requireActivity());
                colorPicker.show();
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        //Здес надо сделать выбор цвета для "таска"
                        colorForTask[0] = color;
                        editTitle.setBackgroundColor(color);
                        editDesc.setBackgroundColor(color);
                    }
                    @Override
                    public void onCancel() {
                        // put code
                    }
                });
            }
        });
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FormFragment","FabClick");
                String title = editTitle.getText().toString().trim();
                String desc = editDesc.getText().toString().trim();
                if (title.isEmpty()) {
                    //Проверка на пустоту
                    editTitle.setError("Введите title");
                    return;
                }
                if (desc.isEmpty()) {
                    editDesc.setError("Введите Desc");
                    return;
                }
                if (task == null && checkBox.isChecked()) {
                    task = new Task(title, desc);
                    App.getInstance().getDatabase().taskDao().insert(task); //Запись в базу данных.
                    saveToFirestore(task);
                } else if (task == null && checkBox != null){
                    task = new Task(title, desc);
                    App.getInstance().getDatabase().taskDao().insert(task);
                } else {
                    assert task != null;
                    task.setTitle(title);
                    task.setDescription(desc);
                    App.getInstance().getDatabase().taskDao().update(task);
                    //saveToFirestore(task);
                    //saveUpdate(task);
                    close();
                }
            }

//                Bundle bundle = new Bundle();
//                bundle.putSerializable("task",task);
//                bundle.putInt("position", position);
//                getParentFragmentManager().setFragmentResult("requestKey",bundle);

        });
    }
            private void close(){
                //здес закрываю форм фрагмент и захожу в хом фрагмент
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.popBackStack();
            }
            private void saveToFirestore(Task task) {
        //Соединение с Firebase
//                Map<String, Object> map = new HashMap<>();  //Если у нас значение не одного типа то нужно поставить Object
//                map.put("title", task.getTitle());
//                map.put("desc", task.getDescription());
                FirebaseFirestore.getInstance().collection("tasks").add(task).addOnCompleteListener
                        (new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    close();
                                }else {
                                    Toast.makeText(requireContext(),"Ошибка записи", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

//            private void saveUpdate(Task task){
//                final Map<String, Object> data = new HashMap<>();
//                data.put("title", task.getTitle());
//                data.put("desc", task.getDescription());
//                db.collection("tasks").document("title")
//                        .set(data)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                db.collection("tasks").document("title")
//                                        .set(data, SetOptions.merge());
//                                Log.d(TAG, "DocumentSnapshot successfully written!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error writing document", e);
//                            }
//                        });
//            }
    }
