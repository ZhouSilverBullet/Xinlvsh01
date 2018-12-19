package com.huichenghe.xinlvshuju.http;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huichenghe.bleControl.Ble.BluetoothLeService;
import com.huichenghe.bleControl.upgrande.UpdateVersionTask;
import com.huichenghe.xinlvshuju.DataEntites.updateInfoEntity;
import com.huichenghe.xinlvshuju.NoDoubleClickListener;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.Utils.ProgressUtils;
import com.huichenghe.xinlvshuju.mainpack.MyApplication;
import com.huichenghe.xinlvshuju.slide.AboutActivity;
import com.huichenghe.xinlvshuju.slide.DeviceAmdActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by lixiaoning on 16-1-6.
 */
public class getHardVersionHelper
{
    private static final String TAG = getHardVersionHelper.class.getSimpleName();
    private String hardVersion = null;
    private String blueVersion = null;
    private String softVersion = null;
    private String language = null;
    private boolean isShowToast = false;
    private Context context ;
    private String FILE_OF_MCU = "mcu_update.img";
    private String FILE_OF_BLUETOOTH = "bluetooth_update.img";
    private updateAdapter adapterU;
    private String filePath;
    private byte canContinue = (byte)0xff;
    private byte endSuccess = (byte)0xff;
    private int positionRemove;
    private int totalPackege = 0;
    private int eachPackege = 0;
    private boolean isOk = false;
    private boolean isOverUpdate = false;
    private boolean isCancle = false;
    private static getHardVersionHelper mgetGetHardVersionHelper;
    private MyUpdateRecever re;
    private UpdateVersionTask updateTask = null;

    /**
     *
     * @return

    public static getHardVersionHelper getInstance(Context context, String version, boolean isShowToast)
    {
        if(mgetGetHardVersionHelper == null)
        {
            mgetGetHardVersionHelper = new getHardVersionHelper(context, version, isShowToast);
        }
//        else if(mgetGetHardVersionHelper != null)
//        {
//            mgetGetHardVersionHelper = null;
//            System.gc();
//            mgetGetHardVersionHelper = new getHardVersionHelper(context, version, isShowToast);
//
//        }
        return mgetGetHardVersionHelper;
    }*/

    public getHardVersionHelper()
    {
    }


    public void initHardVersionHelper(Context context, String version, boolean isShowToast)
    {
        Log.i(TAG, "当前版本号：" + version);
        this.context = context;
        this.isShowToast = isShowToast;
        String[] vvv = version.split("/");
        hardVersion = String.valueOf(Integer.valueOf(vvv[0], 16));
        blueVersion = vvv[1];
        softVersion = vvv[2];
        blueVersion = formatTheVersion(blueVersion);
        softVersion = formatTheVersion(softVersion);
        Locale locale = context.getResources().getConfiguration().locale;
        String lan = locale.getLanguage();
        if(lan.equals("zh"))
        {
            language = "zh-ch";
        }
        else
        {
            language = "en-us";
        }
    }


    private void registBroactReceiver(Context mContext)
    {
//        (re == null) ? re = new MyUpdateRecever() : re;
        if(re == null)
        {
            re = new MyUpdateRecever();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE);
//        filter.addAction(MyConfingInfo.ON_DEVICE_STATE_CHANGE);
        mContext.registerReceiver(re, filter);
    }

    private String formatTheVersion(String blueVersion)
    {
        Log.i(TAG, "版本号：" + blueVersion);
        String one = blueVersion.substring(0, 1);
        String two = blueVersion.substring(1);
        int twoInt = Integer.parseInt(two, 16);
        if(twoInt < 10)
        {
            two = "0" + twoInt;
        }
        else
        {
            two = twoInt + "";
        }
        return one + "." + two;
    }

    public void getNewVersionFromNet()
    {
        new MyTaskForHardVersion().execute();
    }

    class MyTaskForHardVersion extends AsyncTask<Void, Void, Boolean>
    {
        String resultRe;
        @Override
        protected Boolean doInBackground(Void... params)
        {
            try {
                resultRe = getNewVersion();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(resultRe != null && !resultRe.equals(""))
            {
                if(resultRe.equals("--1"))
                {
                    if(ProgressUtils.getInstance().isShowing())
                    {
                        ProgressUtils.getInstance().closeProgressDialog();
                    }
                    MyToastUitls.showToast(context, R.string.net_wrong, 1);
                }
                parseTheResult(resultRe);
            }
        }
    }



