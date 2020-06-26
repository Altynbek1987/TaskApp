package com.example.taskapp.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.MainActivity;
import com.example.taskapp.R;
import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TaskAdapter adapter;
    private ArrayList<Task> list = new ArrayList<>();
private NavController navController;

    private FragmentResultListener fragmentResultListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getParentFragmentManager().setFragmentResultListener("formRed",
                getViewLifecycleOwner(), fragmentResultListener);

        getParentFragmentManager().setFragmentResultListener("form",
                getViewLifecycleOwner(), fragmentResultListener);
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        if (list.isEmpty()) {
            adapter = new TaskAdapter(list);
            list.add(new Task("Алтынбек", ""));
            list.add(new Task("Руслан", ""));
            list.add(new Task("Тумар", ""));
            list.add(new Task("Данияр", ""));
            adapter.notifyDataSetChanged();
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        fragmentResultListener = new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                switch (requestKey) {
                    case "form":
                        Task task = (Task) result.getSerializable("task");
                        list.add(0, task);
                        break;
                    case "formRed":
                        int position = result.getInt("position");
                        Task taskModel = (Task) result.getSerializable("task");
                        list.remove(position);
                        list.add(position, taskModel);
                        break;
                }
            }
        };

       adapter.setOnItemClickListener(new OnItemClickListener() {
           @Override
           public void onItemClick(int position) {
               Log.e("Home","pos" + position);
               Bundle bundle = new Bundle();
               bundle.putInt("position",position);
               bundle.putSerializable("task", list.get(position));
               navController.navigate(R.id.formFragment, bundle);
           }

           @Override
           public void onLongItemClick(final int position) {
               final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               builder.setMessage("Вы хотите удалить?")
                       .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       list.remove(position);
                       adapter.notifyDataSetChanged();
                   }
               })
               .setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
               builder.show();

           }

           @Override
           public void onTheEndClick() {

           }

//           @Override
//           public void onTheEndClick() {
//               AlertDialog.Builder builder1 = new AlertDialog.Builder();
//               builder1.setMessage("Хотите закрыть приложение?").setPositiveButton("Da", new DialogInterface.OnClickListener() {
//                   @Override
//                   public void onClick(DialogInterface dialog, int which) {
//
//                   }
//               })
//                       .setNegativeButton("Net", new DialogInterface.OnClickListener() {
//                           @Override
//                           public void onClick(DialogInterface dialog, int which) {
//
//                           }
//                       })
//                       .setNeutralButton("Otmena", new DialogInterface.OnClickListener() {
//                           @Override
//                           public void onClick(DialogInterface dialog, int which) {
//
//                           }
//                       });
//
//           }
       });

    }


}