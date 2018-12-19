package com.huichenghe.bleControl;

import java.util.Locale;

/**
 * Created by lixiaoning on 2016/12/23.
 */

public class CountryUtils
{

    public static boolean getMonthAndDayFormate()
    {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String contr = locale.getCountry();
        if(lang != null
                && (lang.equals("zh")
                || lang.equals("ja")
                || lang.equals("ko")
                || (lang.equals("en") && contr != null && contr.equals("US"))))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean getLanguageFormate()
    {
        String language = Locale.getDefault().getLanguage();
        if(language != null && language.equals("zh"))
        {
            return true;
        }
        return false;
    }
}
