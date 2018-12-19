package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * Created by lixiaoning on 15-10-30.
 */
public class BleScanUtils
{

    private static final String TAG = BleScanUtils.class.getSimpleName();


    public boolean isScanning = false;     // 标识正在扫描

    private int SHOUT_TIME_PRPEAT = 5000;
    private static BleScanUtils mBleScanUtils = null;
    private OnDeviceScanFoundListener mOnDeviceScanFoundListener;
    private Context mContext;
    private Handler mHandler;
    private BleScanUtils(Context mContext)
    {
        this.mContext = mContext;
        mHandler = new Handler(mContext.getMainLooper());
    }



    public static BleScanUtils getBleScanUtilsInstance(Context mContext)
    {

        if(mBleScanUtils == null)
        {
            mBleScanUtils = new BleScanUtils(mContext);
        }
        return mBleScanUtils;
    }

    ScanCallback scannerCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results)
        {
            addDevice(results);
        }

        @Override
        public void onScanFailed(int errorCode)
        {

        }
    };

    private void addDevice(List<ScanResult> results)
    {
//        ArrayList<String> catchs = new ArrayList<>();
        for (ScanResult scanResult : results)
        {
            BluetoothDevice de = scanResult.getDevice();
                String deName = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
                ScanRecord record = scanResult.getScanRecord();
//                SparseArray<byte[]> arr = record.getManufacturerSpecificData();
//                for (int i = 0; i < arr.size(); i++)
//                {
//                    int key = arr.keyAt(i);
//                    byte[] data = arr.get(key);
//                }
                byte[] re = getAndParseBytes(record.getBytes());
                if(deName == null || deName != null && deName.equals(""))
                {
//                    Log.i(TAG, "扫描到的设备名为空的：名字：" + de.getName() + "  --地址：" + de.getAddress() + " --");
                    deName = de.getAddress();
                }
//                ParcelUuid[] pu = de.getUuids();
//                for (int i = 0; i < pu.length; i++)
//                {
////                    ParcelUuid pd = pu[i];
////                    pd.describeContents()
//                }

                LocalDeviceEntity deviceEntity = new LocalDeviceEntity(deName, de.getAddress(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                mOnDeviceScanFoundListener.OnDeviceFound(deviceEntity);
        }
    }

    private byte[] getAndParseBytes(byte[] bytes)
    {
        byte[] dataB6 = new byte[2];
        byte[] dataB7 = new byte[6];
        for (int i = 0; i < bytes.length; i++)
        {
            if((bytes[i]  == (byte)0xff) && (bytes[i + 1] == (byte)0xb6))
            {
                System.arraycopy(bytes, i+2, dataB6, 0, 2);
                return dataB6;
            }
            if((bytes[i] == (byte)0xff) && (bytes[i + 1] == (byte)0xb7))
            {
                System.arraycopy(bytes, i + 2, dataB7, 0, 2);
            }
        }
        return dataB6;
    }



   public void scanDevice(final SwipeRefreshLayout refreshLayout)
    {
        mOnDeviceScanFoundListener.onScanStateChange(true);
        final BluetoothLeScannerCompat scnner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings scanSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(1000)
                        .setUseHardwareBatchingIfSupported(false).build();
        final ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(DeviceConfig.HEARTRATE_SERVICE_UUID)).build());
        scnner.startScan(scanFilters, scanSettings, scannerCallback);
        isScanning = true;
        mHandler.postDelayed(runnable, 10000);
    }


        Runnable runnable =  new Runnable()
        {
            @Override
            public void run()
            {
                if (isScanning)
                    stopScan();
            }
        };


    public void stopScan()
    {
        if(isScanning)
        {
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scannerCallback);
            isScanning = false;
            mOnDeviceScanFoundListener.onScanStateChange(false);
            mHandler.removeCallbacks(runnable);
        }
    }


    public void scanLeDeviceLoopBegin(boolean enableScan, final boolean isInnerRepeat,
                                      final SwipeRefreshLayout relayout)
    {

        if(enableScan)
        {
            if(!isScanning)
            {
                int LONG_TIME = 10000;
                mHandler.postDelayed(new Runnable()     // 10秒后停止扫描,或重新扫描
                {
                    @Override
                    public void run()
                    {
                        if(isScanning)
                        {
                            scanLeDeviceLoopBegin(false, isInnerRepeat, relayout);
                            if(isInnerRepeat)
                            {
                                scanLeDeviceLoopBegin(true, isInnerRepeat, relayout);
                            }
                        }
                    }
                }, isInnerRepeat ? SHOUT_TIME_PRPEAT : LONG_TIME);
            }
            isScanning = true;
            if(BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled())
            {

                BluetoothAdapter.getDefaultAdapter().startLeScan(myLeScanCallback);
//                ((Activity)mContext).runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//
//                    }
//                });

            }

        }
        else
        {
            if(isScanning)
            {
                isScanning = false;
                if(BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled())
                {
                       BluetoothAdapter.getDefaultAdapter().stopLeScan(myLeScanCallback);
//                        ((Activity)mContext).runOnUiThread(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                if(relayout != null && relayout.isRefreshing())
//                                {
//                                    relayout.setRefreshing(false);
//                                }
//                            }
//                        });
                }
            }
        }
    }


    BluetoothAdapter.LeScanCallback myLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        ArrayList<String> catchs = new ArrayList<>();
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
        {     System.out.println("huangqqqqqqqqqq"+com.huichenghe.bleControl.Utils.FormatUtils.bytesToHexString(scanRecord));

            if(device != null && mOnDeviceScanFoundListener != null)  // 若扫描到设备，构建实体类
            {

                if(!catchs.contains(device.getAddress()))
                {
                    catchs.add(device.getAddress());
                    String name = device.getName();
                    if(name != null && name.equals(""))
                    {
                        name = device.getAddress();
                    }
                    LocalDeviceEntity deviceEntity = new LocalDeviceEntity(name, device.getAddress(), rssi, scanRecord);
                    mOnDeviceScanFoundListener.OnDeviceFound(deviceEntity);
                }
                else
                {
                    return;
                }
            }
        }
    };

    public interface OnDeviceScanFoundListener
    {
       void OnDeviceFound(LocalDeviceEntity mLocalDeviceEntity);
       void onScanStateChange(boolean isChange);
    }


    public void setmOnDeviceScanFoundListener(OnDeviceScanFoundListener mOnDeviceScanFoundListener)
    {
        this.mOnDeviceScanFoundListener = mOnDeviceScanFoundListener;
    }


}
