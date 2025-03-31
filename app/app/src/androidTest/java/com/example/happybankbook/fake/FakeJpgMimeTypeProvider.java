package com.example.happybankbook.fake;

import android.net.Uri;

import com.example.happybankbook.MimeTypeProvider;

public class FakeJpgMimeTypeProvider implements MimeTypeProvider {
    @Override
    public String getMimeType(Uri uri) {
        return "image/jpeg";
    }
}