    private String getNewVersion() throws IOException
    {
        String path = MyConfingInfo.WebRoot +
                "bracelet_version?softversion=" + softVersion +
                "&hardversion=" + hardVersion +
                "&blueversion=" + blueVersion +
                "&language=" + language;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6 * 1000);
            connection.setReadTimeout(6 * 1000);
            connection.connect();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "--1";

        } catch (IOException e)
        {
            e.printStackTrace();
            return "--1";
        }

        InputStreamReader inputReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(inputReader);
        String r = "";
        String result = "";
        while ((r = reader.readLine()) != null)
        {
            result += r;
        }
        Log.i(TAG, "获取版本号的结果:" + result);
        Log.i(TAG, "获取版本号的结果:" + path);
        inputReader.close();
        reader.close();
        connection.disconnect();
        return result;
    }

    private void parseTheResult(String result)
    {
        if(onRequesBack != null)
        onRequesBack.onDataBack();
        if(ProgressUtils.getInstance().isShowing())
        {
            ProgressUtils.getInstance().closeProgressDialog();
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            String results = jsonObject.getString("result");
            if(results != null && results.equals("1"))
            {
                String param = jsonObject.getString("param");
                parseTheJsonArray(param);
            }
            else if(results != null && results.equals("0"))
            {
                if(isShowToast)
                {
                    if(ProgressUtils.getInstance().isShowing())
                    ProgressUtils.getInstance().closeProgressDialog();
//                    Toast.makeText(context, context.getString(R.string.hard_version_already), Toast.LENGTH_SHORT).show();
                    if(context instanceof AboutActivity)
                    MyToastUitls.showToastInString(context, context.getString(R.string.hard_version_already), 1);
                    if(updateWindow != null && updateWindow.isShowing())
                    {
                        updateWindow.dismiss();
                    }
                }
            }
            else if(results != null && results.equals("-1"))
            {
                if(ProgressUtils.getInstance().isShowing())
                ProgressUtils.getInstance().closeProgressDialog();
//                    Toast.makeText(context, context.getString(R.string.hard_version_already), Toast.LENGTH_SHORT).show();
                if(context instanceof AboutActivity)
                MyToastUitls.showToastInString(context, context.getString(R.string.hard_version_already), 1);
                if(updateWindow != null && updateWindow.isShowing())
                {
                    updateWindow.dismiss();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<updateInfoEntity> updateList;
    private void parseTheJsonArray(String param)
    {
        String type, url, description, newVersion, oldVersion;
        int fileSize;
        updateList = new ArrayList<updateInfoEntity>();
        try {
            JSONArray jsonArray = new JSONArray(param);
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
                JSONObject obj = jsonArray.getJSONObject(0);
                type = obj.getString("type");
                url = obj.getString("url");
                description = obj.getString("description");
                newVersion = obj.getString("newVersion");
                oldVersion = obj.getString("oldVersion");
                fileSize = obj.getInt("fileSize");
                Log.i(TAG, "文件大小:" + fileSize);
                if(fileSize <= 0 )
                {
                    if(context instanceof DeviceAmdActivity)
                    MyToastUitls.showToast(context, R.string.hard_version_already, 1);
                    return;
                }
                updateList.add(new updateInfoEntity(type, url, description, newVersion, oldVersion, fileSize));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(((Activity)context).isDestroyed() || ((Activity)context).isFinishing())
        {
            return;
        }
        showTheDialog(updateList);
    }

//    AlertDialog.Builder updateDialog;
    private PopupWindow updateWindow;
    private RecyclerView recyclerView;
    private void showTheDialog(ArrayList<updateInfoEntity> updateList)
    {
        if(updateWindow != null && updateWindow.isShowing())
        {
            return;
        }
        if(BluetoothLeService.getInstance() != null && !BluetoothLeService.getInstance().isConnectedDevice())
        {
            return;
        }
            PowerManager powerManager = (PowerManager) context.getSystemService(Service.POWER_SERVICE);
            final PowerManager.WakeLock lock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "locked");
            lock.setReferenceCounted(false);
            View view = getUpdateView(updateList);
            updateWindow = new PopupWindow(context);
//          updateWindow = new PopupWindow(view,
//                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            updateWindow.setContentView(view);
            updateWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            updateWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            updateWindow.setTouchable(true);
            updateWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            updateWindow.setOutsideTouchable(false);
            updateWindow.setFocusable(true);
            updateWindow.setAnimationStyle(R.style.mypopupwindow_anim_style_center);
            updateWindow.setBackgroundDrawable(new BitmapDrawable());
            updateWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            lock.acquire();
            registBroactReceiver(context);
            updateWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss()
                {
                    context.unregisterReceiver(re);
//                WindowManager.LayoutParams params1 = ((Activity) context).getWindow().getAttributes();
//                params1.alpha = 1.0f;
//                ((Activity) context).getWindow().setAttributes(params1);
                    if(updateTask != null)
                    {
                        updateTask.setTaskCancel();
                    }
                    updateWindow = null;
                    lock.release();
                }
            });

//        WindowManager.LayoutParams params = ((Activity) context).getWindow().getAttributes();
//        params.alpha = 0.3f;
//        ((Activity)context).getWindow().setAttributes(params);
//        updateDialog = new AlertDialog.Builder(context);
//        updateDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
//        {
//            @Override
//            public void onDismiss(DialogInterface dialog)
//            {
//
//            }
//        });
//        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                switch (which)
//                {
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        // do nothing
//                        break;
//                }
//
//            }
//        };
//        updateDialog.setCancelable(false);
//        updateDialog.setTitle(context.getString(R.string.bracelet_can_update));
//        updateDialog.setView(view);
//        updateDialog.setNegativeButton(R.string.update_later, dialogListener);
//        updateDialog.create().show();
    }


    public interface OnRequesBack
    {
        void onDataBack();
    }

    private OnRequesBack onRequesBack;
    public void setOnRequstBack(OnRequesBack back)
    {
        this.onRequesBack = back;
    }


    private View getUpdateView(ArrayList<updateInfoEntity> updateList)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_for_hardversion_update_dialog, null);
//        view.setOnKeyListener(new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event)
//            {
//                if(keyCode == KeyEvent.KEYCODE_BACK)
//                {
//                    updateWindow.dismiss();
//                    updateWindow = null;
//                    return true;
//                }
//                return false;
//            }
//        });
        recyclerView = (RecyclerView)view.findViewById(R.id.list_for_update_dialog);
        adapterU = new updateAdapter(updateList);
        ViewGroup.LayoutParams parames = recyclerView.getLayoutParams();
        int dp = 160;
        parames.height = dpTopx(dp) * updateList.size();
        parames.width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.9);
        recyclerView.setLayoutParams(parames);
