package com.angel.black.baframework.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

/**
 * Created by KimJeongHun on 2017-03-22.
 */

public class ResourceUtil {

    public static Uri resourceToUri(Context context, int resId) {
        Resources resources = context.getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) +
                '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) );
    }


    public static String resourceToUriString(Context context, int resId) {
        Resources resources = context.getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) +
                '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) ).toString();
    }
}
