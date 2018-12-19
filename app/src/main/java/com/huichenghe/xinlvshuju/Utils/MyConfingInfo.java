package com.huichenghe.xinlvshuju.Utils;

import java.io.File;

/**
 * Created by lixiaoning on 15-11-sitting.
 */
public class MyConfingInfo {

    public static final String TAG = MyConfingInfo.class.getSimpleName();
    public static final String QQ_TYPE = "2";
    public static final String WEICHART_TYPE = "1";
    public static final String FACEBOOK_TYPE = "3";
    public static final String NOMAL_TYPE = "4";
    public static final String DIRCET_TYPE = "5";
    public static String CURRENT_DATE = "current date";
    public static String THIRD_LOGIN = "third_login";
    public static final String ACCOUNT = "account_login_flag";
    public static final String TYPE = "type_login_flag";
    public static final String DELETE_ALL_DATABASE = "delete_all_database";
//    public static String pathRoot = null;
//    static
//    {
//        pathRoot = new SDPathUtils().getSdcardPath();
//    }

    public static final String DATABASE_PATH = File.separator + "Xlshdatabase";
    public static final String SDCARD_DATA_ROOT_DIR = File.separator + ".DataXinlvshouhuan";
    public static final String SDCARD_DATA_ROOT_DIR_UPDATE =
            File.separator + ".DataXinlvshouhuan" + File.separator + "updateFile";
    public static final String CAMERA_PATH = File.separator + "DCIM" + File.separator + "Camera" + File.separator;
    public static final String IMAGEVIEW_SAVE_PATH = SDCARD_DATA_ROOT_DIR + File.separator + "tmp.jpeg";
    public static final String ON_DEVICE_STATE_CHANGE = "onDeviceStateChange";
    public static final String HARD_VERSION = "hardVersion";
    public static final String PICTURE_NAME = "picture name";
    public static final int CONNECTED = 1;
    public static final int DISCONNECTE = 2;
    public static final String DETAIL_ACTIVITY = "detail activity";
    public static final int FROM_THE_WELOCMEACTIVITY = 3;
    public static final int FROM_THE_MAINACTIVITY = 4;

    public static final String WHERE_ARE_YOU_FROM = "where are you from";

    public static final String weatherRoot = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=";
    //真实服务器复制
    public static final String WebRoot = "http://bracelet.cositea.com:8089/bracelet/";
    public static final String updateTextUrl = "http://bracelet.cositea.com:8089/bracelet/";
    //测试服务器地址
//    public static final String WebRoot = "http://bracelet.cositea.com:8888/bracelet/";
//    public static final String updateTextUrl = "http://bracelet.cositea.com:8888/bracelet/";
    public static final String newWebRoot = WebRoot + "v3";
    public static final String WebRegist = WebRoot + "user_register";
    public static final String WebRequeIcon = WebRoot + "download_userHeader";
    public static final String WebRequeModify = WebRoot + "user_updateInfor";
    public static final String WebAttion = WebRoot + "attention_myAttention";
    public static final String PERSION_INFO_AND_ACOUNT_INFO = "personinfo and accountinfo";
    public static final String WHERE_ARE_YOU_FORM = "where are you from";
    public static final String FORM_REGIST = "from regist";
    public static final String FORM_DIRECTLY_USE = "from directly use";
    public static final String FORM_VIEW_DATA = "from view data";
    public static final String FORM_THIRD_LOGIN = "from third login";
    public static final String EDIT_PERSION_INFO = "bianjigerenziliao";
    public static final String REGIST = "regist";
    public static final String EDIT_PERSION_FIRST = "edit persion first";
    public static final String EDIT_PERSION = "edit persion";
    public static final String SHOW_THE_PERSION_INFO = "xinshigerenxinxi";
    public static final String USER_ACCOUNT = "user acount";
    public static final String USER_PASSWORD = "user password";
    public static final String USER_NICK = "user nick";
    public static final String USER_GENDER = "user gender";
    public static final String USER_EMAIL = "user email";
    public static final String USER_BIRTHDAY = "user birthday";
    public static final String USER_HEIGHT = "user height";
    public static final String USER_WEIGHT = "user weight";

    public static final String COOKIE_FOR_ME = "cookies_for_login";

    public static final String PERSION_HAS_CHANGER = "persion info has change";
    public static final String NOTIFY_MAINACTIVITY_TO_UPDATE_DAY_DATA = "notify the main to update the screen";
    public static final String NOTIFY_MAINACTIVITY_TO_UPDATE_OUTLINE_DATA = "notify main activty update the screen";


    public static final String TARGET_SETTING_VALUE = "target setting value";
    public static final String TARGET_SETTING_VALUE_SLEEP = "target setting value sleep";

    public static final String PHONE_REMIND_CHANGE = "phone remind change";
    public static final String SMS_REMIND_CHANGE = "info remind change";
    public static final String REMIND_OPEN = "remind open";
    public static final String REMIND_CLOSE = "remind close";
    public static final String SP_QQ_REMIND = "sp qq remind";
    public static final String SP_WEICHART_REMIND = "sp weichart remind";
    public static final String SP_FACEBOOK_REMIND = "sp facebook remind";
    public static final String SP_WHATSAPP_REMIND = "sp whatsapp remind";
    public static final String SP_TWITTER_REMIND = "sp twitter remind";
    public static final String SP_SKYPE_REMIND = "sp skype remind";
    public static final String HR_WARNING_SWITCH = "hr warning switch";
    public static final String HR_FREQENCY = "hr freqency";
    public static final String LOST_WARNING = "lost warning";

