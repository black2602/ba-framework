package com.blackangel.baframework.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by Finger-kjh on 2017-10-30.
 */

public class ArrayUtil {

    public static <T> T[] convertArray(List<T> list, Class<T> tClass) {
        T[] spinnerItems = (T[]) Array.newInstance(tClass, list.size());
        T[] copiedSpinnerItems = list.toArray(spinnerItems);

        return copiedSpinnerItems;
    }
}
