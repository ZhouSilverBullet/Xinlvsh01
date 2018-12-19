package com.huichenghe.blecontrol.Ble;

import android.text.TextUtils;

import com.huichenghe.xinlvshuju.Utils.FormatUtils;

import java.util.UUID;

public class DeviceConfig {
	//WRITE
    public final static String DEVICE_NAME = "device name";
    public final static String DEVICE_ADDRESS = "device address";
    public final static String DEVICE_CONNECTE_AND_NOTIFY_SUCESSFUL = "device_connecte_and_notify_ok";
    public final static String[] DEVICE_FILTER_NAME = {
        "HR", "FishAlert"
    };
	public static final UUID MAIN_SERVICE_UUID = UUID.fromString(FormatUtils.convert16to128UUID("FFF0"));
	public static final UUID UUID_CHARACTERISTIC_WR = UUID.fromString(FormatUtils.convert16to128UUID("FFF2"));
	public static final UUID UUID_CHARACTERISTIC_NOTIFY = UUID.fromString(FormatUtils.convert16to128UUID("FFF1"));
	
//	public static final UUID OAD_SERVICE_UUID  = UUID.fromString(FormatUtils.convert16to128UUID("FEF5"));
//	public static final UUID OAD_Charatic_UUID1 = UUID.fromString("8082CAA8-41A6-4021-91C6-56F9B954CC34");
//	public static final UUID OAD_Charatic_UUID2 = UUID.fromString("724249F0-5EC3-4B5F-8804-42345AF08651");
//	public static final UUID OAD_Charatic_UUID3 = UUID.fromString("6C53DB25-47A1-45FE-A022-7C92FB334FD4");
//	public static final UUID OAD_Charatic_UUID4 = UUID.fromString("9D84B9A3-000C-49D8-9183-855B673FDA31");
//	public static final UUID OAD_Charatic_UUID5 = UUID.fromString("457871E8-D516-4CA1-9116-57D0B17B9CB2");
//	public static final UUID OAD_Charatic_UUID6 = UUID.fromString("5F78DF94-798C-46F5-990A-B3EB6A065C88");

    
    public static boolean isDeviceNameOk(String devicename) {
        if (TextUtils.isEmpty(devicename)) {
            return false;
        }
    
//        for (int i = 0; i < DEVICE_FILTER_NAME.length; i++) {
//            String str = DEVICE_FILTER_NAME[i];
//            if (devicename.toLowerCase().startsWith(str.toLowerCase())) {
//                return true;
//            }
//        }
        return true;
    }
    
    public static final UUID HEARTRATE_SERVICE_UUID = UUID.fromString(FormatUtils.convert16to128UUID("180D"));
    public static final UUID HEARTRATE_FOR_TIRED_NOTIFY = UUID.fromString(FormatUtils.convert16to128UUID("2A37"));
}
