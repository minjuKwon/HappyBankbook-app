package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.MemoPresenter;

public class MemoFragment extends Fragment implements View.OnClickListener{

    private TextView txtDate;
    private TextView txtAddPicture;
    private TextView txtSave;
    private ImageView img;
    private EditText editContent;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private MemoPresenter presenter;

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
        ((MainActivity)getActivity()).setNowDate(txtDate);
        getGallery();

    }

    @Override
    public void onStart() {
        super.onStart();
        editContent.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
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

    private void init(View view){
        txtDate=view.findViewById(R.id.txtDate);
        txtAddPicture=view.findViewById(R.id.addPicture);
        txtSave=view.findViewById(R.id.save);
        img=view.findViewById(R.id.imageView);
        editContent=view.findViewById(R.id.editMemo);

        presenter=new MemoPresenter();

        txtDate.setOnClickListener(this);
        txtAddPicture.setOnClickListener(this);
        txtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.txtDate){
           ((MainActivity)getActivity()).setDate(txtDate,getContext());
       }else if(v.getId()==R.id.addPicture){
           loadImage();
       }else if(v.getId()==R.id.save){
           save();
       }
    }

    public void loadImage(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT).
                setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        Intent createChooserIntent=Intent.createChooser(intent,null);
        activityResultLauncher.launch(createChooserIntent);
    }

    public void save(){

        Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_happy);

        TextView txtOk=dialog.findViewById(R.id.ok);
        TextView txtCancel=dialog.findViewById(R.id.cancel);

        int intDate=((MainActivity)getActivity()).dateIntToString(txtDate);

        String content=editContent.getText().toString();

        EditText editHappy=dialog.findViewById(R.id.editHappy);

        MemoData data=new MemoData();
        data.setDate(intDate);
        data.setContent(content);

        if(img.getVisibility()==View.VISIBLE){
            BitmapDrawable drawable = (BitmapDrawable)img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            data.setBitmap(bitmap);
        }

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strPrice=editHappy.getText().toString().trim();

                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getContext(),"행복을 메모해 주세요:)",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(strPrice)){
                    Toast.makeText(getContext(),"얼마나 행복하셨나요?:D",Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(strPrice)){
                    Integer price=Integer.parseInt(strPrice);
                    data.setPrice(price);

                    presenter.insertMemo(RoomDB.getInstance(getContext()).memoDao(),data);
                    dialog.dismiss();
                    ((MainActivity)getActivity()).navigation(R.id.mainMenu);
                }

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}