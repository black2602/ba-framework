package com.blackangel.baframework.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.blackangel.baframework.logger.MyLog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by Finger-kjh on 2017-08-07.
 *
 * 국가코드는 ISO 639-2 표준을 씀 (세글자 국가 코드)
 */

public class LanguageUtil {

    public static final String LANGUAGE_VIETNAM = "VIE";
    public static final String LANGUAGE_ENGLISH = "ENG";
    public static final String LANGUAGE_KOREAN = "KOR";

    /**
     * ISO639 국가코드(세글자) 를 Locale 로 맵핑시켜 변환한다.
     *
     * @param iso639CountryCode
     * @return
     */
    public static Locale getMappingLocale(String iso639CountryCode) {

        switch (iso639CountryCode) {
            case LANGUAGE_KOREAN:
                return Locale.KOREA;
            case LANGUAGE_VIETNAM:
                return new Locale("vi", "VN");
            case LANGUAGE_ENGLISH:
                return Locale.ENGLISH;
        }

        // 디폴트 영어
        return Locale.ENGLISH;
    }

    private void getISO3Country(Locale locale) {
        System.out.println("Country=" + locale.getISO3Country());
    }

    /**
     * Get price with VietNam currency
     *
     * @param price
     * */
    public static String formatVnCurrence(String price) {

        NumberFormat format = new DecimalFormat("#,##0.00");// #,##0.00 ¤ (¤:// Currency symbol)
        format.setCurrency(Currency.getInstance(Locale.US));//Or default locale

        price = (!TextUtils.isEmpty(price)) ? price : "0";
        price = price.trim();
        price = format.format(Double.parseDouble(price));
        price = price.replaceAll(",", "\\.");

        if (price.endsWith(".00")) {
            int centsIndex = price.lastIndexOf(".00");
            if (centsIndex != -1) {
                price = price.substring(0, centsIndex);
            }
        }
        price = String.format("%s đ", price);
        return price;
    }

    public static String formatVnCurrency2(long price) {
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance("VND");

        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        System.out.println("Formatted currency: " + numberFormat.format(price));

        return numberFormat.format(price);
    }


    public static String formatCurrencySystemLocale(long price, String iso639CountryCode, String currencyCode) {
        MyLog.i("countryCode = " + iso639CountryCode + ", currencyCode = " + currencyCode);

        Locale locale = getMappingLocale(iso639CountryCode);
        Currency currency = Currency.getInstance(currencyCode);

        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        System.out.println("Formatted currency: " + numberFormat.format(price));

        String formattedPrice = numberFormat.format(price);

        if (formattedPrice.endsWith(".00")) {
            int centsIndex = formattedPrice.lastIndexOf(".00");
            if (centsIndex != -1) {
                formattedPrice = formattedPrice.substring(0, centsIndex);
            }
        }

        return formattedPrice;
    }

    /**
     * 기기의 로케일(지역 설정)을 강제로 바꾼다.
     *
     * @param language  ISO-639-2 표준 언어코드(세글자)
     */
    public static void changeLocale(Context context, String language) {
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        Locale locale = LanguageUtil.getMappingLocale(language);

        if(BuildUtil.isAboveJellyBean17()) {
            conf.setLocale(locale); // API 17+ only.
        } else {
            conf.locale = locale;
        }

        // Use conf.locale = new Locale(...) if targeting lower versions

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(conf);
        } else {
            res.updateConfiguration(conf, dm);
        }
    }
}
