package com.example.happybankbook.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class ImageMimeTypeProvider implements MimeTypeProvider{
    private final ContentResolver contentResolver;

    @Inject
    public ImageMimeTypeProvider(@ApplicationContext Context context){
        this.contentResolver= context.getContentResolver();
    }

    @Override
    public String getMimeType(Uri uri) {
        return contentResolver.getType(uri);
    }
}
