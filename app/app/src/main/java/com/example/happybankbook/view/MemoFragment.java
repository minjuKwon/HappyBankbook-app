package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import static com.example.happybankbook.Utils.convertDateToInt;
import static com.example.happybankbook.Utils.setCurrentDate;
import static com.example.happybankbook.Utils.setDate;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_MEMO_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_SIZE_SMALL;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_MEMO_TEXT_STYLE;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_SMALL;

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

public class MemoFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private Activity mActivity;
    private MemoPresenter presenter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private TextView dateTextView, okTextView, cancelTextView;
    private EditText contentEditText, happyValueEditText;
    private ImageView contentImageView;

    private float textSize= TEXT_SIZE_DEFAULT_SMALL;
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
        //변경 text size 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_MEMO_TEXT_SIZE,
                        this,
                        (requestKey, result) -> {
                            textSize=result.getFloat(BUNDLE_KEY_TEXT_SIZE);
                            contentEditText.setTextSize(textSize);
                        }
                );
        isClearContentTxt=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        dateTextView=view.findViewById(R.id.txtMemoDate);
        TextView addPictureTextView=view.findViewById(R.id.addPicture);
        TextView saveTextView=view.findViewById(R.id.save);
        contentEditText=view.findViewById(R.id.editMemo);
        contentImageView=view.findViewById(R.id.imageView);

        dateTextView.setOnClickListener(this);
        addPictureTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);

        presenter=new MemoPresenter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrentDate(dateTextView);
        getGallery();

        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_MEMO_TEXT_STYLE,Context.MODE_PRIVATE);
        textSize=preferences.getFloat(PREF_KEY_TEXT_SIZE, PREF_DEFAULT_TEXT_SIZE_SMALL);
        contentEditText.setTextSize(textSize);
    }

    public void getGallery(){

        activityResultLauncher=
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if(result.getResultCode()==RESULT_OK&&result.getData()!=null){
                                contentImageView.setVisibility(View.VISIBLE);
                                Uri imageUri=result.getData().getData();
                                getImage(imageUri);
                            }else if(result.getData()!=null){
                                Toast.makeText(
                                        mContext,
                                        getResources().getString(R.string.cantLoadImg),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

    }

    private void getImage(Uri imageUri){
        final String imageType=
                mContext.getContentResolver().getType(imageUri);
        final MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(imageType);
        contentImageView.setTag(extension);
        Glide.with(mContext).load(imageUri).into(contentImageView);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isClearContentTxt){ contentEditText.setText("");}
    }

    @Override
    public void onStop() {
        super.onStop();
        resetTextStyle();
        isClearContentTxt=false;
    }

    private void resetTextStyle(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_MEMO_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(PREF_KEY_TEXT_SIZE, textSize);

        editor.apply();
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

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.txtMemoDate){
           setDate(dateTextView, mContext);
       }else if(v.getId()==R.id.addPicture){
           loadImage();
       }else if(v.getId()==R.id.save){
           save();
       }
    }

    public void loadImage(){
        final String imgType="image/*";
        final Uri contentUri= android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT).setDataAndType(contentUri, imgType);
        Intent createChooserIntent=Intent.createChooser(intent,null);
        activityResultLauncher.launch(createChooserIntent);
    }

    public void save(){
        //Dialog 설정
        Dialog dialog=setDialog();

        MemoData data=new MemoData();

        getDate(data);

        String content=contentEditText.getText().toString();
        data.setContent(content);

        boolean hasCorrectType=setImage(data);

        okTextView.setOnClickListener(v -> {
            String priceStr=happyValueEditText.getText().toString().trim();
            validateInput(content, priceStr, data, dialog);
        });

        cancelTextView.setOnClickListener((v)-> dialog.dismiss());

        if(hasCorrectType){
            dialog.show();
        }

    }

    private Dialog setDialog(){
        Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_happy);

        okTextView=dialog.findViewById(R.id.ok);
        cancelTextView=dialog.findViewById(R.id.cancel);
        happyValueEditText=dialog.findViewById(R.id.editHappy);
        return dialog;
    }

    private void getDate(MemoData data){
        int dateInt= convertDateToInt(dateTextView);
        data.setDate(dateInt);

        presenter.setIntResultCallback(value -> data.setNum(value+1));
        presenter.getDataRange(RoomDB.getInstance(mContext).memoDao(), dateInt);

        //현재 날짜 받기
        String currentDateStr= setCurrentDate();
        String [] dateStr=currentDateStr.split("\\.");
        int currentDateInt=Integer.parseInt(dateStr[0]+dateStr[1]+dateStr[2]);

        //설정한 날짜가 현재 날짜와 다르면,
        // 중간에 메모가 삽입이 되는 것처럼 보이게 하기 위해 table num 값 update
        if(dateInt!=currentDateInt){
            presenter.changeNum(RoomDB.getInstance(mContext).memoDao(), dateInt);
        }
    }

    private boolean setImage(MemoData data){
        if(contentImageView.getVisibility()==View.VISIBLE){
            //image mime type 확인
            if(!contentImageView.getTag().equals("png")&&
                    !contentImageView.getTag().equals("jpeg")&&
                    !contentImageView.getTag().equals("jpg")
            ){
                Toast.makeText(
                        mContext,
                        getText(R.string.imageType),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }else{
                BitmapDrawable drawable = (BitmapDrawable)contentImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                data.setImage(bitmap);
            }
        }
        return true;
    }

    private void validateInput(String content, String priceStr, MemoData data, Dialog dialog){
        if(TextUtils.isEmpty(content)){
            Toast.makeText(mContext,getResources().getText(R.string.memoContentEmpty),Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(priceStr)){
            Toast.makeText(mContext,getResources().getText(R.string.memoPriceEmpty),Toast.LENGTH_SHORT).show();
        }

        if(!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(priceStr)){
            try{
                int priceInt=Integer.parseInt(priceStr);
                data.setPrice(priceInt);

                presenter.insertMemo(RoomDB.getInstance(mContext).memoDao(),data);
                dialog.dismiss();
                ((MainActivity)mActivity).navigation(R.id.mainMenu);
            }catch(NumberFormatException e){
                Toast.makeText(
                        mContext,
                        getResources().getText(R.string.memoPriceOver),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

}