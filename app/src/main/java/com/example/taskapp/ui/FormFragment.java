package com.example.taskapp.ui;

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
import android.widget.EditText;

import com.example.taskapp.R;
import com.example.taskapp.models.Task;

public class FormFragment extends Fragment {
    private EditText editTitle;
    private EditText editDesc;
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
        editTitle = view.findViewById(R.id.editTitle);
        editDesc = view.findViewById(R.id.editDesc);

        if (getArguments()!=null){
            Task task = (Task) getArguments().getSerializable("task");
            position = getArguments().getInt("position");
            assert task !=null;
            editTitle.setText(task.getTitle());
            editDesc.setText(task.getDescription());
            requestKey = "formRed";
        }

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String desc = editDesc.getText().toString().trim();
                if (title.isEmpty()){
                    editTitle.setError("Введите title");
                    editDesc.setError("Введите Desc");
                    return;
                }
                Task task = new Task(title,desc);
                Bundle bundle = new Bundle();
                bundle.putSerializable("task",task);
                bundle.putInt("position", position);
                getParentFragmentManager().setFragmentResult(requestKey,bundle);
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.popBackStack();
            }
        });
    }
}
