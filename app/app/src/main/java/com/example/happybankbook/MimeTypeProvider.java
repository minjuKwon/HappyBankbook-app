package com.example.happybankbook;

import android.net.Uri;

public interface MimeTypeProvider {
    String getMimeType(Uri uri);
}
