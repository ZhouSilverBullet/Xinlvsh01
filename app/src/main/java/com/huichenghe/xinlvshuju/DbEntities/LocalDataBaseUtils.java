package com.huichenghe.xinlvshuju.DbEntities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
//import com.sh.xlshouhuan.hp_view.sub_view.TrendSubDataView;
//import com.sh.xlshouhuan.localconfig.MyGlobalConfig;
//import com.sh.xlshouhuan.localconfig.UserInfoConfig;
//import com.sh.xlshouhuan.localutils.DateUtils;
//import com.syt_framework.common_util.tlog.TLog;

import android.content.Context;

/**
 * 数据库管理类
 * @author lixiaoning
 *
 */
public class LocalDataBaseUtils {
	private static final String TAG = "LocalDataBaseUtils";
	
//	adb pull /sdcard/.xlsh/ble_data.db d:\
	
	private final int CURRENT_DB_INDEX = 1;
	
	private static LocalDataBaseUtils mLocalDataBase;
	private Context mContext;
	private DbUtils mDbUtilsHandler = null;									// JDBC工具累
	
	private LocalDataBaseUtils(Context c) {										// 构造方法私有
		mContext = c;
	}
	
	public static LocalDataBaseUtils getInstance(Context c) {		// 懒汉式单例模式
		if (mLocalDataBase == null) {
			mLocalDataBase = new LocalDataBaseUtils(c);
		}
		return mLocalDataBase;
	}
	
