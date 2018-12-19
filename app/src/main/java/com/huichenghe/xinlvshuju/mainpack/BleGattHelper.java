package com.huichenghe.xinlvshuju.mainpack;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.huichenghe.bleControl.Ble.BleNotifyParse;
import com.huichenghe.bleControl.Ble.DeviceConfig;
import com.huichenghe.bleControl.Ble.IServiceCallback;
import com.huichenghe.bleControl.Ble.LocalDeviceEntity;
import com.huichenghe.bleControl.Utils.DateUtils;

import com.huichenghe.xinlvshuju.SDPathUtils;
import com.huichenghe.xinlvshuju.Utils.MyConfingInfo;
import com.huichenghe.xinlvshuju.http.UserAccountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by lixiaoning on 2016/8/19.
 */
public class BleGattHelper implements IServiceCallback
{
    private Context context;
    private BleGattHelperListener gattHelperListener;
    private static BleGattHelper bleGattHelper;
//    private BleGattHelperListener listener;
//    private ArrayList<BleGattHelperListener> gattHelper;

    /**
     * 获取ble帮助类
     * @param context
     * @param listener
     * @return
     */
    public static BleGattHelper getInstance(Context context, BleGattHelperListener listener)
    {
        if(bleGattHelper == null) {
            synchronized (BleGattHelper.class)
            {
                if(bleGattHelper == null)
                {
                    bleGattHelper = new BleGattHelper(context);
                }
            }
        }
        bleGattHelper.setBleGattListener(listener);
        return bleGattHelper;
    }


    private BleGattHelper(Context context)
    {
        this.context = context;
//        gattHelper = new ArrayList<>();
    }

    private void setBleGattListener(BleGattHelperListener listener)
    {
        this.gattHelperListener = listener;
    }

    @Override
    public void onBLEServiceFound(LocalDeviceEntity device,
                                   BluetoothGatt gatt,
                                   List<BluetoothGattService> list)
    {

    }
    @Override
    public void onBLEDeviceConnected(LocalDeviceEntity device,
                                      BluetoothGatt gatt)
    {

    }

    @Override
    public void onBLEDeviceDisConnected(LocalDeviceEntity device,
                                         BluetoothGatt gatt)
    {

    }

    @Override
    public void onCharacteristicRead(LocalDeviceEntity device,
                                      BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic,
                                      boolean success){}

    @Override
    public void onCharacteristicChanged(LocalDeviceEntity device,
                                         BluetoothGatt gatt,
                                         String uuid,
                                         byte[] value)
    {
        if(uuid.equals(DeviceConfig.HEARTRATE_FOR_TIRED_NOTIFY.toString()))
        {
            gattHelperListener.onDeviceStateChangeUI(device, gatt, uuid, value);
//					Log.i(TAG, "stepFragment对象：" + fragmentList.size());
        }
        else if(uuid.equals(DeviceConfig.UUID_CHARACTERISTIC_NOTIFY.toString()))
        {
//			Log.i(TAG, "上传的所有数据:" + FormatUtils.bytesToHexString(value));
//			Log.i(TAG, "添加到线程后返回的数据，进行解析：："
//					+ FormatUtils.bytesToHexString(value) + "++长度++" + value.length);
            BleNotifyParse.getInstance().doParse(context, value);
            saveToFile(value);  // 保存到sd卡
        }
    }

    @Override
    public void onCharacteristicWrite(LocalDeviceEntity device,
                                       BluetoothGatt gatt,
                                       BluetoothGattCharacteristic characteristic,
                                       boolean success)
    {

    }

    @Override
    public void onDescriptorRead(LocalDeviceEntity device,
                                  BluetoothGatt gatt,
                                  BluetoothGattDescriptor bd,
                                  boolean success)
    {

    }

    @Override
    public void onDescriptorWrite(LocalDeviceEntity device,
                                   BluetoothGatt gatt,
                                   BluetoothGattDescriptor bd,
                                   boolean success)
    {

    }

    @Override
    public void onNoBLEServiceFound() {

    }

    @Override
    public void onBLEDeviceConnecError(LocalDeviceEntity device,
                                        boolean showToast,
                                        boolean fromServer)
    {
        gattHelperListener.onDeviceConnectedChangeUI(device, showToast, fromServer);
    }

    @Override
    public void onReadRemoteRssi(LocalDeviceEntity device,
                                  BluetoothGatt gatt, int rssi,
                                  boolean success) {

    }

    @Override
    public void onReliableWriteCompleted(LocalDeviceEntity device,
                                          BluetoothGatt gatt,
                                          boolean success) {

    }

    @Override
    public void onMTUChange(BluetoothGatt gatt,
                             int mtu, int status) {

    }


    /**
     * 将app收的到的数据保存到sd卡
     * @param notifyData
     */
    private void saveToFile(byte[] notifyData)
    {
        FileOutputStream out;
        String filePath = SDPathUtils.getSdcardPath() + MyConfingInfo.SDCARD_DATA_ROOT_DIR;
        File fileP = new File(filePath);
//        Log.i(TAG, "当前文件保存大小" + fileP.length());
        if(!fileP.exists())fileP.mkdirs();
        File file = new File(filePath + File.separator + "liveData.txt");
        if(file.length() > 1024 * 1024)
        {
            boolean s = file.delete();
        }
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String useAccount = UserAccountUtil.getAccount(context);
        try {
            out = new FileOutputStream(file, true);     // 创建输出流对象，true代表追加
            String str = "手环数据："  + useAccount + " \t时间" + DateUtils.getNowTimeToMill() + "\t数据:"
                    + com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(notifyData) + "\r\n";
            out.write(str.getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
