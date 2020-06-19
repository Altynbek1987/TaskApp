package com.example.taskapp.ui.onboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.taskapp.MainActivity;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.google.android.material.tabs.TabLayout;

public class BoardFragment<instantiateItem> extends Fragment implements PagerAdapter.OnStartClickListener {
    private PagerAdapter.OnStartClickListener onStartClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.view_Pager);
        PagerAdapter adapter = new PagerAdapter();
        viewPager.setAdapter(adapter);


        TabLayout tabLayout = view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        adapter.setOnStartClickListener(new PagerAdapter.OnStartClickListener() {
            @Override
            public void OnClick() {
                new Prefs(requireActivity()).isShown(true);
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.popBackStack();
            }
        });
        Button button_skip = view.findViewById(R.id.btn_skip);
        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Prefs(requireActivity()).isShown(true);
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.popBackStack();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    @Override
    public void OnClick() {
        startActivity(new Intent(getActivity(),MainActivity.class));
    }
}

