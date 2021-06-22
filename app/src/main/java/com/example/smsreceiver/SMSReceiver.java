package com.example.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() 호출됨.");

        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);

        if(messages.length > 0){
            String sender = messages[0].getOriginatingAddress(); // 발신번호
            Log.d(TAG, "Sender: " +  sender);
            String contents = messages[0].getMessageBody().toString(); // 내용
            Log.d(TAG, "contents: " + contents);
            Date receiveDate = new Date(messages[0].getTimestampMillis()); // 발신시
            Log.d(TAG, "received date: " + receiveDate);

            sendToActivity(context, sender, contents, receiveDate);
        }
    }

    private void sendToActivity (Context context, String sender, String contents, Date receiveDate){
        Intent intent = new Intent(context, SmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP );

        intent.putExtra("sender", sender);
        intent.putExtra("contents", contents);
        intent.putExtra("receiveDate", format.format(receiveDate));
        context.startActivity(intent);

    }

    private SmsMessage[] parseSmsMessage(Bundle bundle){
        // SMTP 프로토콜 안에 해당 이름으로 들어가있습니다.
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i<objs.length; i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            }else{
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }
        return messages;
    }
}