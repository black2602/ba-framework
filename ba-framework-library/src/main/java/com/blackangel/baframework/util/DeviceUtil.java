package com.blackangel.baframework.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.opengl.GLES20;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.blackangel.baframework.logger.MyLog;

import java.nio.IntBuffer;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by KimJeongHun on 2016-06-01.
 */
public class DeviceUtil {
    private static final String TAG = DeviceUtil.class.getSimpleName();

    public static void writeDebugLogScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        MyLog.d(TAG, dm.toString());
    }

    /**
     * 기기의 휴대폰 번호를 가져온다.
     * @param activity
     * @return
     */
    public static String getDevicePhoneNumber(Activity activity) {
        TelephonyManager telManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        return StringUtil.parsePhoneNumberFormat(StringUtil.notNullString(telManager.getLine1Number()));
    }

    public static String getDeviceModelName() {
        return Build.MODEL;
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean screenOn;

        if(BuildUtil.isAboveLollipop()) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }

        MyLog.d("isScreenOn=" + screenOn);
        return screenOn;
    }

    public static void copyClipboard(Context context, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(text, text);
            cm.setPrimaryClip(clipData);
        } else {
            // API 10 이하 - 진저브레드 이하일 때 처리
            android.text.ClipboardManager cm = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(text);
        }
    }

    /**
     * 넥서스 5x 같이 하드웨어가 아닌 디스플레이 화면안에 뒤로가기, 홈키 등의 네비게이션 바가 있을 때
     * 그 네비게이션 바의 높이를 구한다.
     * 없는 경우 0 리턴
     */
    public static int getDeviceBottomNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasSoftNavigationBar (Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }


    public static int getDeviceSupportMaxSize() {
        int[] maxTextureSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

        int maxSize = maxTextureSize[0];

        return maxSize > 0 ? maxSize : Integer.MAX_VALUE;
    }

    public static int getDeviceSupportMaxSize2() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLContext ctx = egl.eglGetCurrentContext();
        GL10 gl = (GL10) ctx.getGL();
        IntBuffer val = IntBuffer.allocate(1);
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, val);
        int size = val.get(); // 최대 크기 구함

        return size;
    }

    public static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }

        // Release
        egl.eglTerminate(display);

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
    }

    public static boolean isVibrationMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        MyLog.d("audioManager.getRingerMode() : " + audioManager.getRingerMode());

        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
    }

    /**
     * 디바이스 유심의 UUID 를 가져온다.
     * 권한 필요 - 전화 권한
     * @param mContext
     * @return
     */
    public static String getDevicesTelephonyUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        MyLog.i("deviceUuid=" + deviceId);

        return deviceId;
    }

    /**
     * 랜덤한 UUID 한개를 생성한다.
     * @return
     */
    public synchronized static String getDeviceUUID() {
        String deviceUuid = UUID.randomUUID().toString();
        MyLog.i("deviceUuid=" + deviceUuid);

        return deviceUuid;
    }
}
