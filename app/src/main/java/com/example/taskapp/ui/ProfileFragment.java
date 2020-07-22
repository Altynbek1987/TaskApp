package com.example.taskapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StringLoader;
import com.example.taskapp.App;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST = 1;
    private int RESULT_OK;
    private ImageView imageProfile,editPenRedak;
    private TextView textName;
    private Prefs prefs;
    private com.example.taskapp.models.Task task;
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
        imageProfile = view.findViewById(R.id.image_profile);
        prefs = new Prefs(requireActivity());
        setListeners();
        textName.setText(prefs.getName());
        showAvatar();
        if (prefs.getName().isEmpty())
            getNameFromFB();
    }
    private void setListeners() {
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("imageProfile", "Click");
                if (prefs.getAvatarUrl().equals("")) {
                    requestPermission();
                }

                showAlert();

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
             //Читаем из Firebase
                String userId = FirebaseAuth.getInstance().getUid();
                FirebaseFirestore.getInstance().collection("users").document(userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String name = documentSnapshot.getString("name");
                                String avatar = documentSnapshot.getString("avatar");
                                textName.setText(name);
                                prefs.saveName(name);
                                prefs.saveAvatarUrl(avatar);
                                //showAvatar();
                            }
                        });
            }
            private void showAvatar(){
                Glide.with(this)
                        .load(prefs.getAvatarUrl())
                        .circleCrop().into(imageProfile);
            }
            private void saveName(String name) {
                String userId = FirebaseAuth.getInstance().getUid();
                Map <String, String> map = new HashMap<>();
                map.put("name", name);
                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        //.set(map)
                        .update("name",name)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(requireContext(), "Result " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
    private void saveAvatarUrl(String url) {
        prefs.saveAvatarUrl(url);
        String userId = FirebaseAuth.getInstance().getUid();
        Map <String, String> map = new HashMap<>();
        map.put("avatar",url);
        FirebaseFirestore.getInstance().collection("users").document(userId)
                //.set(map)
                .update("avatar",url)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
    private void upLoadImageFB(final Uri uri) {
        // Здесь я загружаю картинки на Firestore
        if (uri != null){
            final ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setTitle("Подождите идет загрузка");
            progressDialog.show();
            String userId = FirebaseAuth.getInstance().getUid();
            final StorageReference ref = storageReference.child("images/" + userId + ".png");
            ref.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return ref.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.e("TAG","url " + task.getResult());
                    saveAvatarUrl(task.getResult().toString());
                    Toast.makeText(requireContext(), "Загружено ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    });

//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                            progressDialog.setMessage("Загрузка " + (int)progress+ "%");
//                        }
//                    });
        }

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
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }
    private void openGallery(){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLERY_REQUEST); //Это прямую заходит в галерею
            //startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
         /*Намерение ACTION_PICK вызывает
           отображение галереи всех изображений,
        хранящихся на телефоне, позволяя выбрать одно изображение*/
        }
        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            Log.e("ProfileFragment", "GalleryPicture");
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                Glide.with(this).load(uri).circleCrop().into(imageProfile);
                upLoadImageFB(uri);
            }
        }
    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Вы хотите редактировать профиль?")
                .setPositiveButton("Удалить аватар", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseStorage.getInstance().getReference().child("images/" +
                                (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid() + ".png")
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("users")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .update("avatar", "")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        prefs.saveAvatarUrl("");
                                                        Toast.makeText(requireActivity(), "успешно", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }else {
                                    Toast.makeText(requireActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Выбрать из галереи", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                })
                .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.show();
    }

    }


