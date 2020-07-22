package com.example.taskapp.ui.note;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.FileLoader;
import com.example.taskapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class NoteFragment extends Fragment {
    private EditText editWrite;
    private TextView textRead;
    private Button writeBtn;
    private Button readBtn;
    private ScrollView scrollView;
    private final static String FILE_NAME = "TaskApp/note.txt";

    public NoteFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editWrite = view.findViewById(R.id.text_Folder_write);
        textRead = view.findViewById(R.id.text_Folder_read);
        writeBtn = view.findViewById(R.id.btn_Write);
        readBtn = view.findViewById(R.id.btn_Read);
        //scrollView = view.findViewById(R.id.SCROLLER_ID);
        initFile();
        writeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
        public void onClick (View v){
                write();
            }
    });
        readBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
           readFolder();

        }
    });
}
    private void initFile() {
        File folder = new File(Environment.getExternalStorageDirectory(),"TaskApp");
        folder.mkdirs();
        File file = new File(folder,"note.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(){
        FileOutputStream fos = null;
        try {
            String text = editWrite.getText().toString();
            fos = new FileOutputStream(getExternalPath());
            fos.write(text.getBytes());
            Toast.makeText(requireContext(), "Файл сохранен", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex) {
            Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File getExternalPath() {
        return(new File(Environment.getExternalStorageDirectory(),FILE_NAME));
    }
    public void readFolder(){
        FileInputStream fin = null;
        File file = getExternalPath();
        if (!file.exists())return;
        try {
            fin = new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            textRead.setText(text);
        }
        catch(IOException ex) {
            Toast.makeText(requireActivity(), "Данный файл или каталог отсутствует", Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){
                Toast.makeText(requireActivity(), "Данный файл или каталог отсутствует", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
