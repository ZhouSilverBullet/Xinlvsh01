package com.huichenghe.xinlvshuju.handlerHelper;

import android.os.Handler;

/**
 * Created by lixiaoning on 16-phone-15.
 */
public class MainHander extends Handler
{
//    private static final int MSG_SYNC_TIME = 0;            	// 同步时间
//    private static final int MSG_GET_DEVICE_VERSION = clock;	// 版本号
//    private static final int MSG_GET_DEVICE_BATTERU = phone;	// 电量
//    private static final int MSG_GET_DAY_DATA = info;        	// 全天数据
//    private static final int MST_GET_ALL_HEARTREAT_DATA = sitting;    // 全天心率
//    private static final int MSG_GET_EACH_HOUR_DATA = heartWarning;        // 分时运动数据
//    private static final int MSG_GET_SLEEP_DATA = lost;            // 睡眠数据
//    private static final int MSG_LIVE_HR_DATA = sos;
//
//    public static final String TAG = MainHander.class.getSimpleName();
//
//
//    WeakReference<MainActivity> mAcitvity;
//
//		MainHander(MainActivity mainActivity)
//		{
//			mAcitvity = new WeakReference<MainActivity>(mainActivity);
//		}
//
//		@Override
//		public void handleMessage(Message msg)
//		{
//			super.handleMessage(msg);
//			MainActivity ac = mAcitvity.get();
//			if(ac != null)
//			{
//				switch (msg.what) {
//					case MSG_SYNC_TIME:                // 同步时间
//						new BleDataforSyn().syncCurrentTime();
//						break;
//
//					case MSG_GET_DEVICE_BATTERU:    // 获取电量
//						Log.i(TAG, "获取电量battey");
//						new BleDataForBattery();    // 发送获取电量
//						this.postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								int battery = BleDataForBattery.getmCurrentBattery();    // 获取电量
//								updateBattery(battery);        // 更新电量
//								Log.i(TAG, "battery：" + battery);
//							}
//						}, 1000);
//						break;
//
//					case MSG_GET_DEVICE_VERSION:    // 获取固件版本号
//						LocalDataSaveTool.getInstance(ac).writeSp(MyConfingInfo.HARD_VERSION, "");
//						final BleDataForHardVersion hardVersion = new BleDataForHardVersion();
//						hardVersion.getHardVersion();
//
//						this.postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								String hardV = LocalDataSaveTool.getInstance(ac).readSp(MyConfingInfo.HARD_VERSION);
//								if (hardV == null || hardV.equals("")) {
//									hardVersion.getHardVersion();
//								}
//								if (hardV != null && !hardV.equals("")) {
//									connectNetToGetUpdate(hardV);
//								}
//							}
//						}, 1000);
//						break;
//					case MSG_GET_DAY_DATA:            // 获取当天数据
//						new GetEachHourTask().execute(info);
//
//						this.sendEmptyMessageDelayed(MSG_GET_DAY_DATA, 10000);
//
//						break;
//
//					case MST_GET_ALL_HEARTREAT_DATA:
//						// 发送数据获取全天心率
//						new GetEachHourTask().execute(phone);
//
//						break;
//
//					case MSG_GET_EACH_HOUR_DATA:
//						// 获取分时数据
//						new GetEachHourTask().execute(clock);
//						break;
//
//					case MSG_GET_SLEEP_DATA:
//						new GetSleepDataTask().execute();
//						break;
//
//					case MSG_LIVE_HR_DATA:
////					new BleLiveHRData().openLiveWatch(MainActivity.this);
//
//						break;
//				}
//			}

}
