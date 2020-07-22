package com.example.taskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;
import com.example.taskapp.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //TextView header;
    ImageView imageViewHeader;

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView headerNameMy, headerEmailMy ;
    View hView;
    private ImageView headerImageView;
    private Prefs prefs;
    private ImageView imageProfile;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.formFragment);
            }
        });
        initNavController();
//        if (!new Prefs(this).isShown())
//            navController.navigate(R.id.boardFragment);
//        else
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) navController.navigate(R.id.phoneFragment);
        boolean isShown = new Prefs(this).isShown();
        if (!isShown)
            navController.navigate(R.id.boardFragment);
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            navController.navigate(R.id.phoneFragment);


    }

    private void initNavController() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        headerNameMy = (TextView)hView.findViewById(R.id.headerNameMy);
        headerEmailMy = (TextView)hView.findViewById(R.id.headerEmailMy);
        // Здесь показал имя на Хедере. Взял из prefs
        prefs = new Prefs(this);
        headerNameMy.setText(prefs.getName());

        //TextView name = headerNameMy.findViewById(R.id.headerNameMy);
        Log.e("ololo", "initNavController: " + prefs.getAvatarUrl());
        ImageView icon = hView.findViewById(R.id.headerImageView);
        Glide.with(this)
                .load(prefs.getAvatarUrl())
                .circleCrop().into(icon);


        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.close();
                navController.navigate(R.id.profileFragment);
            }
        });
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_firestore,R.id.nav_note)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_home) {
                    fab.show();
                } else {
                    fab.hide();
                }
                if (destination.getId() == R.id.boardFragment) {
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (destination.getId() == R.id.phoneFragment){
                    toolbar.setVisibility(View.GONE);
                }else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private boolean flag;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Этот интерфейс отвечает за три точки (toolbar)
        switch (item.getItemId()) {
            case R.id.settings_clear:
                new Prefs(MainActivity.this).clear();
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.sorting_alphabetically:
                //   navController.navigate(R.id.nav_home);
                if (flag) {
                    //Метод сортировки по алфавиту
                    Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    ((HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).sort();
                    flag = false;
                } else {
                    Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    ((HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).sorttwo();
                    flag = true;
                }
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                navController.navigate(R.id.phoneFragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