//        recyclerView.removeOnLayoutChangeListener();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapterU);
        return view;
    }


    // 下载固件
    class downloadTask extends AsyncTask<updateInfoEntity, Integer, Boolean>
    {
        updateInfoEntity updateEntity;

        @Override
        protected void onProgressUpdate(final Integer... values)
        {
            super.onProgressUpdate(values);
            ((Activity)context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    adapterU.addProgress(values[0]);
                    adapterU.notifyDataSetChanged();
                }
            });
            Log.i(TAG, "文件下载进度:onProgressUpdate" + values[0]);
        }
        @Override
        protected Boolean doInBackground(updateInfoEntity... params)
        {
            this.updateEntity = params[0];
            boolean result = false;
            if(updateWindow == null || !updateWindow.isShowing())
                return false;
            try {
                result = downTheFile(updateEntity.getUrl(), updateEntity.getType(), updateEntity.getFilesize());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                Log.i(TAG, "固件下载完成, 开始升级!");
                if(updateWindow == null || !updateWindow.isShowing())
                {
                    String content = context.getString(R.string.update_failed);
                    MyToastUitls.showToastInString(context, content, 1);
                    deleteFiles(FILE_OF_MCU);
                    deleteFiles(FILE_OF_BLUETOOTH);
                }
                else
                {
                    updateTask = new UpdateVersionTask(context, new UpdatePregress());
                    updateTask.execute(filePath);
                }
            }
            else
            {
                String content = context.getString(R.string.update_failed);
                MyToastUitls.showToastInString(context, content, 1);
            }
        }


        /**
         * 下载固件
         * @param url
         * @param type
         * @param fileSize
         * @return
         * @throws IOException
         */
        private boolean downTheFile(String url, String type, int fileSize) throws IOException
        {
            HttpURLConnection conn = null;
            try {
                URL u = new URL(url);
                conn = (HttpURLConnection)u.openConnection();
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(20 * 1000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = null;
            if(type != null && type.equals("mcu"))
            {
                fileName = FILE_OF_MCU;
            }
            if(type != null && type.equals("bluetooth"))
            {
                fileName = FILE_OF_BLUETOOTH;
            }
            File fileSave = createFile(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(fileSave);
            byte[] buffer = new byte[2 * 1024];
            InputStream in = conn.getInputStream();
            int len = -1;
            int count = 0;
            while ((len = in.read(buffer)) != -1)
            {
                fileOutputStream.write(buffer, 0, len);
                count += len;
                onProgressUpdate((int)(((float)count / fileSize) * 100));
                Log.i(TAG, "文件下载进度:" + count + "--" + fileSize + "--" + (int)(((float) count / fileSize) * 100));
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            in.close();
            conn.disconnect();
            return true;
        }


        private File createFile(String fileName) throws IOException
        {
            filePath = SDPathUtils.getSdcardPath()
                    + MyConfingInfo.SDCARD_DATA_ROOT_DIR_UPDATE
                    + File.separator + fileName;
            File file = new File(SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR_UPDATE);
            File file1 = new File(filePath);
            if(!file.exists())
            {
                file.mkdirs();
            }
            if(file1.exists())
            {
                file1.delete();
            }
            if(!file1.exists())
            {
                file1.createNewFile();
            }
            return file1;
        }
    }

    class UpdatePregress implements UpdateVersionTask.UpdateListener
    {
        @Override
        public void onProgress(int value)
        {
            final int v = value;
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterU.addAnotherProgress(v);
                    adapterU.notifyDataSetChanged();
                }
            });
        }


        @Override
        public void onUpdateSuccess()
        {
            MyToastUitls.showToastInString(context, context.getString(R.string.update_success), 1);
//            BluetoothLeService.getInstance().close(false);
            if(updateWindow != null && updateWindow.isShowing())
            {
                updateWindow.dismiss();
            }
            deleteFiles(FILE_OF_MCU);
            deleteFiles(FILE_OF_BLUETOOTH);
        }

        @Override
        public void onUpdateFailed()
        {
            String content = context.getString(R.string.update_failed);
            MyToastUitls.showToastInString(context, content, 1);
            if(updateWindow != null && updateWindow.isShowing())
            {
                updateWindow.dismiss();
            }
            deleteFiles(FILE_OF_MCU);
            deleteFiles(FILE_OF_BLUETOOTH);
            if(BluetoothLeService.getInstance() != null)
            {
                BluetoothLeService.getInstance().setBLENotify(null, true, true);
            }
        }
    }


    private void deleteFiles(String file_of_mcu)
    {
        String pa = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR_UPDATE + File.separator + file_of_mcu;
        File file = new File(pa);
        if(file.exists())
        {
            file.delete();
        }
    }


