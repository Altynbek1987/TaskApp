package com.example.taskapp.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.taskapp.R;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST = 1;
    private int RESULT_OK;
    private ImageView imageView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.image_profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ololo","Click");
                onGalleryClick();
            }
        });
    }
    private void onGalleryClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //startActivityForResult(intent, GALLERY_REQUEST); Это прямую заходит в галерею
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
         /*Намерение ACTION_PICK вызывает
           отображение галереи всех изображений,
        хранящихся на телефоне, позволяя выбрать одно изображение*/
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("ololo","Click2");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            Glide.with(this).load(data.getData()).circleCrop().into(imageView);
            upload(data.getData());
        }
    }
    private void upload(Uri data) {
    }

    }

