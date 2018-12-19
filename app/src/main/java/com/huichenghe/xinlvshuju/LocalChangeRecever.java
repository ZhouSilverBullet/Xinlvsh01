package com.huichenghe.xinlvshuju;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lixiaoning on 2016/8/4.
 */
public class LocalChangeRecever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case Intent.ACTION_LOCALE_CHANGED:
//                context.sendBroadcast(new Intent(MyConfingInfo.CLOSE_APP));
                break;
        }

    }
}
