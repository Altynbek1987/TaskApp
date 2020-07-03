package com.example.taskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.example.taskapp.interfeces.OnItemClickListener;
import com.example.taskapp.models.Task;
import com.example.taskapp.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int MENU_ALPHA_ID = 1;
    final int MENU_ROTATE_ID = 4;
    final int MENU_COMBO_ID = 5;
    //TextView header;
    ImageView imageViewHeader;

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private FloatingActionButton fab;
    private Toolbar toolbar;

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
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout header = (LinearLayout) headerView.findViewById(R.id.header);
        initNavController();
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.profileFragment);

            }
        });
//        if (!new Prefs(this).isShown())
//            navController.navigate(R.id.boardFragment);
//        else
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) navController.navigate(R.id.phoneFragment);
        boolean isShown = new Prefs(this).isShown();
        if (!isShown) {
            navController.navigate(R.id.boardFragment);
            return;
        } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            navController.navigate(R.id.phoneFragment);
            return;
        }
    }

    private void initNavController() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
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

    public void onTheEndClick() {

    }

}

//@Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
//        switch (v.getId()){
//            case R.id.headerImageView:
//                menu.add(MENU_ALPHA_ID);
//                menu.add(MENU_COMBO_ID);
//                menu.add(MENU_ROTATE_ID);
//                break;
//        }
//        super.onCreateContextMenu(menu,v,menuInfo);
//    }
//    @Override
//    public boolean onContextItemSelected(MenuItem item){
//        Animation anim = null;
//        switch (item.getItemId()){
//            case MENU_ALPHA_ID:
//                anim = AnimationUtils.loadAnimation(this,R.anim.myalpha);
//                break;
//            case MENU_COMBO_ID:
//                anim = AnimationUtils.loadAnimation(this,R.anim.mycombo);
//                break;
//            case MENU_ROTATE_ID:
//                anim = AnimationUtils.loadAnimation(this,R.anim.myrotate);
//                break;
//        }
//        header.startAnimation(anim);
//        return super.onContextItemSelected(item);
//    }

//registerForContextMenu(header);