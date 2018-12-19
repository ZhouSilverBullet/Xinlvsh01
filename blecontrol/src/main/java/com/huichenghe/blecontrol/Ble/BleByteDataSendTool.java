package com.huichenghe.blecontrol.Ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.huichenghe.blecontrol.Utils.FormatUtils;

import java.util.ArrayList;

/**
 * 发送数据工具类，此类为单例模式
 * Created by lixiaoning on 15-11-sosPhone.
 */
public class BleByteDataSendTool
{
    private final static String TAG = "BleByteDataSendTool";
    private static BleByteDataSendTool mBleByteDataSendTool;

    public final static int TASK_WAITING = 0;       // 代表task在等待
    public final static int TASK_SENDING = 1;       // 正在发送
    public final static int TASK_SENDED_CONFORM = 2;// 按顺序发送


    private final ArrayList<stTask> mTaskList = new ArrayList<stTask>();    //任务类集合
    private final ArrayList<stTask> mConformList = new ArrayList<stTask>(); //任务类集合

    
    private BleByteDataSendTool()
    {
        // 创建线程并启动
        new BeginSendTaskThread().start();
        new BeginTaskWatchThread().start();
    }

    /**
     * 获取此类实例
     * @return 此类对象
     */
    public static BleByteDataSendTool getInstance()
    {
        if(mBleByteDataSendTool == null)
        {
            mBleByteDataSendTool = new BleByteDataSendTool();
        }
        return mBleByteDataSendTool;
    }


    /**
     * 此方法判断data的字节长度，并且将包含此data的Task对象添加到集合中
     * @param value
     */
    public void judgeTheDataLengthAndAddToSendByteArray(byte[] value)
    {
        int count = (value.length + 19) / 20;       // +19 的目的是保证，数据不足20的情况下count为1
        if(count == 1)                              // == 1代表value的长度小于 20
        {
            synchronized (mTaskList)                // Task对象为锁
            {
                mTaskList.add(new stTask(value));   // 将Task对象添加到list集合中
                mTaskList.notifyAll();              // 唤醒等待的线程
            }
        }
        else                                        // 若大于20， count有可能为2,info,sitting....
        {
            int alreadyUsed = 0;                    // 计数变量，表示已发出的数据
            for(int i = 0; i < count; i ++)         // 遍历数据，count个数就是发送的次数
            {
                int lastValue = value.length - alreadyUsed;         // 剩余的数据
                int length = (lastValue > 20) ? 20 : lastValue;     // 剩余的数据大于20，则长度定为20，小于等于20，则长度为本身的长度
                byte[] bytes = new byte[length];    // 创建一个byte数组，长度为发送数据的长度
                for(int j = 0; j < length; j ++)
                {
                    bytes[j] = value[alreadyUsed + j];
                }
                alreadyUsed += length;
                synchronized (mTaskList)
                {
                    mTaskList.add(new stTask(bytes));
                    mTaskList.notifyAll();
                }
            }
        }
        Log.i(TAG, "添加到线程成功 --- ");
    }


    /**
     * 执行发送数据的线程类
     */
    public class BeginSendTaskThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            try {
                while (true)        // 死循环
                {
                    synchronized (mTaskList)
                    {
                        if(mTaskList.size() == 0)   // 没有数据
                        {
                            Log.i(TAG, "BeginSendTaskThread wait 线程等待中...");
                            mTaskList.wait();
                        }
                        if(mTaskList.size() > 0)    // 有数据
                        {
                            stTask task = mTaskList.get(0);       // 取出第一个task对象
                            if(TASK_WAITING == task.status)
                            {
                                task.status = TASK_SENDING;      // task状态标识为sending
                            }
                            boolean isSendOk = writeDelayValue(task);                 // 发送数据
                            synchronized (mConformList)         // 将此任务对象添加进ConformLIst集合中
                            {
                                mConformList.add(task);
                                mConformList.notifyAll();
                            }
                            if(isSendOk)
                            {
                                mTaskList.remove(0);
                            }
                            else
                            {
                                sleep(20);
                                writeDelayValue(task);
                                mTaskList.remove(0);
                            }
                        }
                    }
                sleep(20);
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean writeDelayValue(final stTask task)
    {
        final boolean[] booleenR = new boolean[1];
        BluetoothLeService serviceMain = BluetoothLeService.getInstance();
        if(serviceMain == null)
        {
            Log.i(TAG, "writeDeylayValue  e1");
            return false;
        }
        // 拿到gatt
        BluetoothGatt gatt = serviceMain.getBluetoothGatt();
        if(gatt == null)
        {
            Log.i(TAG, "writeDelayValue   e2");
            return false;
        }
        BluetoothGattService service = gatt.getService(DeviceConfig.MAIN_SERVICE_UUID);
        if(service == null)
        {
            Log.i(TAG, "writeDelayValue   e3");
            return false;
        }
        BluetoothGattCharacteristic chara = service.getCharacteristic(DeviceConfig.UUID_CHARACTERISTIC_WR);
        BluetoothLeService.getInstance().writeDelayValue(task.value, chara, new BluetoothLeService.WriteCallBack() {
            @Override
            public void onWrite(boolean result) {
                task.status = TASK_SENDED_CONFORM;
                booleenR[0] = result;
            }
        });
        try {
            Thread.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return booleenR[0];
    }


    /**
     * 任务类
     */
    public class stTask
    {
        byte[] value;
        int status;

        public stTask(byte[] mValue)
        {
            value = mValue;
        }
    }


    /**
     * 监听task线程类
     */
    public class BeginTaskWatchThread extends Thread
    {
        @Override
        public void run()
        {
            super.run();
            try
            {
            while (true)
            {
                synchronized (mConformList)
                {
                    if(mConformList.size() == 0)
                    {
                        Log.i(TAG, "BeginTaskWatch---wait");
                        mConformList.wait();
                    }
                    Log.i(TAG, "beginTaskWatch--mConformList.size() = " + mConformList.size());
                }
                sleep(10000);
                synchronized (mConformList)
                {
                    for (int i = 0; i < mConformList.size(); i++)
                    {
                        stTask task = mConformList.get(i);
                        // 没有发送出去，回调函数没有被调用，打印出来
                        if(TASK_SENDING == task.status)
                        {
                            Log.i(TAG, "error_60s task_sending" + FormatUtils.bytesToHexString(task.value));
                            task.status ++;
                        }
                        else    // 发送成功，移除task对象
                        {
                            mConformList.remove(i);
                        }
                    }
                }
            }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