//    /**
//     * 升级
//     */
//    class updateVersionTask extends AsyncTask<updateInfoEntity, Integer, Boolean>
//    {
//        private updateInfoEntity upEntity;
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//            Intent intent = new Intent(MyConfingInfo.CLOSE_THE_REQUEST_FORM_DEVICE);
//            intent.putExtra("ok_or_no", true);
//            context.sendBroadcast(intent);
//        }
//
//
//        @Override
//        protected Boolean doInBackground(updateInfoEntity... params)
//        {
////            if(BluetoothLeService.getInstance() != null)
////            {
////               BluetoothLeService.getInstance().setBLENotify(null, true, false);
////            }
//            this.upEntity = params[0];
//            String updateType = upEntity.getType();
//            byte[] fileData = readTheFileFormSD();
//            Log.i(TAG, "开始升级,文件长度:" + fileData.length);
//            isUpdateing = true;
//            if(!updateNow(fileData))
//            {
//                isUpdateing = false;
//                return false;
//            }
//            return true;
//        }
//
//        private byte[] readTheFileFormSD()
//        {
//            File file = new File(filePath);
//            byte[] fileBytePool = new byte[2 * 1024 * 1024];
//            byte[] fileBytes = null;
//
//            FileInputStream inputStream = null;
//            byte[] tempbytes = new byte[500];
//            int byteRead = 0;
//            int len = 0;
//            try {
//                inputStream = new FileInputStream(file);
//                while ((byteRead = inputStream.read(tempbytes)) != -1)
//                {
//                    System.arraycopy(tempbytes, 0, fileBytePool, len, byteRead);
//                    len += byteRead;
//                }
//
//                fileBytes = new byte[len];
//                System.arraycopy(fileBytePool, 0, fileBytes, 0, len);
////                byte checkSun = IntegerUtil.getCheckSun(fileBytes);
////                fileBytes[len] = checkSun;
//
//                inputStream.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return fileBytes;
//        }
//
//
//
//        @Override
//        protected void onProgressUpdate(final Integer... values)
//        {
//            super.onProgressUpdate(values);
//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    adapterU.addAnotherProgress(values[0]);
//                    adapterU.notifyDataSetChanged();
//                }
//            });
//        }
//
//        synchronized private boolean updateNow(byte[] fileData)
//        {
//            BleDataForUpLoad ble = new BleDataForUpLoad();
//            ble.sendToDeviceToStartUpLoad(fileData.length);
//            Log.i(TAG, "升级数据比较:" + canContinue + "--" + (byte)0x00);
//            int startConut = 0;
//            while (!isOk && startConut < 20)
//            {
//                Log.i(TAG, "升级开始的消息" + isOk);
//                try {
//                    Thread.sleep(1000);
//                    startConut ++;
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//            if(!isOk)
//            {
//                ble.sendToDeviceToStartUpLoad(fileData.length);
//                startConut = 0;
//            }
//            while (!isOk && startConut < 10)
//            {
//                try {
//                    Thread.sleep(1000);
//                    startConut ++;
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//            if(!isOk)return false;
//            int times = 0;
//            Log.i(TAG, "升级开始的消息" + isOk);
//
//            boolean isR = false;
//            int countt = fileData.length / 200;  // 计算发送次数
//            int surplus = fileData.length % 200; // 剩余数据长度
//            int alreadySend = 0;
//            if(surplus > 0)
//            {
//                countt = countt + 1;
//            }
//            int j = 0;
//            int jCount = 0;
//            for(int i = 0; i < countt; i++)
//            {
//                if(j == i)
//                {
//                    jCount++;
//                    if(jCount >= 4)
//                    {
//                        return false;
//                    }
//                }
//                else
//                {
//                        jCount = 0;
//                        j = i;
//                }
//
//                if(isCancle)
//                {
//                    return false;
//                }
//                int lastValue = fileData.length - (i * 200);
//                byte[] big = new byte[(lastValue > 200) ? 200 : lastValue];
//                System.arraycopy(fileData, alreadySend, big, 0, big.length);
//                byte[] allData = sendDataBodyToDevice(big, countt, i + 1);
////                Log.i(TAG, "每个包的数据:第" + (i + 1) + "个包:" + FormatUtils.bytesToHexString(allData) + countt);
//                int c = 0;
//                int second = allData.length / 20;
//                second += (allData.length % 20) > 0 ? 1 : 0;
//                int alreadyLength = 0;
//                int k = 0;
//                int kCount = 0;
//                while (c < second)
//                {
//                    if(k == c)
//                    {
//                        kCount++;
//                        if(kCount >= 4)
//                        {
//                            return false;
//                        }
//                    }
//                    else
//                    {
//                        kCount = 0;
//                        k = c;
//                    }
//                    int seLastes = allData.length - alreadyLength;
//                    byte[] data = new byte[(seLastes > 20) ? 20 : seLastes];
//                    System.arraycopy(allData, alreadyLength, data, 0, data.length);
//                    isR = setDataToDevice(data);
//                    if(!isR)
//                    {
////                        c = -1;
////                        alreadyLength = 0;
//                        try {
//                            Thread.sleep(4);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        continue;
//                    }
//                    else
//                    {
//                        alreadyLength += data.length;
//                        c++;
//                    }
//                }
//                    Log.i(TAG, "升级校验:第几包递减调用前后aa:"  + i);
//                    if(!checkTheCallback(i + 1))
//                    {
//                        Log.i(TAG, "升级校验:第几包:"  + i);
//                        i--;
//                        Log.i(TAG, "升级校验:第几包递减后:"  + i);
//                        continue;
//                    }
//                alreadySend += big.length;
//                onProgressUpdate((int) (((float) alreadySend / fileData.length) * 100));
//            }
//
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            ble.overTheUpdate();
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            int timees = 0;
//            while (!isOverUpdate && timees < 10)
//            {
//                if(timees == 3)
//                {
//                    ble.overTheUpdate();
//                }
//
//                if(timees == 6)
//                {
//                    ble.overTheUpdate();
//                }
//                if(timees == 9)
//                {
//                    ble.overTheUpdate();
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                timees ++;
//            }
//            return isR;
//        }
//
//        private boolean setDataToDevice(byte[] data)
//        {
//            final boolean[] a = new boolean[1];
//            if(data != null)
//            {
//                    BluetoothLeService serviceMain = BluetoothLeService.getInstance();
//                    if(serviceMain == null)
//                    {
//                        Log.i(TAG, "writeDeylayValue  e1");
//                        return false;
//                    }
//                    // 拿到gatt
//                    BluetoothGatt gatt = serviceMain.getBluetoothGatt();
//                    if(gatt == null)
//                    {
//                        Log.i(TAG, "writeDelayValue   e2");
//                        return false;
//                    }
//                    BluetoothGattService service = gatt.getService(DeviceConfig.MAIN_SERVICE_UUID);
//                    if(service == null)
//                    {
//                        Log.i(TAG, "writeDelayValue   e3");
//                        return false;
//                    }
//                    BluetoothGattCharacteristic chara = service.getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_WR);
//                    BluetoothLeService.getInstance().writeDelayValue(data, chara, new BluetoothLeService.WriteCallBack()
//                    {
//                        @Override
//                        public void onWrite(boolean result)
//                        {
//                            a[0] = result;
////                            Log.i(TAG, "固件升级数据传输结果:" + result);
//                        }
//                    });
//            }
//            try {
//                Thread.sleep(4);
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            return a[0];
//        }
//        public byte[] sendDataBodyToDevice(byte[] big, int count, int each)
//        {
//            byte[] byteData = new byte[big.length + 5];
//            byteData[0] = (byte)0x01;
//            byteData[1] = (byte)(count & 0xff);
//            byteData[2] = (byte)(count >> 8 & 0xff);
//            byteData[3] = (byte)(each & 0xff);
//            byteData[4] = (byte)(each >> 8 & 0xff);
//            System.arraycopy(big, 0, byteData, 5, big.length);
//            return getSendByteArray(byteData);
//        }
//
//
//        byte msg_cmd = BleDataForUpLoad.toDevice;
//        byte msg_head = (byte)0x68;
//        byte msg_tail = 0x16;
//        /**
//         * 此方法将以上所有项，整合到一个byte数组，以待发送
//         * @return
//         */
//        public byte[] getSendByteArray(byte[] datas)
//        {
//            byte[] sendData = new byte[6 + datas.length];    // 创建一个长度为整条数据总长的byte数组
//            int count_for_check = 0;                            // 校验码的计数变量
//
//            for (int i = 0; i < sendData.length; i ++)
//            {
//                if(0 == i)      // 包头，数据的第一个字节
//                {
//                    sendData[i] = msg_head;
//                    count_for_check += sendData[i];
//                }
//                else if(1 == i) // 功能码，数据的第二个字节
//                {
//                    sendData[i] = msg_cmd;
//                    count_for_check += sendData[i];
//                }
//                else if(2 == i) // 数据长度的第一个字节，整条数据的第三个字节
//                {
//                    sendData[i] = (byte)(datas.length & 0xff);       // int为32位，取最后八位
//                    count_for_check += sendData[i];
//                }
//                else if(3 == i) // 数据长度的第二个字节
//                {
//                    sendData[i] = (byte)((datas.length >> 8) & 0xff);// int向右移动8位，取右边第二个八位
//                    count_for_check += sendData[i];
//                }
//                else if((i >= 4) && (i < (datas.length + 4)))        // 数据域,范围是从第5个字节开始，到数据长度加上前边的四个字节的位置
//                {
//                    sendData[i] = datas[i - 4];
//                    count_for_check += sendData[i];
//                }
//                else if(i == 4 + datas.length)                       // 校验码，整条数据的倒数第二个字节
//                {
//                    sendData[i] = (byte)(count_for_check % 256);        // 各个字节相加，对256取余
//                }
//                else if(i == 5 + datas.length)
//                {
//                    sendData[i] = msg_tail;
//                }
//
//            }
//
//
//            Log.i("", "整合后的数据getSendByteArray" + FormatUtils.bytesToHexString(sendData));
//            return sendData;
//        }
//
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean)
//        {
//            super.onPostExecute(aBoolean);
//            isUpdateing = false;
//            if(aBoolean)
//            {
////                Toast.makeText(context, upEntity.getType() + context.getString(R.string.update_success), Toast.LENGTH_SHORT).show();
//                MyToastUitls.showToastInString(context, upEntity.getType() + context.getString(R.string.update_success), 1);
//                deleteFiles(FILE_OF_MCU);
//                deleteFiles(FILE_OF_BLUETOOTH);
//                removeTheItem();
////                isUpdateing = false;
////                BluetoothLeService.getInstance().close(false);
////                connectTheSaveDevice();
//            }
//            else
//            {
//                String content = upEntity.getType() + context.getString(R.string.update_failed);
////                Toast.makeText(context, upEntity.getType() + context.getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
//                MyToastUitls.showToastInString(context, content, 1);
//                deleteFiles(FILE_OF_MCU);
//                deleteFiles(FILE_OF_BLUETOOTH);
//                if(BluetoothLeService.getInstance() != null)
//                {
//                    BluetoothLeService.getInstance().setBLENotify(null, true, true);
//                }
//            }
//
//            if(updateWindow != null && updateWindow.isShowing())
//            {
//                updateWindow.dismiss();
//            }
//
//            Intent intent = new Intent(MyConfingInfo.CLOSE_THE_REQUEST_FORM_DEVICE);
//            intent.putExtra("ok_or_no", false);
//            context.sendBroadcast(intent);
//
//
//        }
//
//
//        private void connectTheSaveDevice()
//        {
//            ArrayList<DeviceSaveData> allDevice = (ArrayList<DeviceSaveData>) LocalDataBaseUtils.getInstance(context).getDeviceListFromDB();
//            if(allDevice.size() != 0)
//            {
//                DeviceSaveData deviceSaveData = allDevice.get(allDevice.size() - 1);
//                LocalDeviceEntity entity = new LocalDeviceEntity(deviceSaveData.getDeviceName(), deviceSaveData.getDeviceAddress(), -70, new byte[10]);
//                BluetoothLeService.getInstance().connect(entity);
//            }
//
//        }
//
//        private void deleteFiles(String file_of_mcu)
//        {
//            String pa = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR_UPDATE + File.separator + file_of_mcu;
//            File file = new File(pa);
//            if(file.exists())
//            {
//                file.delete();
//            }
//        }
//
//    }

    private boolean checkTheCallback(int currentPackage)
    {
        int times = 0;
        while (totalPackege == 0 || eachPackege == 0)
        {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            times ++;
            if(times == 10)
            {
                break;
            }
        }

            int c = 0;
            while (eachPackege != currentPackage && c < 50)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                c ++;
                Log.i(TAG, "等待手环返回:eachPackege:" + eachPackege + "currentPackage:" + currentPackage + "-" + c);
            }

        return eachPackege == currentPackage;
    }


    class MyUpdateRecever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
