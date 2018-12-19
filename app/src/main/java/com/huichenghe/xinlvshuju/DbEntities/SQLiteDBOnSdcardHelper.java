package com.huichenghe.xinlvshuju.DbEntities;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.File;
import java.io.IOException;

/**
 * 数据库帮助类，类似与SQLiteOpenHelper，
 * 不同之处在于此类创建的数据库不是在data/data
 * 而是在sd卡上
 * Created by lixiaoning on 15-11-18.
 */
public abstract class SQLiteDBOnSdcardHelper {
    public static final String TAG = SQLiteDBOnSdcardHelper.class.getSimpleName();
    private static final boolean DEBUG_STRICT_READONLY = false;
    private SQLiteDatabase mSqLiteDatabase;     // 数据库类
    private final Context mContext;
    private final String name;
    private final SQLiteDatabase.CursorFactory mCursorFactory;
    private final int mNewVersion;
    private final DatabaseErrorHandler mDatabaseErrorHandler;
    private boolean mIsInitializing;            // 表示重复调用
    private boolean mEnableWriteAheadLogging;

    public SQLiteDBOnSdcardHelper(Context mContext,
                                  String name,
                                  SQLiteDatabase.CursorFactory mCursorFactory,
                                  int version) {
        // 调用下边的构造方法
        this(mContext, name, mCursorFactory, version, null);
    }

    /**
     * 构造方法
     *
     * @param mContext       上下文对象
     * @param name           数据库名字
     * @param mCursorFactory 游标工厂
     * @param version        数据库版本号
     * @param mErrorHandler  错误帮助类
     */
    public SQLiteDBOnSdcardHelper(Context mContext,
                                  String name,
                                  SQLiteDatabase.CursorFactory mCursorFactory,
                                  int version,
                                  DatabaseErrorHandler mErrorHandler) {
        // 对数据库版本进行判断
        if (version < 1) throw new IllegalArgumentException("数据库起始版本必须是大于等于1，当前是：" + version);
        this.mContext = mContext;
        this.name = name;
        this.mCursorFactory = mCursorFactory;
        this.mNewVersion = version;
        this.mDatabaseErrorHandler = mErrorHandler;
    }


    public void setWriteAheadLoggingEnabled(boolean enabled) {
        synchronized (this) {
            if (mEnableWriteAheadLogging != enabled) {
                if (mSqLiteDatabase != null && mSqLiteDatabase.isOpen() && !mSqLiteDatabase.isReadOnly()) {
                    if (enabled) {
                        mSqLiteDatabase.enableWriteAheadLogging();
                    } else {
                        mSqLiteDatabase.disableWriteAheadLogging();
                    }
                }
                mEnableWriteAheadLogging = enabled;
            }
        }
    }


    /**
     * 获取数据库名字
     *
     * @return
     */
    public String getDataBaseName() {
        return name;
    }


    /**
     * 打开一个可写的数据库
     *
     * @return
     */
    public SQLiteDatabase getMyWritableDatabase() {
        synchronized (this)     // 添加同步锁
        {
            return getMyDatabaseLocked(true);
        }
    }

    public SQLiteDatabase getMyReadableDatabase() {
        synchronized (this) {
            return getMyDatabaseLocked(false);
        }
    }


