package com.angel.black.baframework.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Finger-kjh on 2017-09-06.
 */

public class IntentUtil {

    public static void sendMail(Context context, String receiver, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + receiver));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {
            context.startActivity(Intent.createChooser(intent, "이메일 전송"));
        } catch (Exception e) {
            /**
             * 이메일 기능을 지원하지 않는 단말 예외처리
             */
            e.printStackTrace();
            Toast.makeText(context, "해당 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void doCall(Context context, String telno) {
        String url = "tel:" + telno;

        if (telno == null || telno.equals("")) {
            Toast.makeText(context, "전화번호가 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent call_phone = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            context.startActivity(call_phone);
        } catch (Exception e) {
            /**
             * 전화 기능을 지원하지 않는 단말 예외처리
             */
            e.printStackTrace();
            Toast.makeText(context, "해당 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendSMS(Context context, String telno) {
        try {
            Uri uri = Uri.parse("smsto:" + telno);
            Intent sms = new Intent(Intent.ACTION_SENDTO, uri);
            context.startActivity(sms);
        } catch (Exception e) {
            /**
             * 문자 기능을 지원하지 않는 단말 예외처리
             */
            e.printStackTrace();
            Toast.makeText(context, "해당 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
