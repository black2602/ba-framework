package com.angel.black.baframework.util;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by KimJeongHun on 2017-03-07.
 */

public class CollectionUtil {
    public static Bundle convertMapToBundle(Map<String, ? extends Object> map) {
        Bundle bundle = new Bundle();

        Set<String> keySet = map.keySet();

        Iterator<String> iterator = keySet.iterator();

        while(iterator.hasNext()) {
            String key = iterator.next();
            Object value = map.get(key);

            putBundle(bundle, key, value);
        }

        return bundle;
    }

    private static void putBundle(Bundle bundle, String key, Object value) {
        if(value instanceof Integer) {
            bundle.putInt(key, (int) value);
        } else if(value instanceof Boolean) {
            bundle.putBoolean(key, (boolean) value);
        } else if(value instanceof Long) {
            bundle.putLong(key, (long) value);
        } else if(value instanceof Float) {
            bundle.putFloat(key, (float) value);
        } else if(value instanceof Double) {
            bundle.putDouble(key, (double) value);
        } else if(value instanceof String) {
            bundle.putString(key, (String) value);
        } else if(value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else if(value instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) value);
        }
    }

    public static Map<String, String> convertBundleToMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>();
        Set<String> keySet = bundle.keySet();

        Iterator<String> iterator = keySet.iterator();

        while(iterator.hasNext()) {
            String key = iterator.next();
            String value = bundle.get(key).toString();

            map.put(key, value);
        }

        return map;
    }
}
