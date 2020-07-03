package com.example.taskapp.ui;

import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.models.Task;

import petrov.kristiyan.colorpicker.ColorPicker;

public class FormFragment extends Fragment {
    private Button btnColor;
    private EditText editTitle;
    private EditText editDesc;
    private Task task;
    private int position;
    private String requestKey = "form";
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
        btnColor = view.findViewById(R.id.btn_color);
        editTitle = view.findViewById(R.id.editTitle);
        editDesc = view.findViewById(R.id.editDesc);
        if (getArguments()!=null) {
            task = (Task) getArguments().getSerializable("task");
            if (task!=null){
                editTitle.setText(task.getTitle());
                editDesc.setText(task.getDescription());
            }
        }
        if (getArguments()!=null){
            Task task = (Task) getArguments().getSerializable("task");
            position = getArguments().getInt("position");
            assert task !=null;
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
                    public void onChooseColor(int position,int color) {
                        colorForTask[0] = color;
                        editTitle.setBackgroundColor(color);
                        editDesc.setBackgroundColor(color);
                    }
                    @Override
                    public void onCancel(){
                        // put code
                    }
                });

                //Здес надо сделать выбор цвета для "таска"
            }
        });


        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String desc = editDesc.getText().toString().trim();
                if (title.isEmpty()){
                    //Проверка на пустоту
                    editTitle.setError("Введите title");
                    editDesc.setError("Введите Desc");
                    return;
                }
                if (task == null){
                    task = new Task(title,desc);
                    App.getInstance().getDatabase().taskDao().insert(task);  //Запись в базу данных.
                }else {
                    task.setTitle(title);
                    task.setDescription(desc);
                    App.getInstance().getDatabase().taskDao().update(task);
                }
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("task",task);
//                bundle.putInt("position", position);
//                getParentFragmentManager().setFragmentResult(requestKey,bundle);
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.popBackStack();
            }
        });
    }
}
