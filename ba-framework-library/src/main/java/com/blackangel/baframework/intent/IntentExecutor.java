package com.blackangel.baframework.intent;

import android.app.Activity;
import android.content.Intent;

import com.blackangel.baframework.media.image.BaseImagePickActivity;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class IntentExecutor {
    public static void executeGalleryPick(Activity activity) {
        // 사진 선택
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), IntentConst.REQUEST_PICK_GALLERY);
    }

    /**
     * 프레임워크에서 만든 이미지 픽 액티비티를 띄운다.
     * @param activity BaseImagePickActivity 를 상속받은 액티비티
     */
    public static void executeCustomGalleryPick(Activity activity, Class<? extends BaseImagePickActivity> imagePickActivity, int pickCount) {
        // 사진 선택
        Intent intent = new Intent(activity, imagePickActivity);
        intent.putExtra(IntentConst.KEY_IMAGE_PICK_COUNT, pickCount);
        activity.startActivityForResult(intent, IntentConst.REQUEST_PICK_GALLERY);
    }
}
