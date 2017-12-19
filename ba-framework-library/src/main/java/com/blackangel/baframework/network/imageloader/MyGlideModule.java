package com.blackangel.baframework.network.imageloader;

/**
 * Created by Finger-kjh on 2017-05-31.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.HttpUrlGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

/**
 * Glide Module Configuration class. declared AndroidManifest.xml
 */
public class MyGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int memoryCacheSize = maxMemory / 8;

        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, 1024 * 1024 * 50))
                .setMemoryCache(new LruResourceCache(memoryCacheSize));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {

        // 외부의 http 도 로드될 수 있도록 등록
        glide.register(GlideUrl.class, InputStream.class, new HttpUrlGlideUrlLoader.Factory());

        // okHttpClient 를 통해 이미지 로드하도록 함 (okhttpclient 에서 ssl 설정시 https 만 로드됨)
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(
//                mGlideModuleInitializer.provideClient()));
    }

    public static void displayImageUrl(ImageView imgView, String url) {
        MyLog.i("url = " + url);

        Glide.with(imgView.getContext())
                .load(url)
                .error(android.R.color.transparent)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        MyLog.e();
                        if(e != null)
                            e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        MyLog.i("");
                        return false;
                    }
                })
                .into(imgView);
    }

    public static Bitmap getImageAsBitmapSync(Context context, String url, OkHttpClient okHttpClient) {
        try {
            return Glide.with(context)
                    .using(new StreamModelLoaderWrapper<>(new OkHttpUrlLoader(okHttpClient)))
                    .load(new GlideUrl(url))
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getImageAsBitmapAsync(Context context, String url, OkHttpClient okHttpClient, final GetImageBitmapListener listener) {
        Glide.with(context)
                .using(new StreamModelLoaderWrapper<>(new OkHttpUrlLoader(okHttpClient)))
                .load(new GlideUrl(url))
                .asBitmap()
                .listener(new RequestListener<GlideUrl, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                        if(e == null) {
                            MyLog.e("load icon error model = " + model);
                            listener.onFailGetImageBitmap(new RuntimeException("UnknownError"));
                            return false;
                        }

                        MyLog.i("e.getMessage = " + e.getMessage());
                        e.printStackTrace();
                        listener.onFailGetImageBitmap(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, GlideUrl model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        MyLog.i("loaded " + model + ", bitmap size = " + resource.getWidth() + "x" + resource.getHeight());
                        listener.onSuccessGetImageBitmap(resource);
                        return true;
                    }
                })
                .preload();
    }

    static class StreamModelLoaderWrapper<T> implements StreamModelLoader<T> {
        private final ModelLoader<T, InputStream> wrapped;

        StreamModelLoaderWrapper(ModelLoader<T, InputStream> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public DataFetcher<InputStream> getResourceFetcher(T model, int width, int height) {
            return wrapped.getResourceFetcher(model, width, height);
        }
    }

    public interface GetImageBitmapListener {
        void onSuccessGetImageBitmap(Bitmap bitmap);
        void onFailGetImageBitmap(Exception e);
    }
}
