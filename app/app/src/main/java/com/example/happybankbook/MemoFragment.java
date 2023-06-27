package com.example.happybankbook;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MemoFragment extends Fragment implements View.OnClickListener{

    private TextView txtAddPicture;
    private TextView txtSave;
    private ImageView img;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGallery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo, container, false);
        init(view);
        return view;
    }

    public void init(View view){
        txtAddPicture=view.findViewById(R.id.addPicture);
        txtSave=view.findViewById(R.id.save);
        img=view.findViewById(R.id.imageView);

        txtAddPicture.setOnClickListener(this);
        txtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.addPicture){
            loadImage();
       }else if(v.getId()==R.id.save){
            save();
       }
    }

    public void getGallery(){

        img.setVisibility(View.VISIBLE);

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode()==RESULT_OK&&result!=null&&result.getData()!=null){
                Uri imageUri=result.getData().getData();
                Glide.with(getContext()).load(imageUri).into(img);
            }
        });

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