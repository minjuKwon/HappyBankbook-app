package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
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
import com.example.happybankbook.presenterReturnInterface.GetReturnInt;

public class MemoFragment extends Fragment implements View.OnClickListener{

    private TextView txtDate;
    private ImageView img;
    private EditText editContent;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private MemoPresenter presenter;
    private float fontSize=12;
    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if(context instanceof Activity){
            mActivity=(Activity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //변경 font size 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.fontSize3), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.fontSize));
                editContent.setTextSize(fontSize);
            }
        });
    }

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
        ((MainActivity)mActivity).setNowDate(txtDate);
        getGallery();

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.memoTextSetting),Context.MODE_PRIVATE);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),12);
        editContent.setTextSize(fontSize);
    }

    @Override
    public void onStart() {
        super.onStart();
        editContent.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        resetTextSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    public void getGallery(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if(result.getResultCode()==RESULT_OK&&result.getData()!=null){
                img.setVisibility(View.VISIBLE);
                Uri imageUri=result.getData().getData();
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                String extension = mime.getExtensionFromMimeType(mContext.getContentResolver().getType(imageUri));
                img.setTag(extension);
                Glide.with(mContext).load(imageUri).into(img);
            }else if(result.getData()!=null){
                Toast.makeText(getContext(),getResources().getString(R.string.cantLoadImg),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init(View view){
        txtDate=view.findViewById(R.id.txtMemoDate);
        TextView txtAddPicture=view.findViewById(R.id.addPicture);
        TextView txtSave=view.findViewById(R.id.save);
        img=view.findViewById(R.id.imageView);
        editContent=view.findViewById(R.id.editMemo);

        presenter=new MemoPresenter();

        txtDate.setOnClickListener(this);
        txtAddPicture.setOnClickListener(this);
        txtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.txtMemoDate){
           ((MainActivity)mActivity).setDate(txtDate,getContext());
       }else if(v.getId()==R.id.addPicture){
           loadImage();
       }else if(v.getId()==R.id.save){
           save();
       }
    }

    public void loadImage(){
        final String imgType="image/*";
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT).
                setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imgType);
        Intent createChooserIntent=Intent.createChooser(intent,null);
        activityResultLauncher.launch(createChooserIntent);
    }

    public void save(){
        //Dialog 설정
        Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_happy);

        TextView txtOk=dialog.findViewById(R.id.ok);
        TextView txtCancel=dialog.findViewById(R.id.cancel);
        EditText editHappy=dialog.findViewById(R.id.editHappy);

        MemoData data=new MemoData();

        int intDate=((MainActivity)mActivity).dateIntToString(txtDate);
        data.setDate(intDate);

        presenter.setReturnInt(new GetReturnInt() {
            @Override
            public void getInt(int value) {
                data.setNum(value+1);
            }
        });
        presenter.getDataRange(RoomDB.getInstance(getContext()).memoDao(), intDate);

        //현재 날짜 받기
        String strNowDate=((MainActivity)mActivity).setNowDate();
        String [] strDate=strNowDate.split("\\.");
        int intNowDate=Integer.parseInt(strDate[0]+strDate[1]+strDate[2]);

        //설정한 날짜가 현재 날짜와 다르면, 중간에 메모가 삽입이 되는 것처럼 보이게 하기 위해 table num 값 update
        if(intDate!=intNowDate){
            presenter.changeNum(RoomDB.getInstance(getContext()).memoDao(), intDate);
        }

        String content=editContent.getText().toString();
        data.setContent(content);

        boolean type=true;
        if(img.getVisibility()==View.VISIBLE){
            //image mime type 확인
            if(!img.getTag().equals("png")&&!img.getTag().equals("jpeg")&&!img.getTag().equals("jpg")){
                Toast.makeText(mContext,getText(R.string.imageType),Toast.LENGTH_SHORT).show();
                type=false;
            }else{
                BitmapDrawable drawable = (BitmapDrawable)img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                data.setBitmap(bitmap);
            }
        }

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getContext(),getResources().getText(R.string.memoContentEmpty),Toast.LENGTH_SHORT).show();
                }

                String strPrice=editHappy.getText().toString().trim();
                if(TextUtils.isEmpty(strPrice)){
                    Toast.makeText(getContext(),getResources().getText(R.string.memoPriceEmpty),Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(strPrice)){
                    try{
                        int price=Integer.parseInt(strPrice);
                        data.setPrice(price);

                        presenter.insertMemo(RoomDB.getInstance(getContext()).memoDao(),data);
                        dialog.dismiss();
                        ((MainActivity)mActivity).navigation(R.id.mainMenu);
                    }catch(NumberFormatException e){
                        Toast.makeText(getContext(),getResources().getText(R.string.memoPriceOver),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        txtCancel.setOnClickListener((v)-> dialog.dismiss());

        if(type){
            dialog.show();
        }

    }

    private void resetTextSetting(){
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.memoTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}