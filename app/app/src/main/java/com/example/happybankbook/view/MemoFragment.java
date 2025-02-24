package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_MEMO_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_MEMO_TEXT_STYLE;

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
import com.example.happybankbook.presenterReturnInterface.IntResultCallback;

public class MemoFragment extends Fragment implements View.OnClickListener{

    private TextView dateTextView;
    private ImageView contentImageView;
    private EditText contentEditText;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private MemoPresenter presenter;
    private Context mContext;
    private Activity mActivity;
    private float fontSize=12;
    private boolean isClearContentTxt=false;

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
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_MEMO_TEXT_SIZE, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.text_Size));
                contentEditText.setTextSize(fontSize);
            }
        });
        isClearContentTxt=true;
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
        ((MainActivity)mActivity).setCurrentDate(dateTextView);
        getGallery();

        SharedPreferences preferences= mActivity.getSharedPreferences(PREF_NAME_MEMO_TEXT_STYLE,Context.MODE_PRIVATE);
        fontSize=preferences.getFloat(PREF_KEY_TEXT_SIZE,12);
        contentEditText.setTextSize(fontSize);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isClearContentTxt){ contentEditText.setText("");}
    }

    @Override
    public void onStop() {
        super.onStop();
        resetTextSetting();
        isClearContentTxt=false;
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
                contentImageView.setVisibility(View.VISIBLE);
                Uri imageUri=result.getData().getData();
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                String extension = mime.getExtensionFromMimeType(mContext.getContentResolver().getType(imageUri));
                contentImageView.setTag(extension);
                Glide.with(mContext).load(imageUri).into(contentImageView);
            }else if(result.getData()!=null){
                Toast.makeText(getContext(),getResources().getString(R.string.cantLoadImg),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init(View view){
        dateTextView=view.findViewById(R.id.txtMemoDate);
        TextView addPictureTextView=view.findViewById(R.id.addPicture);
        TextView saveTextView=view.findViewById(R.id.save);
        contentImageView=view.findViewById(R.id.imageView);
        contentEditText=view.findViewById(R.id.editMemo);

        presenter=new MemoPresenter();

        dateTextView.setOnClickListener(this);
        addPictureTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.txtMemoDate){
           ((MainActivity)mActivity).setDate(dateTextView,getContext());
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

        TextView okTextView=dialog.findViewById(R.id.ok);
        TextView cancelTextView=dialog.findViewById(R.id.cancel);
        EditText happyValueEditText=dialog.findViewById(R.id.editHappy);

        MemoData data=new MemoData();

        int dateInt=((MainActivity)mActivity).convertDateToInt(dateTextView);
        data.setDate(dateInt);

        presenter.setIntResultCallback(new IntResultCallback() {
            @Override
            public void onIntResult(int value) {
                data.setNum(value+1);
            }
        });
        presenter.getDataRange(RoomDB.getInstance(getContext()).memoDao(), dateInt);

        //현재 날짜 받기
        String currentDateStr=((MainActivity)mActivity).setCurrentDate();
        String [] dateStr=currentDateStr.split("\\.");
        int currentDateInt=Integer.parseInt(dateStr[0]+dateStr[1]+dateStr[2]);

        //설정한 날짜가 현재 날짜와 다르면, 중간에 메모가 삽입이 되는 것처럼 보이게 하기 위해 table num 값 update
        if(dateInt!=currentDateInt){
            presenter.changeNum(RoomDB.getInstance(getContext()).memoDao(), dateInt);
        }

        String content=contentEditText.getText().toString();
        data.setContent(content);

        boolean hasCorrectType=true;
        if(contentImageView.getVisibility()==View.VISIBLE){
            //image mime type 확인
            if(!contentImageView.getTag().equals("png")&&!contentImageView.getTag().equals("jpeg")&&!contentImageView.getTag().equals("jpg")){
                Toast.makeText(mContext,getText(R.string.imageType),Toast.LENGTH_SHORT).show();
                hasCorrectType=false;
            }else{
                BitmapDrawable drawable = (BitmapDrawable)contentImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                data.setBitmap(bitmap);
            }
        }

        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getContext(),getResources().getText(R.string.memoContentEmpty),Toast.LENGTH_SHORT).show();
                }

                String priceStr=happyValueEditText.getText().toString().trim();
                if(TextUtils.isEmpty(priceStr)){
                    Toast.makeText(getContext(),getResources().getText(R.string.memoPriceEmpty),Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(priceStr)){
                    try{
                        int priceInt=Integer.parseInt(priceStr);
                        data.setPrice(priceInt);

                        presenter.insertMemo(RoomDB.getInstance(getContext()).memoDao(),data);
                        dialog.dismiss();
                        ((MainActivity)mActivity).navigation(R.id.mainMenu);
                    }catch(NumberFormatException e){
                        Toast.makeText(getContext(),getResources().getText(R.string.memoPriceOver),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        cancelTextView.setOnClickListener((v)-> dialog.dismiss());

        if(hasCorrectType){
            dialog.show();
        }

    }

    private void resetTextSetting(){
        SharedPreferences preferences= mActivity.getSharedPreferences(PREF_NAME_MEMO_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(PREF_KEY_TEXT_SIZE, fontSize);

        editor.apply();
    }

}