	/**
	 * 此方法，创建数据库，创建表
	 * @return
	 */
	private DbUtils getHandler() {


		if (mDbUtilsHandler == null) {
			try {
				DbUtils.DaoConfig config = new DbUtils.DaoConfig(mContext);
				config.setDbDir(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR);
				config.setDbName("ble_data" + CURRENT_DB_INDEX + ".db");
				config.setDbVersion(1);
				mDbUtilsHandler = DbUtils.create(config);	// 创建数据库
			}catch (Exception e)
			{
				e.printStackTrace();
			}
//			mDbUtilsHandler = DbUtils.create(mContext,
//					, );
			try
			{
				// 创建表
				mDbUtilsHandler.createTableIfNotExist(DeviceSaveData.class);
//				mDbUtilsHandler.createTableIfNotExist(RemindDataBase.class);
//				mDbUtilsHandler.createTableIfNotExist(SleepDataBase.class);
//				mDbUtilsHandler.createTableIfNotExist(SportEventData.class);
//				mDbUtilsHandler.createTableIfNotExist(TriedDataBase.class);
				
				// 加载旧数据库
//				loadOldDb();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mDbUtilsHandler;
	}


	/**
	 * 新设备插入或者更新数据库
	 * @param date
	 * @param deviceName
	 * @param devieAddress
	 */
	public void insertDeviceList(Date date, String deviceName, String devieAddress)
	{
		DeviceSaveData deviceData = null;

		try {	// 首先查询此设备是否已经存入表中
			deviceData = getHandler().findFirst(
					Selector.from(DeviceSaveData.class)
							.where("deviceName", "=", deviceName));
		} catch (DbException e) {
			e.printStackTrace();
		}
		// 若为空，则构建对象，并保存数据
		if(deviceData == null)
		{
			deviceData = new DeviceSaveData(mContext, date);
		}

		deviceData.deviceName = deviceName;
		deviceData.deviceAddress = devieAddress;
		deviceData.date = date;


		try {
			// 将构建的对象存入数据库
			getHandler().saveOrUpdate(deviceData);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询所有设备列表
	 * @return
	 */
	public List<DeviceSaveData> getDeviceListFromDB()
	{
		List<DeviceSaveData> deviceList = null;
		try {
			deviceList = getHandler().findAll(DeviceSaveData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(deviceList == null)
		{	// 若没有查询到数据，则devicelist为null，此时需要构建List对象，不然会造成空指针
			deviceList = new ArrayList<DeviceSaveData>();
		}


		return deviceList;
	}

	/**
	 * 根据指定的设备名查询整条信息
	 * @param deviceName
	 */
	public DeviceSaveData selectDevice(String deviceName)
	{
		DeviceSaveData deviceData = null;
		try {
			deviceData = getHandler().findFirst(Selector.from(DeviceSaveData.class)
            .where("deviceName", "=", deviceName));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return deviceData;
	}

	/**
	 * 删除指定数据,通过address或者设备名
	 */
	public void delete(Class<DeviceSaveData> entity, String colume, String value)
	{
		WhereBuilder where = WhereBuilder.b(colume, "=", value);
		try {
			getHandler().delete(DeviceSaveData.class, where);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}



















	
	/**
	 * 加载旧数据库

	private void loadOldDb() {
		String filePath = MyGlobalConfig.SDCARD_DATA_ROOT_DIR +File.separator;//+"ble_data.db";
		
		for (int index = 0; index < CURRENT_DB_INDEX; index++) {
			String fillIndex = "";
			if (index != 0) {
				fillIndex = fillIndex + index;
			}
			
			String dbName = "ble_data"+fillIndex+".db";
			
			
			TLog.e(TAG, "loadOldDb, filePath = "+filePath);
			TLog.e(TAG, "loadOldDb, dbName = "+dbName);
			
			File check = new File(filePath+dbName);
			if (check.exists()) {
				try {
					DbUtils handler = DbUtils.create(mContext, 
							MyGlobalConfig.SDCARD_DATA_ROOT_DIR, dbName);
					handler.createTableIfNotExist(DayActivtyData.class);
					List<DayActivtyData> datas1 = handler.findAll(
							Selector.from(DayActivtyData.class));
					for (int i = 0; i < datas1.size(); i++) {
						datas1.get(i).id = 0;
						getHandler().saveOrUpdate(datas1.get(i));
					}
					
					handler.createTableIfNotExist(RemindDataBase.class);
					List<RemindDataBase> datas2 = handler.findAll(
							Selector.from(RemindDataBase.class));
					for (int i = 0; i < datas2.size(); i++) {
						datas2.get(i).id = 0;
						getHandler().saveOrUpdate(datas2.get(i));
					}
					
					handler.createTableIfNotExist(SleepDataBase.class);
					List<SleepDataBase> datas3 = handler.findAll(
							Selector.from(SleepDataBase.class));
					for (int i = 0; i < datas3.size(); i++) {
						datas3.get(i).id = 0;
						getHandler().saveOrUpdate(datas3.get(i));
					}
					
					handler.createTableIfNotExist(SportEventData.class);
					List<SportEventData> datas4 = handler.findAll(
							Selector.from(SportEventData.class));
					for (int i = 0; i < datas4.size(); i++) {
						datas4.get(i).id = 0;
						getHandler().saveOrUpdate(datas4.get(i));
					}
					
					handler.createTableIfNotExist(TriedDataBase.class);
					List<TriedDataBase> datas5 = handler.findAll(
							Selector.from(TriedDataBase.class));
					
					TLog.e(TAG, "loadOldDb, datas5 = "+datas5);
					if (datas5 != null) {
						TLog.e(TAG, "loadOldDb, datas5.size() = "+datas5.size());
					}
					for (int i = 0; i < datas5.size(); i++) {
						TLog.e(TAG, "loadOldDb, datas5.get(i) = "+datas5.get(i));
						datas5.get(i).id = 0;
						getHandler().saveOrUpdate(datas5.get(i));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				check.delete();
			}
		}
	}
	 */


	/**
	 *
	 * @param date
	 * @param stepCount
	 * @param mileage
	 * @param heatCost
	 * @param activityTimeMin

	// 保存或者更新白天的数据
	public void updateBaitianData(String date, int stepCount, int mileage,
			int heatCost, int activityTimeMin) {
		
	    Date theDate = DateUtils.parseDayString(date);
		TLog.e(TAG, "updateBaitianData() theDate = "+theDate.toString()); 
		DayActivtyData day = null;
		try {// 先查询
			day = getHandler().findFirst(
					Selector.from(DayActivtyData.class)
					.where("date","=",theDate)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		// 没有数据，创建数据对象
		if (day == null) {
			day = new DayActivtyData(mContext, theDate);
		}
		// 赋值
		day.stepCount = stepCount;
		day.mileageCM = mileage;
		day.heatCost = heatCost;
		day.activityTimeMin = activityTimeMin;
		
		try {
			Log.e(TAG, "updateBaitianData() day =" + day.toString());
			// 保存或覆盖数据
			getHandler().saveOrUpdate(day);
			
			TrendSubDataView.updateBaitianWeekDateToServer(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DayActivtyData getBaitianData(Date theday) {
		if (theday == null) {
			theday = DateUtils.getTodayNYR(null);
		}
		DayActivtyData day = null;
		try {
			day = getHandler().findFirst(
					Selector.from(DayActivtyData.class)
					.where("date","=",theday)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return day;
	}
	
	public List<DayActivtyData> getBaitianData(Date begin, Date end) {
		List<DayActivtyData> datas = null;
		try {
			datas = getHandler().findAll(
					Selector.from(DayActivtyData.class)
					.where("date", "between", new Date[]{begin, end})
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account))
					.orderBy("date"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return datas;
	}
	
//	public List<DayActivtyData> getBaitianAllData() {
//		List<DayActivtyData> datas = null;
//		try {
//			datas = getHandler().findAll(
//					Selector.from(DayActivtyData.class)
//					.where("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account))
//					.orderBy("date"));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return datas;
//	}

	// 晚上的数据 
	public void updateYewanData2Db(int sleepTimeB, int sleepTimeE,
			String sleepStatus, float sleepStatusyPrecent,
			String heartRate, float heartRatePrecent) {
		// TODO Auto-generated method stub
//		Date theDate = DateUtils.getTodayNYR(null);
		Date theDate = DateUtils.getTodayNYR(DateUtils.getDateFromSec(sleepTimeB));
		TLog.e(TAG, "Sleep, save  sleepTimeB = "+sleepTimeB);
		TLog.e(TAG, "Sleep, save  sleepTimeE = "+sleepTimeE);
		TLog.e(TAG, "Sleep, save  theDate = "+theDate.toString());
		SleepDataBase data = null;
		try {
			data = getHandler().findFirst(
					Selector.from(SleepDataBase.class)
					.where("date","=",theDate)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		TLog.e(TAG, "Sleep, data = "+data);
		if (data == null) {
			data = new SleepDataBase(mContext, theDate);
		}
		data.saveSleepInfo(theDate, sleepTimeB, sleepTimeE,
				sleepStatus, sleepStatusyPrecent,
				heartRate, heartRatePrecent);
		
		try {
			getHandler().saveOrUpdate(data);
			
			TrendSubDataView.updateYewanWeekDateToServer(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SleepDataBase getSleepData(Date theday) {
		if (theday == null) {
			theday = DateUtils.getTodayNYR(null);
		}
		
		TLog.e(TAG, "Sleep, getSleepData  theDate = "+theday.toString());
		SleepDataBase sleep = null;
		try {
			sleep = getHandler().findFirst(
					Selector.from(SleepDataBase.class)
					.where("date","=",theday)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return sleep;
	}
	
	public List<SleepDataBase> getSleepData(Date begin, Date end) {
		List<SleepDataBase> datas = null;
		try {
			datas = getHandler().findAll(
					Selector.from(SleepDataBase.class)
					.where("date", "between", new Date[]{begin, end})
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account))
					.orderBy("date"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return datas;
	}
	
//	public List<SleepDataBase> getSleepAllData() {
//		List<SleepDataBase> datas = null;
//		try {
//			datas = getHandler().findAll(
//					Selector.from(SleepDataBase.class)
//					.where("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account))
//					.orderBy("date"));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return datas;
//	}
	
	/**
	 * 写入 疲劳数据
	 * 

	public void updateTiredData(int secondLen, int tiredVal) {
		Date today = DateUtils.getTodayNYR(null);
		TriedDataBase data = null;
		try {
			data = getHandler().findFirst(
					Selector.from(TriedDataBase.class)
					.where("date","=",today)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (data == null) {
			data = new TriedDataBase(mContext, today);
			data.statistics = clock;
			data.keepSeconds = secondLen;
			data.triedVal = tiredVal;
		} else {
			int his_val = data.triedVal * data.statistics;
			his_val += tiredVal;
			data.statistics++;
			data.keepSeconds = secondLen;
			data.triedVal = his_val / data.statistics;
		}
		try {
			getHandler().saveOrUpdate(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */
	/**
	 * 读取 疲劳数据
	 * 

	public List<TriedDataBase> getTiredDataArray(Date begin, Date end) {
		List<TriedDataBase> datas = null;
		try {
			datas = getHandler().findAll(
					Selector.from(TriedDataBase.class)
					.where("date", "between", new Date[]{begin, end})
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account))
					.orderBy("date"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return datas;
	}
	 */
	/**
	 * 把数据保存到数据库sportevent字段
	 * 
	 * @param mSportType		运动类型
	 * @param beginTime		开始时间
	 * @param endTime			结束时间
	 * @param kll						卡路里
	 * @param zbs					步数
	 * 
	 * @param live_xl				动态心率
	 * @param live_xl_jd			数据传输/录入进度
	 * 
	 * @param live_ydqd			运动强度
	 * @param live_ydqd_jd		数据传输/录入进度

	public void saveASPortEvent(int sportType, int beginTime,
			int endTime, int kll_cost, int step_count, 
			String heartRate, float heartRatePrecent,
			String stepSpeed, float stepSpeedPrecent) {
		Date theDate = DateUtils.getTodayNYR(DateUtils.getDateFromSec(beginTime));	// 毫秒数转换为日期格式
		TLog.e(TAG, "SPort, save  theDate = "+theDate.toString()+"; beginTime="+beginTime);
		SportEventData sport = null;					// 此变量存储运动数据
		try {
			sport = getHandler().findFirst(			// 通过SportEventData类型获取数据
					Selector.from(SportEventData.class)
					.where("date","=",theDate)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		if (sport == null) {
			TLog.e(TAG, "SPort, save  ---new");
			sport = new SportEventData(mContext, theDate);
		}
		sport.saveSportInfo(theDate, sportType, beginTime,
				endTime, kll_cost, step_count, 
				heartRate, heartRatePrecent,
				stepSpeed, stepSpeedPrecent);
		
		try {
			getHandler().saveOrUpdate(sport);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 */
	/**
	 * 查询某一天的 所有的  运动 事件
	 * @param findDate
	 * @return

	public SportEventData getASPortEvent(Date findDate) {
		
		Date theDate = DateUtils.getTodayNYR(findDate);
		TLog.e(TAG, "SPort, get  theDate = "+theDate.toString());
		SportEventData sport = null;
		try {
			sport = getHandler().findFirst(
					Selector.from(SportEventData.class)
					.where("date","=",theDate)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return sport;
	}
	
	public void updateASPortEvent(Date updateDate, int sportTimeBegin, int sportTimeEnd, int sportType) {
		
		Date theDate = DateUtils.getTodayNYR(updateDate);
		TLog.e(TAG, "SPort, get  theDate = "+theDate.toString());
		SportEventData sport = null;
		try {
			sport = getHandler().findFirst(
					Selector.from(SportEventData.class)
					.where("date","=",theDate)
					.and("account","=",MyGlobalConfig.getUserDataAtApp(mContext).read(UserInfoConfig.account)));
			
			sport.updateItem(sportTimeBegin, sportTimeEnd, sportType);
			
			try {
				getHandler().saveOrUpdate(sport);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
	}
	 */
	/**
	 * 保存欢迎页面的提醒设置
	 * @param 
	 * @return

	public void saveUserRemind(String addr, boolean remindLost, boolean remindPhone, boolean remindSms) {
		RemindDataBase remind = getRemindSetting(addr);
		if (null == remind) {
			remind = new RemindDataBase();
		}
		
		remind.deviceAddr = addr;
		remind.remindLost = remindLost;
		remind.remindPhone = remindPhone;
		remind.remindSms = remindSms;
		
		try {
			getHandler().saveOrUpdate(remind);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public RemindDataBase getRemindSetting(String connectDevAddr) {
		RemindDataBase remind = null;
		try {
			remind = getHandler().findFirst(
					Selector.from(RemindDataBase.class)
					.where("deviceAddr","=",connectDevAddr));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (null == remind) {
			if (!connectDevAddr.equals("")) {
				remind = getRemindSetting("");
			}
		}
		
		return remind;
	}

	public void updateRemindSetting(RemindDataBase remind) {
		// TODO Auto-generated method stub
		try {
			getHandler().saveOrUpdate(remind);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 */
	
}
