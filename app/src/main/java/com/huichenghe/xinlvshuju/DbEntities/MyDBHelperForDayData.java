package com.huichenghe.xinlvshuju.DbEntities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by lixiaoning on 15-11-18.
 */
public class MyDBHelperForDayData extends SQLiteDBOnSdcardHelper {

    static MyDBHelperForDayData instance = null;
    public static String DATABASE_NAME = "xlsh_simple_db.db";

    public static MyDBHelperForDayData getInstance(Context mContext) {
        if (instance == null) {
            synchronized (MyDBHelperForDayData.class) {
                if (instance == null) {
                    instance = new MyDBHelperForDayData(mContext);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("ALTER TABLE blood_date_table ADD deviceType TEXT");
//        db.execSQL("ALTER TABLE blood_date_table ADD dataSendOK TEXT");
//        db.execSQL("ALTER TABLE blood_date_table ADD other01 TEXT");
//        db.execSQL("ALTER TABLE blood_date_table ADD other02 TEXT");
        // 全天总数据表
        String sql = " CREATE TABLE if not exists day_data_one (_id INTEGER PRIMARY KEY AUTOINCREMENT, "        // 建表四个字段，
                + "userAccount TEXT , day TEXT , stepAll Integer , calorie Integer ,"
                + "mileage Integer , movementTime TEXT, moveCalorie INTEGER, sitTime TEXT, sitCalorie INTEGER, deviceType TEXT, "
                + "dataSendOK TEXT, other01 TEXT, other02 TEXT); ";
        db.execSQL(sql);

        // 心率数据表
        String sqlOne = "CREATE TABLE IF NOT EXISTS heart_reat_table (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "userAccount TEXT, day TEXT, one TEXT, two TEXT, three TEXT, four TEXT, five TEXT, six TEXT,"
                + "seven TEXT, eight TEXT, nine TEXT, ten TEXT, one1 TEXT, two1 TEXT, three1 TEXT, four1 TEXT,"
                + "five1 TEXT, six1 TEXT, seven1 TEXT, eight1 TEXT, nine1 TEXT, ten1 TEXT, one2 TEXT, two2 TEXT,"
                + "three2 TEXT, four2 TEXT, deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlOne);


        // 离线数据表
        String sqlOutline = "CREATE TABLE IF NOT EXISTS outline_movement_table (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "userAccount TEXT, day TEXT, type TEXT, startTime TEXT, endTime TEXT, calorie INTEGER, stepTotle INTEGER,"
                + "heartRate TEXT, deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlOutline);


        // 每小时计步数据表
        String sqlEachHourData = "CREATE TABLE IF NOT EXISTS each_hour_data_table (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "userAccount TEXT, day TEXT, one INTEGER, two INTEGER, three INTEGER, four INTEGER, five INTEGER, six INTEGER, seven INTEGER,"
                + "eight INTEGER, nine INTEGER, ten INTEGER, one1 INTEGER, two1 INTEGER, three1 INTEGER, four1 INTEGER, five1 INTEGER, six1 INTEGER,"
                + "seven1 INTEGER, eight1 INTEGER, nine1 INTEGER, ten1 INTEGER, one2 INTEGER, two2 INTEGER, three2 INTEGER, four2 INTEGER, "
                + "deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlEachHourData);

        // 每小时卡路里数据表
        String sqlEachHourCalorieData = "CREATE TABLE IF NOT EXISTS each_hour_calorie_data_table (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "userAccount TEXT, day TEXT, one INTEGER, two INTEGER, three INTEGER, four INTEGER, five INTEGER, six INTEGER, seven INTEGER,"
                + "eight INTEGER, nine INTEGER, ten INTEGER, one1 INTEGER, two1 INTEGER, three1 INTEGER, four1 INTEGER, five1 INTEGER, six1 INTEGER,"
                + "seven1 INTEGER, eight1 INTEGER, nine1 INTEGER, ten1 INTEGER, one2 INTEGER, two2 INTEGER, three2 INTEGER, four2 INTEGER, "
                + "deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlEachHourCalorieData);

        // 睡眠数据表
        String sqlSleepData = "CREATE TABLE IF NOT EXISTS sleep_table (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "userAccount TEXT, day TEXT, sleepData TEXT, "
                + "deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlSleepData);
    }


    public Cursor selectTheDay(Context mContext, String userAccount) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT day FROM each_hour_data_table WHERE userAccount = ?;";
        Cursor mCursor = db.rawQuery(sql, new String[]{userAccount});
        return mCursor;
    }

    public Cursor selectTheDaye(Context mContext, String userAccount) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT day FROM sleep_table WHERE userAccount = ?;";
        Cursor mCursor = db.rawQuery(sql, new String[]{userAccount});
        return mCursor;
    }


    /**
     * 查询睡眠数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectTheSleepData(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlForSelect = "SELECT sleepData, dataSendOK, deviceType FROM sleep_table WHERE userAccount = ? and day = ?;";
        Cursor mCursor = db.rawQuery(sqlForSelect, new String[]{userAccount, day});
        return mCursor;
    }

    /**
     * 按类型查询睡眠数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectTheSleepData(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlForSelect = "SELECT sleepData, dataSendOK FROM sleep_table "
                + "WHERE userAccount = ? and day = ? and deviceType = ?;";
        Cursor mCursor = db.rawQuery(sqlForSelect, new String[]{userAccount, day, deviceType});
        return mCursor;
    }


    /**
     * 更新睡眠数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param dataSleep
     */
    public void updateTheSleepData(Context mContext, String userAccount, String day, String dataSleep, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        String sqlForUpdate = "UPDATE sleep_table SET sleepData = ?, dataSendOK = ? WHERE userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sqlForUpdate, new String[]{dataSleep, flag, userAccount, day, deviceType});
    }

    public void updateSleepData(Context context, String userAccount, String day, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update sleep_table set dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, day, deviceType});
    }


    /**
     * 插入睡眠数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param dataSleep
     */
    public void insertTheSleepData(Context mContext, String userAccount, String day, String dataSleep, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "INSERT INTO sleep_table(userAccount, day, sleepData, deviceType, dataSendOK)values(?, ?, ?, ?, ?)";
        db.execSQL(sql, new String[]{userAccount, day, dataSleep, deviceType, flag});
    }

    /**
     * 插入分时热量数据
     */
    public void insertEachHourCalorieData(Context mContext, String userAccount, String day, int one, int two, int three,
                                          int four, int five, int six, int seven, int eight, int nine, int ten,
                                          int one1, int two1, int three1, int four1, int five1, int six1, int seven1,
                                          int eight1, int nine1, int ten1, int one2, int two2, int three2, int four2, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        db.execSQL("insert into each_hour_calorie_data_table(userAccount, day, one, two, three, four, five, six, seven, eight, nine,"
                        + "ten, one1, two1, three1, four1, five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2, deviceType, dataSendOK)"
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{userAccount, day, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1,
                        three1, four1, five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2, deviceType, flag});
    }


    // 查询分时热量数据
    public Cursor getEachHourCalorieData(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlSelect = "select dataSendOK, deviceType, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1, five1, six1,"
                + "seven1, eight1, nine1, ten1, one2, two2, three2, four2 from each_hour_calorie_data_table "
                + "where userAccount = ? and day = ?;";
        Cursor c = db.rawQuery(sqlSelect, new String[]{userAccount, day});
        return c;
    }

    // 按类型查询分时热量数据
    public Cursor getEachHourCalorieData(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlSelect = "select dataSendOK, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1, five1, six1,"
                + "seven1, eight1, nine1, ten1, one2, two2, three2, four2 from each_hour_calorie_data_table "
                + "where userAccount = ? and day = ? and deviceType = ?;";
        Cursor c = db.rawQuery(sqlSelect, new String[]{userAccount, day, deviceType});
        return c;
    }


    // 更新热量数据
    public void updateEachHourCalorieData(Context mContext, String userAccount, String day, int one, int two, int three,
                                          int four, int five, int six, int seven, int eight, int nine, int ten,
                                          int one1, int two1, int three1, int four1, int five1, int six1, int seven1,
                                          int eight1, int nine1, int ten1, int one2, int two2, int three2, int four2,
                                          String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        String spl = "update each_hour_calorie_data_table set one = ?, two = ?, three = ?, four = ?, five = ?, six = ?, seven = ?, "
                + "eight = ?, nine = ?, ten = ?, one1 = ?, two1 = ?, three1 = ?, four1 = ?, five1 = ?, six1 = ?, seven1 = ?, "
                + "eight1 = ?, nine1 = ?, ten1 = ?, one2 = ?, two2 = ?, three2 = ?, four2 = ?, dataSendOK = ? "
                + "WHERE userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(spl, new Object[]{one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1,
                five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2, flag, userAccount, day, deviceType});
    }


    public void updateEachCalorieData(Context context, String userAccount, String day, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update each_hour_calorie_data_table set dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, day, deviceType});
    }

    // 插入分时计步数据到数据库
    public void insertEachHourData(Context mContext, String userAccount, String day, int one, int two, int three,
                                   int four, int five, int six, int seven, int eight, int nine, int ten,
                                   int one1, int two1, int three1, int four1, int five1, int six1, int seven1,
                                   int eight1, int nine1, int ten1, int one2, int two2, int three2, int four2,
                                   String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        db.execSQL("insert into each_hour_data_table(userAccount, day, one, two, three, four, five, six, seven, eight, nine, "
                        + "ten, one1, two1, three1, four1, five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2, deviceType, dataSendOK)"
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{userAccount, day, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1,
                        three1, four1, five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2, deviceType, flag});
    }


    // 更新分时计步数据
    public void updateEachHourData(Context mContext, String userAccount, String day, int one, int two, int three,
                                   int four, int five, int six, int seven, int eight, int nine, int ten,
                                   int one1, int two1, int three1, int four1, int five1, int six1, int seven1,
                                   int eight1, int nine1, int ten1, int one2, int two2, int three2, int four2,
                                   String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        String spl = "update each_hour_data_table set one = ?, two = ?, three = ?, four = ?, five = ?, six = ?, seven = ?, "
                + "eight = ?, nine = ?, ten = ?, one1 = ?, two1 = ?, three1 = ?, four1 = ?, five1 = ?, six1 = ?, seven1 = ?, "
                + "eight1 = ?, nine1 = ?, ten1 = ?, one2 = ?, two2 = ?, three2 = ?, four2 = ?, dataSendOK = ?"
                + "WHERE userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(spl, new Object[]{one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1,
                five1, six1, seven1, eight1, nine1, ten1, one2, two2, three2, four2,
                flag, userAccount, day, deviceType});
    }

    public void updateEachStepData(Context context, String userAccount, String day, String flag, String deviceType) {
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "update each_hour_data_table set dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, day, deviceType});
    }


    // 查询分时计步数据
    public Cursor getEachHourData(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlSelect = "select dataSendOK, deviceType, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1, five1, six1,"
                + "seven1, eight1, nine1, ten1, one2, two2, three2, four2 from each_hour_data_table "
                + "where userAccount = ? and day = ?;";
        Cursor c = db.rawQuery(sqlSelect, new String[]{userAccount, day});
        return c;
    }

    // 分类查询分时计步数据
    public Cursor getEachHourData(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sqlSelect = "select dataSendOK, one, two, three, four, five, six, seven, eight, nine, ten, one1, two1, three1, four1, five1, six1,"
                + "seven1, eight1, nine1, ten1, one2, two2, three2, four2 from each_hour_data_table "
                + "where userAccount = ? and day = ? and deviceType = ?;";
        Cursor c = db.rawQuery(sqlSelect, new String[]{userAccount, day, deviceType});
        return c;
    }


    /**
     * 插入离线数据到数据库
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param startTime
     * @param endTime
     * @param calorie
     * @param stepTotle
     */
    public void insertOutLineData(Context mContext, String userAccount, String type, String day,
                                  String startTime, String endTime, int calorie, int stepTotle,
                                  String hr, String sportName, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        db.execSQL("insert into outline_movement_table(userAccount, type, day, startTime, endTime, calorie, stepTotle, heartRate, sportName, deviceType, dataSendOK)"
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{userAccount, type, day, startTime, endTime, calorie, stepTotle, hr, sportName, deviceType, flag});
        Log.i(TAG, "插入离线数据");
    }

    public void updateOutLineHr(Context context, String userAccount, String day, String starTime, String endTime, String hr, String deviceType) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        db.execSQL("update outline_movement_table set heartRate = ? " +
                        "where userAccount = ? and day = ? and startTime = ? and endTime = ? and deviceType = ?",
                new String[]{hr, userAccount, day, starTime, endTime, deviceType});
    }

    /**
     * 查询离线数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectOutLineData(Context mContext, String userAccount, String day) {
        Cursor mCursor;
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        try {
            mCursor = db.rawQuery("select dataSendOK, deviceType, startTime, endTime, calorie, stepTotle, type, day, heartRate, sportName from outline_movement_table"
                            + " where userAccount = ? and day = ?;",
                    new String[]{userAccount, day});
        } catch (SQLiteException e) {
            upgradeOutlineTable(getMyWritableDatabase());
            mCursor = db.rawQuery("select dataSendOK, deviceType, startTime, endTime, calorie, stepTotle, type, day, heartRate, sportName from outline_movement_table"
                            + " where userAccount = ? and day = ?;",
                    new String[]{userAccount, day});
            Log.i(TAG, e.toString());
        }
        return mCursor;
    }

    /**
     * 按类型查询离线数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectOutLineData(Context mContext, String userAccount, String day, String deviceType) {
        Cursor mCursor;
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        try {
            mCursor = db.rawQuery("select dataSendOK, startTime, endTime, calorie, stepTotle, type, day, heartRate, sportName from outline_movement_table"
                            + " where userAccount = ? and day = ? and deviceType = ?;",
                    new String[]{userAccount, day, deviceType});
        } catch (SQLiteException e) {
            upgradeOutlineTable(getMyWritableDatabase());
            mCursor = db.rawQuery("select dataSendOK, startTime, endTime, calorie, stepTotle, type, day, heartRate, sportName from outline_movement_table"
                            + " where userAccount = ? and day = ? and deviceType = ?;",
                    new String[]{userAccount, day, deviceType});
            Log.i(TAG, e.toString());
        }
        return mCursor;
    }


    public void updateOutLineData(Context mContext, String userAccount,
                                  String day, String startTime, String endTime, String column, int type, String sportName, String name,
                                  String deviceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        String sqlUpdate = "UPDATE outline_movement_table SET " + column + " = ?, " + sportName + " = ?, dataSendOK = ? "
                + "WHERE userAccount = ? and day = ? and startTime = ? and endTime = ? and deviceType = ?";
        db.execSQL(sqlUpdate, new Object[]{type, name, flag, userAccount, day, startTime, endTime, deviceType});
//        Log.i(TAG, "更新离线数据名称--sql:" + sqlUpdate);
    }

    public void updateDataSendOK(Context context, String userAccount, String startTime, String endTime, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update outline_movement_table set dataSendOK = ? where userAccount = ? and startTime = ? and endTime = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, startTime, endTime, deviceType});
    }

    public void deleteOutLineData(Context context, String userAccount, String day, String startTime, String endTime, String deviceType) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String deleteSql = "delete from outline_movement_table where userAccount = ? and day = ? and startTime = ? " +
                "and endTime = ? and deviceType = ?";
        db.execSQL(deleteSql, new String[]{userAccount, day, startTime, endTime, deviceType});
    }


    private MyDBHelperForDayData(Context mContext) {
        super(mContext, DATABASE_NAME, null, 7);    // 创建数据库
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        int upgradeVersion = oldVersion;
//        if(upgradeVersion == 2)
//        {
//
//        }
//        Log.i(TAG, "调用数据库升级---");
        if (newVersion == 2) {
            // 升级数据库，添加sportName字段
            upgradeOutlineTable(db);
            // 创建账户列表
            creatAccountMemoryTable(db);
            // 创建疲劳数据表
            createFatigueTable(db);
        }
        if (newVersion == 7) {
            addFieldToTable(db);
        }
    }

    private void addFieldToTable(SQLiteDatabase db) {
        addAllDayData(db);
        addHrData(db);
        addOutLineDbField(db);
        addEachStepField(db);
        addEachKcalField(db);
        addSleepField(db);
        addFatigueField(db);

        addBloodField(db);
    }

    private void addBloodField(SQLiteDatabase db) {
        db.execSQL("drop TABLE  if exists blood_date_table");
        try {
            db.execSQL("ALTER TABLE blood_date_table ADD deviceType TEXT DEFAULT DEFAULT \"002\"");
            db.execSQL("ALTER TABLE blood_date_table ADD dataSendOK TEXT DEFAULT");
            db.execSQL("ALTER TABLE blood_date_table ADD other01 TEXT DEFAULT");
            db.execSQL("ALTER TABLE blood_date_table ADD other02 TEXT DEFAULT");
        } catch (Exception e) {
            createBloodTableIfNotEx(db);
        }
    }

    private void addFatigueField(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE fatigue_table ADD deviceType TEXT DEFAULT \"001\"");
            db.execSQL("ALTER TABLE fatigue_table ADD dataSendOK TEXT");
            db.execSQL("ALTER TABLE fatigue_table ADD other01 TEXT");
            db.execSQL("ALTER TABLE fatigue_table ADD other02 TEXT");
        } catch (SQLiteException e) {
            createFatigueTable(db);
        }
    }

    private void addSleepField(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE sleep_table ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE sleep_table ADD dataSendOK TEXT");
        db.execSQL("ALTER TABLE sleep_table ADD other01 TEXT");
        db.execSQL("ALTER TABLE sleep_table ADD other02 TEXT");
    }

    private void addEachKcalField(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE each_hour_calorie_data_table ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE each_hour_calorie_data_table ADD dataSendOK");
        db.execSQL("ALTER TABLE each_hour_calorie_data_table ADD other01");
        db.execSQL("ALTER TABLE each_hour_calorie_data_table ADD other02");
    }

    private void addEachStepField(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE each_hour_data_table ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE each_hour_data_table ADD dataSendOK TEXT");
        db.execSQL("ALTER TABLE each_hour_data_table ADD other01 TEXT");
        db.execSQL("ALTER TABLE each_hour_data_table ADD other02 TEXT");
    }

    private void addOutLineDbField(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE outline_movement_table ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE outline_movement_table ADD dataSendOK TEXT");
        db.execSQL("ALTER TABLE outline_movement_table ADD other01 TEXT");
        db.execSQL("ALTER TABLE outline_movement_table ADD other02 TEXT");
    }

    /**
     * 在heart_reat_table中添加四个字段
     *
     * @param db
     */
    private void addHrData(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE heart_reat_table ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE heart_reat_table ADD dataSendOK TEXT");
        db.execSQL("ALTER TABLE heart_reat_table ADD other01 TEXT");
        db.execSQL("ALTER TABLE heart_reat_table ADD other02 TEXT");
    }

    /**
     * 在day_data_one添加四个字段，后两个为备用字段
     *
     * @param db
     */
    private void addAllDayData(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE day_data_one ADD deviceType TEXT DEFAULT \"001\"");
        db.execSQL("ALTER TABLE day_data_one ADD dataSendOK TEXT");
        db.execSQL("ALTER TABLE day_data_one ADD other01 TEXT");
        db.execSQL("ALTER TABLE day_data_one ADD other02 TEXT");
    }

    private void upgradeOutlineTable(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE outline_movement_table ADD sportName");
    }

    /**
     * 插入疲劳数据
     *
     * @param context
     * @param userAccount
     * @param day
     * @param data
     */
    public void insertFatigueData(Context context, String userAccount, String day, String data, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sqlInsert = "INSERT INTO fatigue_table(userAccunt, day, fatigue, deviceType, dataSendOK)values(?, ?, ?, ?, ?)";
        db.execSQL(sqlInsert, new String[]{userAccount, day, data, deviceType, flag});
    }

    /**
     * 按类型查询hrv
     *
     * @param context
     * @param userAccount
     * @param day
     * @param deviceType
     * @return
     */
    public Cursor selectFatigueData(Context context, String userAccount, String day, String deviceType) {
        Cursor cursor;
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "SELECT fatigue, dataSendOK FROM fatigue_table WHERE userAccunt = ? and day = ? and deviceType = ?";
        try {
            cursor = db.rawQuery(sql, new String[]{userAccount, day, deviceType});
        } catch (SQLiteException e) {
            createFatigueTable(getMyWritableDatabase());
            cursor = db.rawQuery(sql, new String[]{userAccount, day, deviceType});
        }
        return cursor;
    }

    /**
     * 查询hrv
     *
     * @param context
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectFatigueData(Context context, String userAccount, String day) {
        Cursor cursor;
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "SELECT fatigue, dataSendOK, deviceType FROM fatigue_table WHERE userAccunt = ? and day = ?";
        try {
            cursor = db.rawQuery(sql, new String[]{userAccount, day});
        } catch (SQLiteException e) {
            createFatigueTable(getMyWritableDatabase());
            cursor = db.rawQuery(sql, new String[]{userAccount, day});
        }
        return cursor;
    }

    /**
     * 更新hrv
     *
     * @param context
     * @param userAccount
     * @param day
     * @param data
     * @param deviceType
     */
    public void updateFatigueData(Context context, String userAccount, String day, String data, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "UPDATE fatigue_table SET fatigue = ?, dataSendOK = ? WHERE userAccunt = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{data, flag, userAccount, day, deviceType});
    }

    public void updateFatigue(Context context, String userAccout, String day, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update fatigue_table set dataSendOK = ? where userAccunt = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccout, day, deviceType});
    }


    public void insertAccount(Context mContext, String account) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        String sql = "INSERT INTO account_memory(account)values(?)";
        db.execSQL(sql, new String[]{account});
    }

    public Cursor selectAccount(Context mContext) {
        Cursor cursor;
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "select * from account_memory";
        try {
            cursor = db.rawQuery(sql, null);
        } catch (SQLiteException e) {
            Log.e(TAG, e.toString());
            creatAccountMemoryTable(getMyWritableDatabase());
            cursor = db.rawQuery(sql, null);
        }
        return cursor;
    }

    /**
     * 查询血压，根据具体到秒的时间
     *
     * @param context
     * @param account
     * @param date
     * @param time
     * @return
     */
    public Cursor selectBloodData(Context context, String account, String date, String time, String deviceType) {
        Cursor cu;
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "select time, highPre, lowPre, heartRate, spo2, hrv from blood_date_table" +
                " where account = ? and date = ? and time = ? and deviceType = ?";
        try {
            cu = db.rawQuery(sql, new String[]{account, date, time, deviceType});
        } catch (SQLiteException e) {
            createBloodTableIfNotEx(getMyWritableDatabase());
            cu = db.rawQuery(sql, new String[]{account, date, time, deviceType});
        }
        return cu;
    }

    /**
     * 按类型查询血压数据
     *
     * @param context
     * @param account
     * @param date
     * @param deviceType
     * @return
     */
    public Cursor selectBloodData(Context context, String account, String date, String deviceType) {
        Cursor cu = null;
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "select dataSendOK, time, highPre, lowPre, heartRate, spo2, hrv " +
                "from blood_date_table where account = ? and date = ? and deviceType = ?";
        try {
            cu = db.rawQuery(sql, new String[]{account, date, deviceType});
        } catch (SQLiteException e) {
            deleteBlood(db);
            createBloodTableIfNotEx(db);
        }
        return cu;
    }

    /**
     * 查询全天同一type的血压数据
     *
     * @param context
     * @param account
     * @param date
     * @return
     */
    public Cursor selectBloodData(Context context, String account, String date) {
        Cursor cu = null;
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String sql = "select dataSendOK, deviceType, date, time, highPre, lowPre, heartRate, spo2, hrv " +
                "from blood_date_table where account = ? and date = ?";
        try {
            cu = db.rawQuery(sql, new String[]{account, date});
        } catch (SQLiteException e) {
            deleteBlood(db);
            createBloodTableIfNotEx(getInstance(context).getMyWritableDatabase());
        }
        return cu;
    }

    /**
     * 删除血压表重新创建
     *
     * @param db
     */
    private void deleteBlood(SQLiteDatabase db) throws SQLiteException {
        try {
            String sql = "delete from blood_date_table";
            db.execSQL(sql);
        } catch (SQLiteException ee) {
        }
    }

    private void createBloodTableIfNotEx(SQLiteDatabase db) {
        String createSql = "create table if not exists blood_date_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "account TEXT, date TEXT, time TEXT, highPre INTEGER, lowPre INTEGER, heartRate INTEGER, spo2 INTEGER, hrv INTEGER, "
                + "deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(createSql);
    }

    public void insertBloodData(String account, String dateS, String time, int highPre,
                                int lowPre, int hr, int sp02, int hrv, String deviceType, String flag) {
        String insertSql = "insert into blood_date_table(account, date, time, highPre, "
                + "lowPre, heartRate, spo2, hrv, deviceType, dataSendOK)"
                + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteDatabase db = getMyWritableDatabase();
        db.execSQL(insertSql, new Object[]{account, dateS, time, highPre, lowPre, hr, sp02, hrv, deviceType, flag});
    }

    public void updateBloodDB(Context context, String account, String date, String time, String deviceType, String flag) {
        Log.i(TAG, "更新血压：" + account + "--" + date + "--" + time + "--" + deviceType);
        String sql = "update blood_date_table set dataSendOK = ? where account = ? and date = ? and time = ? and deviceType = ?;";
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        db.execSQL(sql, new String[]{flag, account, date, time, deviceType});
    }

    public void createInfoDataTable(SQLiteDatabase db) {
        String createSql = "create table if not exists info_push_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "appName TEXT);";
        db.execSQL(createSql);
    }

    public Cursor selectInfoData(Context context) {
        Cursor cu = null;
        String selectSql = "select * from info_push_table";
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        try {
            cu = db.rawQuery(selectSql, null);
        } catch (SQLiteException e) {
//            e.printStackTrace();
            createInfoDataTable(getMyWritableDatabase());
        }
        return cu;
    }

    public void insertInfoData(String appName) {
        SQLiteDatabase db = getMyWritableDatabase();
        String sql = "INSERT into info_push_table(appName)values(?)";
        db.execSQL(sql, new String[]{appName});
    }

    public void deleteInfoData(String appName) {
        SQLiteDatabase db = getMyWritableDatabase();
        String deleteSql = "delete from info_push_table where appName = ?";
        db.execSQL(deleteSql, new String[]{appName});
    }

    /**
     * 创建疲劳值数据表
     *
     * @param db
     */
    private void createFatigueTable(SQLiteDatabase db) {
        String sqlFatigue = "CREATE TABLE IF NOT EXISTS fatigue_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userAccunt TEXT, day TEXT, fatigue TEXT, "
                + "deviceType TEXT, dataSendOK TEXT, other01 TEXT, other02 TEXT);";
        db.execSQL(sqlFatigue);
    }

    /**
     * 创建数据库纪录账号
     *
     * @param db
     */
    private void creatAccountMemoryTable(SQLiteDatabase db) {
        String sqlAccount = "CREATE TABLE if not exists account_memory (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account TEXT);";
        db.execSQL(sqlAccount);
    }

    public void deleteAccount(Context context, String account) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "delete from account_memory where account = ?";
        db.execSQL(sql, new String[]{account});
    }

    /**
     * 插入心率数据
     *
     * @param mcContext
     * @param userAccount
     * @param day
     */
    public void insertHrAccount(Context mcContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mcContext).getMyWritableDatabase();
        String sql = "INSERT INTO heart_reat_table(userAccount, day, deviceType)values(?, ?, ?)";
        db.execSQL(sql, new String[]{userAccount, day, deviceType});
    }

    /**
     * 读取所有心率数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectHrAccount(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT * FROM heart_reat_table WHERE userAccount = ? and day = ?";
        Cursor mCursor = db.rawQuery(sql, new String[]{userAccount, day});
        return mCursor;
    }

    /**
     * 按类型读取所有心率数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @return
     */
    public Cursor selectHrAccount(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT * FROM heart_reat_table WHERE userAccount = ? and day = ? and deviceType = ?";
        Cursor mCursor = db.rawQuery(sql, new String[]{userAccount, day, deviceType});
        return mCursor;
    }

    public Cursor selectHRFromCulums(Context mContext, String userAccount, String day, String cus, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "select " + cus + " from heart_reat_table where userAccount = ? and day = ? and deviceType = ?";
        return db.rawQuery(sql, new String[]{userAccount, day, deviceType});
    }


    /**
     * 对应某个字段插入心率数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param one
     * @param hr
     */
    public void insertHr(Context mContext, String userAccount, String day, String one, String hr) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        db.execSQL("insert into heart_reat_table(userAccount, day, " + one + ")"
                        + "values(?, ?, ?)",
                new Object[]{userAccount, day, hr});
        Log.i(TAG, "插入心率数据" + one);
    }


    /**
     * 查询指定column的数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param column
     */
    public Cursor selecteOneColumnHr(Context mContext, String userAccount, String day, String column, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        Log.i(TAG, "查询" + column + "列");
        return db.rawQuery("SELECT " + column + " FROM heart_reat_table WHERE userAccount = ? AND day = ? and deviceType = ?;",
                new String[]{userAccount, day, deviceType});

    }


    /**
     * 更新指定列的数据
     *
     * @param mContext
     * @param userAccount
     * @param day
     * @param column
     * @param data
     */
    public void updateHr(Context mContext, String userAccount, String day, String column, String data, String deivceType, String flag) {
        SQLiteDatabase db = getInstance(mContext).getMyWritableDatabase();
        db.execSQL("UPDATE heart_reat_table SET " + column + " = ?, dataSendOK = ? WHERE userAccount = ? and day = ? and deviceType = ?;",
                new String[]{data, flag, userAccount, day, deivceType});
        Log.i(TAG, "更新心率数据：" + column + "列");
    }


    public void updateHRSend(Context context, String userAccount, String day, String deviceType, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update heart_reat_table set dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, day, deviceType});
    }


    /**
     * 插入全天数据，
     *
     * @param context
     * @param userAccount
     * @param day
     * @param stepAll
     * @param calorie
     * @param mileage
     * @param movementTime
     * @param sitTime
     */
    public void insert(final Context context, String deviceType, final String userAccount, final String day, final int stepAll,
                       final int calorie, final int mileage, final String movementTime, final int moveCalorie,
                       final String sitTime, final int sitCalorie, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        db.execSQL("insert into day_data_one(userAccount, day, stepAll, calorie, mileage, movementTime, moveCalorie,"
                        + " sitTime, sitCalorie, deviceType, dataSendOK)"
                        + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{userAccount, day, stepAll, calorie, mileage, movementTime, moveCalorie, sitTime, sitCalorie, deviceType, flag});
        Log.i(TAG, "插入全天数据");
    }

    public void insertDayDataOnlySteps(Context context, String userAccount, String day, String deviceType, String steps, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "insert into day_data_one(userAccount, day, stepAll, deviceType, dataSendOK)values(?, ?, ?, ?, ?)";
        db.execSQL(sql, new String[]{userAccount, day, steps, deviceType, flag});
    }


    /**
     * 查询全天数据
     *
     * @param mContext
     * @param account
     * @param day
     * @return
     */
    public Cursor selecteDayData(Context mContext, String account, String day) {

        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        Cursor mCursor = db.rawQuery("select deviceType, dataSendOK, stepAll, calorie, mileage, movementTime, moveCalorie, sitTime, sitCalorie"
                        + " from day_data_one where userAccount = ? and day = ?;",
                new String[]{account, day});

        Log.i(TAG, "查询全天数据selecteDayData : account:" + account + "day:" + day);
        return mCursor;
    }

    /**
     * 按类型查询全天数据
     *
     * @param mContext
     * @param account
     * @param day
     * @return
     */
    public Cursor selecteDayData(Context mContext, String account, String day, String deviceType) {

        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        Cursor mCursor = db.rawQuery("select deviceType, dataSendOK, stepAll, calorie, mileage, movementTime, moveCalorie, sitTime, sitCalorie"
                        + " from day_data_one where userAccount = ? and day = ? and deviceType = ?;",
                new String[]{account, day, deviceType});

        Log.i(TAG, "查询全天数据selecteDayData : account:" + account + "day:" + day);
        return mCursor;
    }

    public Cursor selectDayDataNeedUpdate(Context context, String account, String day) {
        SQLiteDatabase db = getInstance(context).getMyReadableDatabase();
        String selectSql = "select dataSendOK from day_data_one where userAccount = ? and day = ?;";
        return db.rawQuery(selectSql, new String[]{account, day});
    }


    public Cursor selectTheCalorieFromDb(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT calorie FROM day_data_one WHERE userAccount = ? and day = ?;";
        return db.rawQuery(sql, new String[]{userAccount, day});
    }

    public Cursor selectTheCalorieFromDb(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT calorie FROM day_data_one WHERE userAccount = ? and day = ? and deviceType = ?;";
        return db.rawQuery(sql, new String[]{userAccount, day, deviceType});
    }


    public Cursor selectTheStepAllFromDB(Context mContext, String userAccount, String day) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT stepAll FROM day_data_one where userAccount = ? and day = ?;";
        return db.rawQuery(sql, new String[]{userAccount, day});
    }

    public Cursor selectTheStepAllFromDB(Context mContext, String userAccount, String day, String deviceType) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "SELECT stepAll FROM day_data_one where userAccount = ? and day = ? and deviceType = ?;";
        return db.rawQuery(sql, new String[]{userAccount, day, deviceType});
    }

    public Cursor selectTheDateFormDb(Context mContext, String userAccount) {
        SQLiteDatabase db = getInstance(mContext).getMyReadableDatabase();
        String sql = "select day from day_data_one where userAccount = ?;";
        return db.rawQuery(sql, new String[]{userAccount});
    }

    /**
     * 更新当天全天数据
     *
     * @param context
     * @param userAccount
     * @param day
     * @param stepAlls
     * @param calorie
     * @param mileage
     * @param movementTime
     * @param sitTime      String sql = " CREATE TABLE if not exists day_data_one (_id INTEGER PRIMARY KEY AUTOINCREMENT, "        // 建表四个字段，
     *                     + "userAccount TEXT , day TEXT , stepAll Integer , calorie Integer ,"
     *                     + "mileage Integer , movementTime TEXT, moveCalorie INTEGER, sitTime TEXT, sitCalorie INTEGER); ";
     *                     <p>
     *                     "insert into day_data_one(userAccount, day, stepAll, calorie, mileage, movementTime, moveCalorie,"
     *                     +" sitTime, sitCalorie)"
     *                     +"values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
     */
    public void updateDayDataToday(Context context, String deviceType, String userAccount, String day, int stepAlls,
                                   int calorie, int mileage, String movementTime, int moveCalorie,
                                   String sitTime, int sitCalorie, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        // 此处不能用and连接，经多次测试必须用，隔开
        db.execSQL("update day_data_one set stepAll = ?, calorie = ?"
                        + ", mileage = ?, movementTime = ?, moveCalorie = ?"
                        + ", sitTime = ?, sitCalorie = ?, dataSendOK = ? "
                        + "where userAccount = ? and day = ? and deviceType = ?;",
                new Object[]{stepAlls, calorie, mileage, movementTime, moveCalorie, sitTime, sitCalorie, flag, userAccount, day, deviceType});
        Log.i(TAG, "更新全天数据" + stepAlls);
        Log.i(TAG, "更新的新数据：" + stepAlls + "--" + calorie + "--" + mileage + "--" + movementTime + "--"
                + moveCalorie + "--" + sitTime + "--" + sitCalorie);
    }

    public void updateDayDataOnStep(Context context, String deviceType, String userAccount, String date, String steps, String flag) {
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update day_data_one set stepAll = ?, dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{steps, flag, userAccount, date, deviceType});
    }

    public void updateSendFlag(Context context, String deviceType, String userAccount, String day, String flag) {
        Log.i(TAG, "更新的新数据：" + userAccount + "--" + day + "--" + flag + "--" + deviceType);
        SQLiteDatabase db = getInstance(context).getMyWritableDatabase();
        String sql = "update day_data_one set dataSendOK = ? where userAccount = ? and day = ? and deviceType = ?;";
        db.execSQL(sql, new String[]{flag, userAccount, day, deviceType});
    }


    public boolean deleteAllDeviceData(Context c) {
        SQLiteDatabase db = getInstance(c).getMyWritableDatabase();
        String[] all = new String[8];
        all[0] = "delete from day_data_one";
        all[1] = "delete from heart_reat_table";
        all[2] = "delete from outline_movement_table";
        all[3] = "delete from each_hour_data_table";
        all[4] = "delete from each_hour_calorie_data_table";
        all[5] = "delete from sleep_table";
        all[6] = "delete from blood_date_table";
        all[7] = "delete from fatigue_table";
        for (int i = 0; i < all.length; i++) {
            try {
                db.execSQL(all[i]);
            } catch (Exception e) {
                Log.i(TAG, "异常111");
            }
        }
        return true;
    }


}