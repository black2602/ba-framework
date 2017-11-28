package com.blackangel.baframework.media.image.loader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by KimJeongHun on 2017-01-30.
 */

public abstract class AbstractImageLoader {

    public abstract void displayImage(String imageUrl, ImageView imageView);

    public abstract void deleteImageCache(String imageUri);

    public abstract void initializeImageLoader(Context context);
}
