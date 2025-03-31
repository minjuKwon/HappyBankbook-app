package com.example.happybankbook.fake;

import android.net.Uri;

import com.example.happybankbook.provider.MimeTypeProvider;


public class FakePngMimeTypeProvider implements MimeTypeProvider {
    @Override
    public String getMimeType(Uri uri) {
        return "image/png";
    }
}
