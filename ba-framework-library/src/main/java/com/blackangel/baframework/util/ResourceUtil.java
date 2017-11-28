package com.blackangel.baframework.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.blackangel.baframework.logger.MyLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

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

    public static String resourceRawToTxt(Context context, int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);

        return convertStreamToString(is);
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1)
                {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static String getResRawFileUrl(String filename) {
        return "file:///android_res/raw/" + filename;
    }

    public static String getResAssetFileUrl(String filename) {
        return "file:///android_asset/" + filename;
    }

    @NonNull
    public static String getStringByLocal(Activity context, int resId, String locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return getStringByLocalPlus17(context, resId, locale);
        else
            return getStringByLocalBefore17(context, resId, locale);
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static String getStringByLocalPlus17(Activity context, int resId, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(resId);
    }

    private static String getStringByLocalBefore17(Context context,int resId, String language) {
        Resources currentResources = context.getResources();
        AssetManager assets = currentResources.getAssets();
        DisplayMetrics metrics = currentResources.getDisplayMetrics();
        Configuration config = new Configuration(currentResources.getConfiguration());
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        /*
         * Note: This (temporarily) changes the devices locale! TODO find a
         * better way to get the string in the specific locale
         */
        Resources defaultLocaleResources = new Resources(assets, metrics, config);
        String string = defaultLocaleResources.getString(resId);
        // Restore device-specific locale
        new Resources(assets, metrics, currentResources.getConfiguration());
        return string;
    }

    public static String loadStringFromAssets(Context context, String file) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream stream = assetManager.open(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }

            return total.toString();

        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e("Load failed assets " + file);
        }

        return "";
    }
}
