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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Update;

import com.example.taskapp.App;
import com.example.taskapp.MainActivity;
import com.example.taskapp.R;
import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;
import com.example.taskapp.ui.firestore.FirestoreFragment;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TaskAdapter adapter;
    private ArrayList<Task> list = new ArrayList<>();
    private NavController navController;
    private FirestoreFragment firestoreFragment;
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

        adapter = new TaskAdapter(list);
        App.getInstance().getDatabase().taskDao().getAllLive().
                observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.setList(tasks);
                    }
                });

        if (list.isEmpty()) {
//            list.addAll(App.getInstance().getDatabase().taskDao().getAll());  //Выташил все данные из Таска в хом фрагмент
            adapter.notifyDataSetChanged();
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
//        fragmentResultListener = new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                switch (requestKey) {
//                    case "form":
//                        Task task = (Task) result.getSerializable("task");
//                        list.add(0, task);
//                        break;
//                    case "formRed":
//                        int position = result.getInt("position");
//                        Task taskModel = (Task) result.getSerializable("task");
//                        list.remove(position);
//                        list.add(position, taskModel);
//                        break;
//                }
//            }
//        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("Home", "pos" + position);
                Task task = list.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("task", list.get(position));
                navController.navigate(R.id.action_nav_home_to_formFragment, bundle);
            }
            @Override
            public void onLongItemClick(final int position) {

                showAlert(list.get(position));
            }

            private void showAlert(final Task task) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы хотите удалить?" + task.getTitle() + task.getDescription())
                        .setNegativeButton("Нет",null)
                        .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getInstance().getDatabase().taskDao().delete(task);

                            }
                        });
                builder.show();
            }

        });
    }

    public void sort() {
        list.clear();
        list.addAll(App.getInstance().getDatabase().taskDao().sortByASC());
        adapter.notifyDataSetChanged();
    }

    public void sorttwo() {
        list.clear();
        list.addAll(App.getInstance().getDatabase().taskDao().sortByDESC());
        adapter.notifyDataSetChanged();
    }

}
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_exit:
//                SharedPreferences preferences = getSharedPreferences("storageFile", Context.MODE_PRIVATE);
//                preferences.edit().putBoolean("isShown", false).apply();
//                finish();
//            case R.id.action_sort:
//                sort();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


