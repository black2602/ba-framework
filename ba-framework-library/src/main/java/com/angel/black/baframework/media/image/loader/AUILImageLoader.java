package com.angel.black.baframework.media.image.loader;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by KimJeongHun on 2017-01-30.
 */

public class AUILImageLoader extends AbstractImageLoader {
    @Override
    public void displayImage(String imageUrl, ImageView imageView) {

    }

    @Override
    public void deleteImageCache(String imageUri) {

    }

    @Override
    public void initializeImageLoader(Context context) {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
    }
}