    /**
     * 获取一个带锁的SQLiteDatabase
     *
     * @param writable 表示是否可写
     * @return 返回SQLiteDatabase
     */
    private SQLiteDatabase getMyDatabaseLocked(boolean writable) {
        if (mSqLiteDatabase != null)         // 若数据库对象已构建
        {
            if (!mSqLiteDatabase.isOpen())   // 若数据库已经打开
            {
                // 用户已关闭数据库
                // 引用指向空
                mSqLiteDatabase = null;
            } else if (!writable || !mSqLiteDatabase.isReadOnly()) {
                // 用户已经打开数据库
                Log.i(TAG, "数据库已打开直接返回");
                return mSqLiteDatabase;
            }
        }

        if (mIsInitializing) // 为true代表已经调用过了，抛出重复调用异常
        {
            throw new IllegalStateException("getDatabase called recursively 重复调用");
        }

        SQLiteDatabase db = mSqLiteDatabase;    // 与第一个判断进行区分

        try {
            mIsInitializing = true;
            if (db != null) {
                if (writable && db.isReadOnly()) {
//                    db.reopenReadWrite();
                }
            } else if (name == null) {
                db = SQLiteDatabase.create(null);
            } else {
                try {
                    if (DEBUG_STRICT_READONLY && !writable) {
                        final String path = getDatabasePath();
                        if(path != null && !path.equals(""))
                        {
                            db = SQLiteDatabase.openDatabase(path,
                                    mCursorFactory,
                                    SQLiteDatabase.OPEN_READONLY,
                                    mDatabaseErrorHandler);
                            Log.i(TAG, "打开只读数据库");
                        }

                    }
                    else
                    {
                        String patha = getDatabasePath();
                        if(patha != null && !patha.equals(""))
                        {
                            db = SQLiteDatabase.openOrCreateDatabase(patha, mCursorFactory);
                            Log.i(TAG, "创建数据库ok" + db + patha);
                        }

                    }
                }
                catch (SQLiteException ex)
                {
                    if(writable)
                    {
                        throw ex;
                    }
                    Log.i(TAG, "不能打开：" + name + "稍等（正在重试只读）：", ex);
                    String path = getDatabasePath();
                    if(path != null && !path.equals(""))
                    {
                        db = SQLiteDatabase.openDatabase(path,
                                mCursorFactory,
                                SQLiteDatabase.OPEN_READONLY,
                                mDatabaseErrorHandler);
                    }
                }
            }


            onConfigure(db);

            // 获取数据库版本
            final int version = db.getVersion();
            Log.i(TAG, "当前数据库版本：" + version + "新版本：" + mNewVersion);
            if(version != mNewVersion)          // 如果版本不同
            {
                if(db.isReadOnly())
                {
                    throw new SQLiteException("不能升级只读数据库，从：" + version + "到" + mNewVersion);
                }
                db.beginTransaction();
                try {
                    if(version == 0)
                    {
                        onCreate(db);
                    }
                    else
                    {
                        if(version > mNewVersion)
                        {
                            onDowngrade(db, version, mNewVersion);
                        }
                        else
                        {
                            onUpgrade(db, version, mNewVersion);
                        }
                    }

                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();

                }
                finally
                {
                    db.endTransaction();
                }
            }
//                db.setVersion(1);
                onOpen(db);
                if(db.isReadOnly())
                {
                    Log.i(TAG, "opend" + name + "只读模式");
                }
                mSqLiteDatabase = db;
                return db;

        } finally
        {
            mIsInitializing = false;
            if(db != null && db != mSqLiteDatabase)
            {
                db.close();
            }
        }
    }




    public synchronized void close()
    {
        if(mIsInitializing)throw new IllegalStateException("Close during initialization");

        if(mSqLiteDatabase != null && mSqLiteDatabase.isOpen())
        {
            mSqLiteDatabase.close();
            mSqLiteDatabase = null;
        }
    }



    public void onConfigure(SQLiteDatabase db) {}
    public void onCreate(SQLiteDatabase db){}

    public void onOpen(SQLiteDatabase db){}

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        throw new SQLiteException("不能升级只读数据库，从：" + oldVersion + "到" + newVersion);
    }

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);


    /**
     * 判断设备有无sd卡，并创建数据库文件
     * @return
     *  String filePath = MyConfingInfo.SDCARD_DATA_ROOT_DIR;
    File fileP = new File(filePath);
    if(!fileP.exists())fileP.mkdirs();
    File file = new File(filePath + File.separator + "liveData.txt");
    if(!file.exists())
    {
    try {
    file.createNewFile();
    } catch (IOException e) {
    e.printStackTrace();
    }
    }
     */
    private String getDatabasePath()
    {
        String dbF = null;
        // 判断有无sd卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String path = SDPathUtils.getSdcardPath() + MyConfingInfo.DATABASE_PATH;
            File dbFile = new File(path);
            if (!dbFile.exists())
            {
                dbFile.mkdirs();
            }
            String absPath = path + File.separator + name;
            Log.i(TAG, "文件夹路径：" + absPath);
            File dbSubFile = new File(absPath);
            boolean isFileCreateSuccess = false;

            if (!dbSubFile.exists()) {
                try {
                        dbSubFile.createNewFile();
                        isFileCreateSuccess = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "创建文件失败：" + e.toString());
                }
            } else {
                isFileCreateSuccess = true;
            }
            if (isFileCreateSuccess) {
               dbF = absPath;
            }
        }
        if(dbF == null)
        {
            throw new SQLiteException("创建数据库的路径不能为:null");
        }
        return  dbF;
    }

}