    public static final String CUSTOM_REMIND_VALID = "custom remind valid";
    public static final String CUSTOM_REMIND_VALID_DATA = "custom remind valid data";

    public static final String SEND_TO_THE_ACTIVITY = "send to the activity";

    public static final String webHelpCn = "http://bracelet.cositea.com:8089/bracelet/v3/cn/phone/HR64/";
    public static final String webHelpEn = "http://bracelet.cositea.com:8089/bracelet/v3/en/phone/HR64/";

    public static final String webHelpCnNotConnect = "http://bracelet.cositea.com:8089/bracelet/v3/cn/phone";
    public static final String webHelpEnNotConnect = "http://bracelet.cositea.com:8089/bracelet/v3/en/phone";

    public static final String webBackLinkCN = "http://bracelet.cositea.com:8089/bracelet/v3/cn/phone/HR64/#anchor_item";
    public static final String webBackLinkEN = "http://bracelet.cositea.com:8089/bracelet/v3/en/phone/HR64/#anchor_item";

    public static final String HRWARNING_SETTING_VALUE = "hr warning setting value";

    public static final String ACTION_FOR_HR_WARNING = "action for hr warning";

    public static final String MAX_HR = "max hr";
    public static final String MIN_HR = "min hr";
    public static final String HR_WARNING_OPEN_OR_NO = "hr open or no";
    public static final String HASLOGIN = "has login";
    public static final String DELAY_DATA = "dalay data";
    public static final String DELAY_TYPE = "dalay type";
    public static final String BROADCAST_DELAY_DATA = "broadcast dalay data";

    public static final String FACTORY_RESET_SUCCESS = "factory reset success";

    public static final String METRICORINCH = "metric or inch";
    public static final String METRIC = "metric";
    public static final String INCH = "inch";
    public static final String BROADCAST_CHAGE_UNIT = "broadcast change unit";

    public static final String BROADCAST_FOR_UPDATA_HARDWARE = "broadcast_for_update_hardware";
    public static final String INTENT_FOR_UPDATA_HARDWARE_RESULT = "intent for update hardware result";

    public static final String DETAIL_REMIND = "show the entity content";
    public static final String HOT_START = "hot start from activity";
    public static final String HOT_START_FROM_MAIN = "hot start from main";
    public static final String CLOSE_THE_REQUEST_FORM_DEVICE = "close_the_request_form_device";
    public static final String UPDATE_THE_MAIN_OUTLINE_TYPE = "update the main outline type";

    public static final String DISABLE_DATA = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    public static final String CLOSE_OTHER_PAGER = "close other pager";
    public static final String CLOSE_APP = "close_app";
    public static final String QQ_THIRD_LOGIN = "qq third login";
    public static final String UPDATE_HR_SHOW = "update_mainactivity_hr_show";
    public static final String HR_DATA = "hr_data_live";

    public static final String RECEVER_NOTIFITION_BRACOST = "recever_notification_from_listenerservice";
    public static final String RECEVER_NOTIFITION_SHOW = "recever_notification_show";
    public static final String RECEVER_NEED_CONNECT_SAVE_DEVICE = "recever_need_connect_save_device";

    public static String USER_DEVICE_TYPE = "user_device_type_choosed";
    public static String DEVICE_HR = "device_hr_brachlet";
    public static String DEVICE_BLOOD = "device_blood_brachlet";
    public static String ACTION_SEARCH_DEVICE = "search_device_pager";
    public final static String CLOSE_DEVICE_AMD_ACTIVITY = "close_device_amd_activity";
    public final static String CHAGE_DEVICE_TYPE = "change_device_type";

    public static String USERCITY = "citey";
    public static String IS_CHINA = "ischina";
    public static String FORCAST = "forcastfor3";
    public static String NOWTEMP = "nowtemp";
    public static String AQI = "aqi";
    public static String TIME = "TIME";


    public static final String PUSH_MESSAGE = "push_message_to_device";
    public static final String PUSH_MESSAGE_TRUE = "push_message_to_device_true";
    public static final String PUSH_MESSAGE_FALSE = "push_message_to_device_false";

    public static String DEVICE_CHANGE_UI = "device_change_ui";
    public static String SUPPORT_BLOOD = "is_support_xueya";
    public static String DEVICE_SHOW_UI = "device_show_ui";
    public static String DEVICE_HIDE_UI = "device_hide_ui";
    public static String DEVICE_INFO_NORMAL = "device_info_nomal";
    public static String DEVICE_INFO_TYPE = "device_info_type";
    public static String DEVICE_INFO_NEW = "device_info_new";
    public static String DISCONNECT_STATE = "disconnect_state";
    public static String ACTIVE_DISCONNECT = "active_disconnect";
    public static String PASSIVE_DISCONNECT = "passive_disconnect";
    public static String CHECK_SUPPORT_HEAETS = "check_support_heart_su";
    public static String SUPPORT_TRUE = "support_true";
    public static String SUPPORT_FALSE = "support_false";
    public static String CHECK_SUPPORT_NEW_CLOCK = "check_support_new_clock";
    public static String CHECK_SUPPORT_WEATHER_INTERFACE = "check_support_weather_interface";
    public static String CHECK_SUPPORT_MSG_MAX_LENGTH = "check_support_msg_max_length";
    public static String CHECK_SUPPORT_CUSTOM_REMIND_MAX_LENGTH = "check_support_custom_max_length";
}
