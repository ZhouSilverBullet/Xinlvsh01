package com.huichenghe.xinlvshuju;

import android.content.Context;
import android.util.Log;

import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by lixiaoning on 2016/8/5.
 */
public class LogHelper
{
    public static final String TAG = LogHelper.class.getSimpleName();
    private LogDumper mLogDumper = null;

    private int mPId;
    private static LogHelper helper;
    private LogHelper(Context context)
    {
        mPId = android.os.Process.myPid();
        inti(context);
    }

    private void inti(Context context)
    {
        File files = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR);
        if(!files.exists())
        {
            files.mkdirs();
        }
    }

    ;
    public static LogHelper getLogcatHelper(Context context)
    {
        if(helper == null)
        {
            helper = new LogHelper(context);
        }
        Log.i(TAG, "mLogDumper_instance");
        return helper;
    }

    public void startLog()
    {
        if(mLogDumper == null)
        {
            mLogDumper = new LogDumper(String.valueOf(mPId), SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR);
        }
        mLogDumper.start();
        Log.i(TAG, "mLogDumper_start");
    }

    public void stopLog()
    {
        if(mLogDumper != null)
        {
            mLogDumper.stopLog();
            mLogDumper = null;
        }
    }



    private class LogDumper extends Thread
    {
        private java.lang.Process logcatProcess;
        private BufferedReader bufferedReader;
        private boolean isRunning = true;
        private String cmds = null;
        private String mPid;
        private FileOutputStream fileOutputStream;
        public LogDumper(String mPid, String dir)
        {
            Log.i(TAG, "mLogDumper_oncreate");
            this.mPid = mPid;
            try {
                fileOutputStream = new FileOutputStream(new File(dir, "mistep.log"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            cmds = "logcat *:e *:i | grep \"(" + mPid + ")\"";
        }

        public void stopLog()
        {
            isRunning = false;
        }

        @Override
        public void run()
        {
            super.run();
            Log.i(TAG, "mLogDumper_running");
            try {
                logcatProcess = Runtime.getRuntime().exec(cmds);
                bufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()), 1024);
                String line = null;
                while (isRunning && (line = bufferedReader.readLine()) != null)
                {
                    if(!isRunning)
                    {
                        break;
                    }
                    if(line.length() == 0)
                    {
                        continue;
                    }
                    if(fileOutputStream != null && line.contains(mPid))
                    {
                        fileOutputStream.write((getCurrentTime() + "\u3000" + line + "\r\n").getBytes("utf-8"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                if(logcatProcess != null)
                {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bufferedReader = null;
                }
                if(fileOutputStream != null)
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileOutputStream = null;
                }
            }
        }



        private String getCurrentTime()
        {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            return new SimpleDateFormat("yyyy-MM-dd++HH-mm-ss-SS").format(calendar.getTime());
        }
    }




}
