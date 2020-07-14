package com.example.taskapp.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.taskapp.App;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST = 1;
    private int RESULT_OK;
    private ImageView imageProfile,editPenRedak;
    private Button saveProfileBtn;
    private TextView textName;
    private Prefs prefs;
    private com.example.taskapp.models.Task task;

    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        textName = view.findViewById(R.id.text_Name);
        editPenRedak = view.findViewById(R.id.image_pen);
        saveProfileBtn = view.findViewById(R.id.btn_save_profile);
        imageProfile = view.findViewById(R.id.image_profile);
        prefs = new Prefs(requireActivity());
        setListeners();
        textName.setText(prefs.getName());
        if (prefs.getName().isEmpty()) getNameFromFB();
    }
    private void setListeners() {
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("imageProfile", "Click");
                requestPermission();
            }
        });
        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("saveProfileBtn", "Click");
                upLoadImage();

            }
            private void upLoadImage() {
                // Здесь я загружаю картинки на Firestore
                if (filePath != null){
                    final ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setTitle("Uploading  ");
                    progressDialog.show();
                    StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded" + (int)progress+ "%");
                                }
                            });
                }

            }
        });

        editPenRedak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("editPenRedak", "Click");
                NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                controller.navigate(R.id.action_profileFragment_to_editFragment);
            }
        });
        getParentFragmentManager().setFragmentResultListener("edit", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.e("RESULT ", "requestKey " + requestKey);
                String name = result.getString("name");
                prefs.saveName(name);
                saveName(name);
            }
        });
    }
            private void getNameFromFB(){
                String userId = FirebaseAuth.getInstance().getUid();
                FirebaseFirestore.getInstance().collection("users").document(userId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                String name = documentSnapshot.getString("name");
                                textName.setText(name);
                                prefs.saveName(name);
                            }
                        });
            }
            private void saveName(String name) {
                String userId = FirebaseAuth.getInstance().getUid();
                Map <String, String> map = new HashMap<>();
                map.put("name", name);
                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(requireContext(), "Result" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            private void requestPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},GALLERY_REQUEST);
        }
    }
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == GALLERY_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery(){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            //startActivityForResult(intent, GALLERY_REQUEST); Это прямую заходит в галерею
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
         /*Намерение ACTION_PICK вызывает
           отображение галереи всех изображений,
        хранящихся на телефоне, позволяя выбрать одно изображение*/
        }
        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            Log.e("ProfileFragment", "GalleryPicture");
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                Glide.with(this).load(filePath).circleCrop().into(imageProfile);
            }

        }

    }


