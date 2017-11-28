package com.blackangel.baframework.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;

import com.blackangel.baframework.logger.MyLog;

import java.util.Date;

/**
 * Created by KimJeongHun on 2017-04-03.
 */

public class SMSReceiver extends BroadcastReceiver {
    private SMSReceiverCallback mSMSReceiverCallback;

    public SMSReceiver(@NonNull SMSReceiverCallback SMSReceiverCallback) {
        mSMSReceiverCallback = SMSReceiverCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLog.d("intent.getAction=" + intent.getAction());

        if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

            // SMS 메시지를 파싱합니다.
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[])bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];

            for(int i = 0; i < messages.length; i++) {
                // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
            }

            // SMS 수신 시간 확인
            Date curDate = new Date(smsMessage[0].getTimestampMillis());
            MyLog.d("문자 수신 시간", curDate.toString());

            // SMS 발신 번호 확인
            String origNumber = smsMessage[0].getOriginatingAddress();

            // SMS 메시지 확인
            String message = smsMessage[0].getMessageBody().toString();
            MyLog.d("문자 내용", "발신자 : "+origNumber+", 내용 : " + message);

            if(mSMSReceiverCallback.isAccept(origNumber)) {
                mSMSReceiverCallback.onReturnMessage(mSMSReceiverCallback.filterSMS(message));
            }
        }
    }

    public interface SMSReceiverCallback {
        boolean isAccept(String fromNo);
        String filterSMS(String message);
        void onReturnMessage(String message);
    }
}
