package com.huichenghe.xinlvshuju.appRemind;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.huichenghe.xinlvshuju.DbEntities.MyDBHelperForDayData;
import com.huichenghe.xinlvshuju.R;
import com.huichenghe.xinlvshuju.Utils.MyToastUitls;
import com.huichenghe.xinlvshuju.appRemind.Utils.RunningAppInfoParam;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class AllAppActivity extends AppCompatActivity {
    private ListView listView;
    List<AppInfo> appInfos = new LinkedList<>();
    private MyAdapter adapter;
    private RunningAppInfoParam runningAppInfoParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Color1SwitchStyle);
        setContentView(R.layout.all_app_activity);
        addListener();

        listView = (ListView) findViewById(R.id.lv_listView);
        adapter = new MyAdapter(this, appInfos);
        listView.setAdapter(adapter);
        setResult(RESULT_OK);
    }

    private void addListener() {
        findViewById(R.id.care_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appInfos.size() == 0)
            initdATA();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        for (int i = 0; i < appInfos.size(); i++) {
//            if (appInfos.get(i).isCheck()) {
//                appInfosChecked.add(appInfos.get(i));
//            }
//        }
    }

    public ProgressDialog dialog;

    public void showDialog(Context context) {
        if (dialog == null)
            dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getResources().getString(R.string.loading_now));
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog() {

        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appInfos.clear();
        appInfos = null;
    }

    private void initdATA() {
        showDialog(this);
        runningAppInfoParam = new RunningAppInfoParam(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos.addAll(runningAppInfoParam.getThirdAppInfo());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        //对集合进行排序
                        if(appInfos == null || appInfos != null && appInfos.size() <= 0)
                        {
                            return;
                        }
                        Collections.sort(appInfos, new Comparator<AppInfo>() {
                            @Override
                            public int compare(AppInfo lhs, AppInfo rhs) {
                                int i, j;
                                if (lhs.isCheck()) {
                                    i = 1;
                                } else {
                                    i = -1;
                                }
                                if (rhs.isCheck()) {
                                    j = 1;
                                } else {
                                    j = -1;
                                }
                                return j - i;
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    class MyAdapter extends BaseAdapter {
        public MyAdapter(Context context, List<AppInfo> tList) {
            this.context = context;
            this.tList = tList;
        }

        Context context;
        List<AppInfo> tList;

        @Override
        public int getCount() {
            return tList.size();
        }

        @Override
        public Object getItem(int position) {
            return tList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_allapp, null);
                holder = new Holder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_label);
                holder.aSwitch = (Switch) convertView.findViewById(R.id.st_switch);
                convertView.setTag(holder);


            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.imageView.setImageDrawable(tList.get(position).getDrawable());
            holder.textView.setText(tList.get(position).getLabel());
            holder.aSwitch.setChecked(tList.get(position).isCheck());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inserName = tList.get(position).getPackageName();
                    if (holder.aSwitch.isChecked())
                    {
                        holder.aSwitch.setChecked(false);
                        tList.get(position).setCheck(false);
//                        Utils.saveData(AllAppActivity.this, false, tList.get(position).getPackageName());
                        Cursor cursor = MyDBHelperForDayData.getInstance(context).selectInfoData(getApplicationContext());
                        if(cursor == null)
                        {
//                            MyDBHelperForDayData.getInstance(context).insertInfoData(inserName);
                        }
                        else
                        {
                            if(!parseCursorAndCompare(cursor, inserName))
                            {
                                MyDBHelperForDayData.getInstance(context).deleteInfoData(inserName);
                            }
                        }
                    } else {
                        holder.aSwitch.setChecked(true);
                        tList.get(position).setCheck(true);
//                        Utils.saveData(AllAppActivity.this, true, tList.get(position).getPackageName());
                        Cursor cursor = MyDBHelperForDayData.getInstance(context).selectInfoData(getApplicationContext());
                        if(cursor == null)
                        {
                            MyDBHelperForDayData.getInstance(context).insertInfoData(inserName);
                        }
                        else
                        {
                            if(cursor.getCount() > 9)
                            {
                                MyToastUitls.showToast(context, R.string.add_app_upper_limit, 1);
                                holder.aSwitch.setChecked(false);
                                tList.get(position).setCheck(false);
                            }
                            else
                            {
                                if(parseCursorAndCompare(cursor, inserName))
                                {
                                    MyDBHelperForDayData.getInstance(context).insertInfoData(inserName);
                                }
                            }
                        }
                    }
//                    Toast.makeText(AllAppActivity.this, position + "", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        private boolean parseCursorAndCompare(Cursor cursor, String com)
        {
            if(com != null && com.equals(""))
            {
                return false;
            }
            if(cursor.getCount() > 0)
            {
                if(cursor.moveToFirst())
                {
                    do {
                        String appName = cursor.getString(cursor.getColumnIndex("appName"));
                        if(com.equals(appName))
                        {
                            return false;
                        }
                    }
                    while (cursor.moveToNext());
                }
            }
            return true;
        }

        class Holder {
            TextView textView;
            ImageView imageView;
            Switch aSwitch;
        }
    }
}
