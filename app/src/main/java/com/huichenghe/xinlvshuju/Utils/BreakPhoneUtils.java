package com.huichenghe.xinlvshuju.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;


public class BreakPhoneUtils {

    private final static String TELEPHONY_SERVICE = "phone";

    public static void doBreakPhone() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null,
                    new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
//			Log.e("", "break the phone ---- telephony.isRinging() = " + telephony.isRinging());

//			if (telephony.isRinging()) {
            telephony.endCall();
            Log.e("", "break the phone ---- ok ");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理来电或短信号码逻辑，先判断有无“+86”，若有
     * 则去掉，然后通过电话号码查询，若第一次未查询到
     * 前边加上“+86”进行再次查询，这样的原因是可能用户
     * 存储的时候是以“+86”开头的形式存储的
     *
     * @param context
     * @param phoneNum
     * @return
     */
    public static String getContactNameFromPhoneBook(Context context, String phoneNum) {
        if (phoneNum == null) {
            return "unkown";
        }
        String name = null;
        Log.i("", "短信号码是：" + phoneNum);
        if (phoneNum.substring(0, 3).equals("+86")) {// +86开头的，将+86去掉
            phoneNum = phoneNum.substring(3);
        }
        name = l_getContactNameFromPhoneBook(context, phoneNum);// 获取联系人
        Log.i("", "联系人名字：" + name);
        Log.i("", "联系人电话：" + phoneNum);
        if (name == null) {                                        // 获取不到，将+86加上，重新获取
            StringBuffer buffer = new StringBuffer();
            buffer.append("+86");
            buffer.append(phoneNum);
            name = l_getContactNameFromPhoneBook(context, buffer.toString());
            Log.i("", "联系人名字：inside:" + name);
            Log.i("", "联系人电话：inside:   " + buffer.toString());
        }

//		if(name == null){
//			StringBuffer sb = new StringBuffer();
//			sb.append("+86");
//			sb.append("");
//			sb.append(phoneNum.substring(0, info));
//			sb.append(" ");
//			sb.append(phoneNum.substring(info, sos));
//			sb.append(" ");
//			sb.append(phoneNum.substring(sos, 11));
//			Log.i("",  "联系人电话：" + sb.toString());
//			l_getContactNameFromPhoneBook(context, sb.toString());
//		}

        return name;
    }

    private static String l_getContactNameFromPhoneBook(Context context, String phoneNum) {
        String contactName = null;
        ContentResolver cr = context.getContentResolver();
//		Cursor pCur = cr.query(
//				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//				null,
//				ContactsContract.CommonDataKinds.Phone.NUMBER + " = " + phoneNum + "", 
//				null,
//				null);
//		cr.query(uri, projection, selection, selectionArgs, sortOrder)
        Cursor pCur = cr.query(
                Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, phoneNum),            // 参数一：uri
                new String[]{PhoneLookup._ID, PhoneLookup.NUMBER,    // 参数二：projection，查询的内容
                        PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE,
                        PhoneLookup.LABEL},
                null,                                                                                        // 参数三：selection
                null,                                                                                        // 参数四：selectionArgs
                null);                                                                                        // 参数五：sortOrder

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("", "此次是否走了这里。。。" + pCur.getCount() + Thread.currentThread().getName());
        if (pCur.moveToFirst()) {
            int nameField = pCur.getColumnIndex(PhoneLookup.DISPLAY_NAME);        // 获取列
            Log.i("", "列数据： ：：：：" + nameField);
            contactName = pCur
                    .getString(nameField);
            if (pCur != null) {
                pCur.close();
            }
        }

        Log.i("TAG", "----------------来电号码：--------------" + phoneNum + "来电名字：" + contactName);

        return contactName;
    }
}