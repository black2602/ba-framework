package com.blackangel.baframework.network.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.blackangel.baframework.logger.MyLog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Glide Module Configuration class. declared AndroidManifest.xml
 */
public class MyGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int memoryCacheSize = maxMemory / 8;

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(getDefaultPlaceHolderRes())
                .error(getDefaultErrorRes())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, 1024 * 1024 * 50))
                .setMemoryCache(new LruResourceCache(memoryCacheSize))
                .setLogLevel(Log.ASSERT)
                .setDefaultRequestOptions(requestOptions);
    }

    private int getDefaultErrorRes() {
        return 0;
    }

    public int getDefaultPlaceHolderRes() {
        return 0;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }

    public static void displayImageUrl(ImageView imgView, String url) {
//        MyLog.i("url = " + url);

        Glide.with(imgView.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })

                .into(imgView);
    }


    public static void getImageAsBitmapAsyncFromLocal(Context context, String url, GetImageBitmapListener listener) {
        Glide.with(context)
                .asBitmap()
                .load(new File(url))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
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
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        MyLog.i("loaded " + model + ", bitmap size = " + resource.getWidth() + "x" + resource.getHeight());
                        listener.onSuccessGetImageBitmap(resource);
                        return true;
                    }
                })
                .preload();
    }

    public static void displayGif(int gitResId, ImageView imageView) {
        Glide.with(imageView.getContext())
                .asGif()
                .load(gitResId)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(imageView);
    }

    public interface GetImageBitmapListener {
        void onSuccessGetImageBitmap(Bitmap bitmap);
        void onFailGetImageBitmap(Exception e);
    }
}
