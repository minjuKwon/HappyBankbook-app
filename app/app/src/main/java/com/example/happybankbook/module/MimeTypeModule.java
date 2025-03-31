package com.example.happybankbook.module;

import android.content.Context;

import com.example.happybankbook.provider.ImageMimeTypeProvider;
import com.example.happybankbook.provider.MimeTypeProvider;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class MimeTypeModule {
    @Provides
    public static MimeTypeProvider provideMimeTypeProvider(@ApplicationContext Context context){
        return new ImageMimeTypeProvider(context);
    }
}
