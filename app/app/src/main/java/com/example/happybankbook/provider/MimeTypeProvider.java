package com.example.happybankbook.provider;

import android.net.Uri;

public interface MimeTypeProvider {
    String getMimeType(Uri uri);
}
