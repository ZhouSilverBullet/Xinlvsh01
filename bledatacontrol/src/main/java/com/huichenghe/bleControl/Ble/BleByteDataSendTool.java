package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.huichenghe.bleControl.Utils.FormatUtils;

import java.util.ArrayList;

public class BleByteDataSendTool
{
    private final static String TAG = "BleByteDataSendTool";
    private static BleByteDataSendTool mBleByteDataSendTool;

    public final static int TASK_WAITING = 0;
    public final static int TASK_SENDING = 1;
    public final static int TASK_SENDED_CONFORM = 2;


    private final ArrayList<stTask> mTaskList = new ArrayList<stTask>();
    private final ArrayList<stTask> mConformList = new ArrayList<stTask>();

    
    private BleByteDataSendTool()
    {
        new BeginSendTaskThread().start();
        new BeginTaskWatchThread().start();
    }

    public static BleByteDataSendTool getInstance()
    {
        if(mBleByteDataSendTool == null)
        {
            mBleByteDataSendTool = new BleByteDataSendTool();
        }
        return mBleByteDataSendTool;
    }


    public void judgeTheDataLengthAndAddToSendByteArray(byte[] value)
    {
        int count = (value.length + 19) / 20;
        if(count == 1)
        {
            synchronized (mTaskList)
            {
                mTaskList.add(new stTask(value));
                mTaskList.notifyAll();
            }
        }
        else
        {
            int alreadyUsed = 0;
            for(int i = 0; i < count; i ++)
            {
                int lastValue = value.length - alreadyUsed;
                int length = (lastValue > 20) ? 20 : lastValue;
                byte[] bytes = new byte[length];
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
    }


    public class BeginSendTaskThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            try {
                while (true)
                {
                    synchronized (mTaskList)
                    {
                        if(mTaskList.size() == 0)
                        {
                            mTaskList.wait();
                        }
                        if(mTaskList.size() > 0)
                        {
                            stTask task = mTaskList.get(0);
                            if(TASK_WAITING == task.status)
                            {
                                task.status = TASK_SENDING;
                            }
                            boolean isSendOk = writeDelayValue(task);
                            synchronized (mConformList)
                            {
                                mConformList.add(task);
                                mConformList.notifyAll();
                            }
                            mTaskList.remove(0);
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
            return false;
        }
        BluetoothGatt gatt = serviceMain.getBluetoothGatt();
        if(gatt == null)
        {
            return false;
        }
        BluetoothGattService service = gatt.getService(DeviceConfig.MAIN_SERVICE_UUID);
        if(service == null)
        {
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


    public class stTask
    {
        byte[] value;
        int status;

        public stTask(byte[] mValue)
        {
            value = mValue;
        }
    }


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
                        mConformList.wait();
                    }
                }
                sleep(10000);
                synchronized (mConformList)
                {
                    for (int i = 0; i < mConformList.size(); i++)
                    {
                        stTask task = mConformList.get(i);
                        if(TASK_SENDING == task.status)
                        {
                            task.status ++;
                        }
                        else
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
