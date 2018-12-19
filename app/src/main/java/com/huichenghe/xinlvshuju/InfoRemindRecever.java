package com.huichenghe.xinlvshuju;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.xinlvshuju.BleDeal.NotificationSendHelper;
import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.Utils.LocalDataSaveTool;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.mainpack.MainActivity;

/**
 * Created by lixiaoning on 2016/9/1.
 */
public class InfoRemindRecever extends BroadcastReceiver
{
    public static final String TAG = InfoRemindRecever.class.getSimpleName();
    public final String NOTIFICATION_TITLE = "notification_title";
    public final String NOTIFICATION_TEXT = "notification_text";
    public final String NOTIFICATION_PAK = "notification_pak";
    private NotificationSendHelper notificationSendHelper = null;
    private final int serviceID = 20160810;
    Notification.Builder notificatains = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
            if(intent.getAction().equals(MyConfingInfo.RECEVER_NOTIFITION_BRACOST))
            {
                abortBroadcast();
                if(BluetoothLeService.getInstance() != null && BluetoothLeService.getInstance().isConnectedDevice())
                {
                    String pak = intent.getStringExtra(NOTIFICATION_PAK);
                    String title = intent.getStringExtra(NOTIFICATION_TITLE);
                    String text = intent.getStringExtra(NOTIFICATION_TEXT);
                    String infoType = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.DEVICE_INFO_TYPE);
                    if(infoType != null && infoType.equals(MyConfingInfo.DEVICE_INFO_NEW))
                    {
//                      boolean aBoolean = Utils.loadData(getApplicationContext(), pak);
                        Cursor cursor = MyDBHelperForDayData.getInstance(context).selectInfoData(context);
                        boolean aBoolean = parseCursorAndCompare(cursor, pak);
                        if (!aBoolean)
                        {
                            String push = LocalDataSaveTool.getInstance(context).readSp(MyConfingInfo.PUSH_MESSAGE);
                            if(push != null && push.equals(MyConfingInfo.PUSH_MESSAGE_TRUE))
                            {
//                              Log.i("", "BluetoothLeService通知包名：" + pak + "title:" + title + "text:" + text);
                                if(notificationSendHelper == null)
                                {
                                    notificationSendHelper = new NotificationSendHelper(context.getApplicationContext());
                                }
                                notificationSendHelper.comparePackageNameAndSend(pak, title, text);
                            }
                        }
                    }
                    else
                    {
                        String weiChart = "com.tencent.mm";
                        String QQ = "com.tencent.mobileqq";
                        String Facebook = "com.facebook.katana";
                        String message = "com.facebook.orca";
                        String WhatsApp = "com.whatsapp";
                        String Twitter = "com.twitter.android";
                        String Skype = "com.skype.rover";
                        String sk = "com.skype.raider";
                        String weiChartTwo = "com.tencent.vt05";
                        if(pak.equals(weiChart) || pak.equals(QQ) || pak.equals(Facebook)
                                || pak.equals(WhatsApp) || pak.equals(Twitter) || pak.equals(Skype)
                                || pak.equals(message) || pak.equals(sk) || pak.equals(weiChartTwo))
                        {
                            if(notificationSendHelper == null)
                            {
                                notificationSendHelper = new NotificationSendHelper(context.getApplicationContext());
                            }
                            notificationSendHelper.comparePackageNameAndSend(pak, title, text);
                        }
                    }

                }
            }
            else if(intent.getAction().equals(MyConfingInfo.RECEVER_NOTIFITION_SHOW))
            {
                Log.i(TAG, "-------接收到通知广播");
                if(notificatains == null)
                {
                    notificatains = new Notification.Builder(context);
                }
                Intent intents = new Intent(BluetoothLeService.getInstance(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intents, 0);
                notificatains.setContentIntent(pendingIntent);
                notificatains.setSmallIcon(R.mipmap.movement_icon);
                notificatains.setAutoCancel(true);
                notificatains.setContentTitle(context.getString(R.string.app_name));
                notificatains.setShowWhen(true);
                notificatains.setWhen(System.currentTimeMillis());
                notificatains.build();
                BluetoothLeService.getInstance().startForeground(serviceID, notificatains.getNotification());
            }
            else if(intent.getAction().equals(MyConfingInfo.RECEVER_NEED_CONNECT_SAVE_DEVICE))
            {
                if(!BluetoothAdapter.getDefaultAdapter().isEnabled())return;
                if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice()) {
                    String deviceName = LocalDataSaveTool.getInstance(context.getApplicationContext()).readSp(DeviceConfig.DEVICE_NAME);
                    String deviceAddress = LocalDataSaveTool.getInstance(context.getApplicationContext()).readSp(DeviceConfig.DEVICE_ADDRESS);
                    if (deviceName != null && !deviceName.equals("") && deviceAddress != null && !deviceAddress.equals("")) {
                        BluetoothLeService.getInstance().connect(new LocalDeviceEntity(deviceName, deviceAddress, -50, new byte[0]));
                    }
                }
            }

    }



    private boolean parseCursorAndCompare(Cursor cursor, String com)
    {
        if(com != null && com.equals(""))
        {
            return false;
        }
        if(cursor.getCount() > 0)
        {
            if(cursor.moveToFirst())
            {
                do {
                    String appName = cursor.getString(cursor.getColumnIndex("appName"));
                    if(com.equals(appName))
                    {
                        return false;
                    }
                }
                while (cursor.moveToNext());
            }
        }
        return true;
    }
}
