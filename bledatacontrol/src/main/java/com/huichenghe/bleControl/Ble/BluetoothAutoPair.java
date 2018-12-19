
package com.huichenghe.bleControl.Ble;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class BluetoothAutoPair
{
    static public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception
    {
        if(Build.VERSION.SDK_INT > 18){
            return btDevice.createBond();
        }else{
            Method createBondMethod = btClass.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            Log.e("returnValue", "createBond:" + returnValue);
            return returnValue.booleanValue();
        }
    }
    
    public static int getAndroidOSVersion()
    {
         int osVersion = Build.VERSION.SDK_INT;
         
         return osVersion;
   }

    /**
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean removeBond(Class btClass, BluetoothDevice btDevice)
            throws Exception
    {
            Method removeBondMethod = btClass.getMethod("removeBond");
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
            Log.e("returnValue", "removeBond:" + returnValue);
            return returnValue.booleanValue();
    }

    static public boolean setPin(Class btClass, BluetoothDevice btDevice,
            String str) throws Exception
    {
            if(Build.VERSION.SDK_INT > 18){
                return btDevice.setPin(str.getBytes(Charset.forName("utf-8")));
            }else{
                Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                        new Class[]
                                {
                        byte[].class
                                });
                Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                        new Object[]
                                {
//                        str.getBytes()
                        str.getBytes(Charset.forName("utf-8"))
                                });
                Log.e("returnValue", "setPin:" + returnValue);
                return returnValue;
            }
    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class btClass,
            BluetoothDevice device)

            throws Exception
    {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        Log.e("returnValue", "cancelPairingUserInput:" + returnValue);
        return returnValue.booleanValue();
    }

    // 取消配对
    static public boolean cancelBondProcess(Class btClass,
            BluetoothDevice device)

            throws Exception
    {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        Log.e("returnValue", "cancelBondProcess:" + returnValue);
        return returnValue.booleanValue();
    }

    /**
     * @param clsShow
     */
    static public void printAllInform(Class clsShow)
    {
        try
        {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++)
            {
                Log.e("method name", hideMethod[i].getName() + ";and the i is:"
                        + i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++)
            {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (SecurityException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static public boolean pair(BluetoothDevice device, String strPsw)
    {
        boolean result = false;
        if (device.getBondState() != BluetoothDevice.BOND_BONDED)
        {
            try
            {
                Log.i("mylog", "NOT BOND_BONDED");
//                boolean r1 = ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
//                boolean r2 = ClsUtils.createBond(device.getClass(), device);
//                boolean r3 = ClsUtils.cancelPairingUserInput(device.getClass(), device);
                boolean r1 = BluetoothAutoPair.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
                boolean r2 = BluetoothAutoPair.createBond(device.getClass(), device);
                boolean r3 = BluetoothAutoPair.cancelPairingUserInput(device.getClass(), device);
                // remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
                return r1 & r2 & r3;
            } catch (Exception e)
            {
                // TODO Auto-generated catch block

                Log.e("mylog", "pair fail!", e);
            } //

        }
        else
        {
            Log.i("mylog", "HAS BOND_BONDED");
//            try
//            {
//                boolean r1 = ClsUtils.createBond(device.getClass(), device);
//                boolean r2 = ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
//                boolean r3 = ClsUtils.createBond(device.getClass(), device);
//                // remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
//                        return r1 & r2 & r3;
//            } catch (Exception e)
//            {
//                // TODO Auto-generated catch block
//                Log.e("mylog", "pair fail!", e);
//                e.printStackTrace();
//            }
            return true;
        }
        return result;
    }
}
