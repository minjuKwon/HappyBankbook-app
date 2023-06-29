package com.example.happybankbook;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MemoFragment extends Fragment implements View.OnClickListener{

    private TextView txtDate;
    private TextView txtAddPicture;
    private TextView txtSave;
    private ImageView img;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd");
        Date date=new Date();
        txtDate.setText(dateFormat.format(date));

        getGallery();
    }

    public void getGallery(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode()==RESULT_OK&&result!=null&&result.getData()!=null){
                img.setVisibility(View.VISIBLE);
                Uri imageUri=result.getData().getData();
                Glide.with(getContext()).load(imageUri).into(img);
            }else if(result.getData()!=null){
                Toast.makeText(getContext(),"이미지를 불러올 수 없습니다",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void init(View view){
        txtDate=view.findViewById(R.id.txtDate);
        txtAddPicture=view.findViewById(R.id.addPicture);
        txtSave=view.findViewById(R.id.save);
        img=view.findViewById(R.id.imageView);

        txtDate.setOnClickListener(this);
        txtAddPicture.setOnClickListener(this);
        txtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.txtDate){
           setDate();
       }else if(v.getId()==R.id.addPicture){
           loadImage();
       }else if(v.getId()==R.id.save){
           save();
       }
    }

    public void setDate(){

        DatePickerDialog.OnDateSetListener calendarListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtDate.setText(String.format("%d.%d.%d",year,month+1,dayOfMonth));
            }
        };

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(),R.style.DialogTheme,calendarListener,year,month,day).show();

    }

    public void loadImage(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT).
                setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        Intent createChooserIntent=Intent.createChooser(intent,null);
        activityResultLauncher.launch(createChooserIntent);
    }

    public void save(){

    }

}