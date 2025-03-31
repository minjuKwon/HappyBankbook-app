package com.example.happybankbook.fake;

import com.example.happybankbook.MimeTypeProvider;
import com.example.happybankbook.module.MimeTypeModule;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = MimeTypeModule.class
)
public class FakeMimeTypeModule {
    @Provides
    public static MimeTypeProvider provideFakePngMimeTypeProvider() {
        return new FakePngMimeTypeProvider();
    }
    @Provides
    public static MimeTypeProvider provideFakeJpgMimeTypeProvider() {
        return new FakeJpgMimeTypeProvider();
    }
    @Provides
    public static MimeTypeProvider provideFakeGifMimeTypeProvider() {
        return new FakeGifMimeTypeProvider();
    }
}