//            if(intent.getAction().equals(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE))
//            {
//                String result = intent.getStringExtra(MyConfingInfo.INTENT_FOR_UPDATA_HARDWARE_RESULT);
//                byte[] dataResult = FormatUtils.hexString2ByteArray(result);
//                byte re = dataResult[0];
//                Log.i(TAG, "广播回传的数据:" + result + "--" + re);
////            广播回传的数据:012b0102002416--1
////            广播回传的数据:012b0104002616--1
////            广播回传的数据:012b0106002816--1
////                           68 88 0100 00 f116
////            byte[] totalLength = new byte[2];
////            System.arraycopy(dataResult, 0, totalLength, 1, 2);
//
//                if(re == 1)
//                {
//                    totalPackege = byteToint(dataResult, 1);
//                    eachPackege = byteToint(dataResult, 3);
//                    Log.i(TAG, "升级返回的数据:--总包数:" + totalPackege + "当前包:" + eachPackege);
//                }
//                else if(re == 0)
//                {
//                    isOk = true;
////                Log.i(TAG, "升级开始的消息recever" + isOk);
//                }
//                else if(re == 2)
//                {
//                    isOverUpdate = false;
////                Log.i(TAG, "升级结束的消息");
//                }
//            }
//            else
            if(intent.getAction().equals(MyConfingInfo.ON_DEVICE_STATE_CHANGE))
            {
                if(updateWindow != null && updateWindow.isShowing())
                {
                    updateWindow.dismiss();
                    updateWindow = null;
                }
            }

        }
    }

    private int byteToint(byte[] totalLength, int offset)
    {
        return (totalLength[offset++] & 0xff) | ((totalLength[offset] & 0xff) << 8);
    }

    private void removeTheItem()
    {
//        int items = adapterU.getItemCount();
//        if(items == 0)
//        {
//            return;
//        }
//        if(items == 1)
//        {
//            updateDialog.setTitle(R.string.update_success);
//            updateDialog.setNegativeButton(R.string.close_text, null);
//        }
//        if(items == 2)
//        {
//            updateList.remove(positionRemove);
//            adapterU.notifyDataSetChanged();
//            isUpdateing = false;
//            updateDialog.setTitle(R.string.update_success);
//            updateDialog.setNegativeButton(R.string.close_text, null);
//        }



    }

    private void listenerForUpdate(updateInfoEntity en)
    {
        new downloadTask().executeOnExecutor(MyApplication.threadService, en);
    }


    class updateAdapter extends RecyclerView.Adapter<updateAdapter.updateHolder>
    {
        private ArrayList<updateInfoEntity> data;
        private int progressData = 0;
        private int otherProgress = 0;
        public void addProgress(int p)
        {
            this.progressData = p;
        }

        public void addAnotherProgress(Integer value)
        {
            this.otherProgress = value;
        }

        public updateAdapter(ArrayList<updateInfoEntity> listData)
        {
            this.data = listData;
        }

        @Override
        public updateHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.item_for_hardversion_update_dialog, null);
            return new updateHolder(v);
        }

        @Override
        public void onBindViewHolder(final updateHolder holder, final int position)
        {
            final updateInfoEntity en = data.get(position);
            NoDoubleClickListener listener = new NoDoubleClickListener()
            {
                @Override
                public void onNoDoubleClick(View v)
                {
                    if (v == holder.updateNow)
                    {
                         if (holder.layoutOne.getVisibility() == View.VISIBLE)
                         {
                            holder.layoutOne.setVisibility(View.GONE);
                            holder.layoutTwo.setVisibility(View.VISIBLE);
                            Intent intent = new Intent();
                            intent.setAction(MyConfingInfo.BROADCAST_FOR_UPDATA_HARDWARE);
                            context.sendBroadcast(intent);
                            listenerForUpdate(en);
                            positionRemove = position;
                         }
                    }
                    else if(v == holder.beCancle)
                    {
                        if(updateWindow != null && updateWindow.isShowing())
                        {
                            updateWindow.dismiss();
                        }
                    }
                    else if(v == holder.cancleUpdate)
                    {
                        if(updateWindow != null && updateWindow.isShowing())
                        {
                            updateWindow.dismiss();
                            if(updateTask != null)
                            {
                                updateTask.setTaskCancel();
                                updateTask = null;
                            }
                        }
                    }
                }
            };

            String upType = en.getType() + context.getString(R.string.can_update);
            String upTypepro = en.getType() + context.getString(R.string.hardware_update);
            String upCurrent = context.getString(R.string.current_version) + en.getOldVersion();
            String upTypeNew = context.getString(R.string.new_version) + en.getNewVersion();
            holder.updateType.setText(upType);
            holder.updateProgressType.setText(upTypepro);
            holder.updateDes.setText(en.getDescription());
            holder.updateCurrent.setText(upCurrent);
            holder.updateNew.setText(upTypeNew);
            holder.updateNow.setOnClickListener(listener);
            holder.beCancle.setOnClickListener(listener);
            holder.cancleUpdate.setOnClickListener(listener);
            holder.progressBar.setSecondaryProgress(progressData);
            holder.progressBar.setProgress(otherProgress);
//            holder.progressUpdate.setText(otherProgress + "%");
            String showProgress;
            if(otherProgress == 0)
            {
                showProgress = progressData + "%";
            }
            else
            {
                showProgress = otherProgress + "%";
            }
            holder.progressUpdate.setText(showProgress);
        }
        @Override
        public int getItemCount()
        {
            return data.size();
        }

        class updateHolder extends RecyclerView.ViewHolder
        {
            TextView updateType, updateDes, updateCurrent,
                    updateNew, updateNow, progressUpdate, updateProgressType, beCancle, cancleUpdate;
            RelativeLayout layoutOne;
            RelativeLayout layoutTwo;
            ContentLoadingProgressBar progressBar;



            public updateHolder(View itemView)
            {
                super(itemView);
                updateType = (TextView)itemView.findViewById(R.id.update_type);
                updateDes = (TextView)itemView.findViewById(R.id.update_description);
                updateCurrent = (TextView)itemView.findViewById(R.id.update_current);
                updateNew = (TextView)itemView.findViewById(R.id.update_new);
                updateNow = (TextView)itemView.findViewById(R.id.update_now_item);
                layoutOne = (RelativeLayout)itemView.findViewById(R.id.layout_one);
                layoutTwo = (RelativeLayout)itemView.findViewById(R.id.layout_two);
                cancleUpdate = (TextView)itemView.findViewById(R.id.cancle_the_update);
                progressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.progress_bar_for_update);
//                progressBar.setProgress(30);
//                progressBar.setSecondaryProgress(60);
                progressUpdate = (TextView)itemView.findViewById(R.id.progress_for_update);
                updateProgressType = (TextView)itemView.findViewById(R.id.update_type_now);
                beCancle = (TextView)itemView.findViewById(R.id.update_later);
            }
        }
    }




    private int dpTopx(float dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }








}
