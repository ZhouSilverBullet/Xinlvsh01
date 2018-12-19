package com.huichenghe.xinlvshuju.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public abstract class SuperAdapter extends BaseAdapter {

	private static final String TAG = "SuperAdapter";

	protected Context mContext;
	protected boolean mForceNewView = false;
	
	protected Handler mHandler;
	private ListView mMyListView;
	protected List<ItemData> mInfoList = new ArrayList<ItemData>(); 

	private int mAdapterMode = 0;
	private long mKeyAsFriendsIs;
	private int mAdapaterIndex = 0;
	

	public abstract class ViewHolder {
	}
	
	public abstract static class ItemData {
	}

	// private GetPhotoFromUrl mGetPhotoFromUrl = GetPhotoFromUrl.getInstance();

	public SuperAdapter(Context c, Handler h, ListView view) {
		super();
		mContext = c;
		mHandler = h;
		mMyListView = view; 
	}

	public SuperAdapter(Context c, Handler h, ListView view, int mode,
			long key) {
		super();
		mContext = c;
		mHandler = h;
		mMyListView = view; 
		mAdapterMode = mode;
		mKeyAsFriendsIs = key;
	}

	@Override
	public synchronized int getCount() {
		// Log.e(TAG,"shen, getCount()");

		if (mInfoList != null) {
			return mInfoList.size();
		}
		return 0;
	}

	public synchronized Object getItemObject(int position) {
		if (mInfoList != null && mInfoList.size() > position) {
			return mInfoList.get(position);
		}
		return null;
	}
	
	@Override
	public synchronized Object getItem(int position) {
		if (mInfoList != null && mInfoList.size() > position) {
			return mInfoList.get(position);
		}
		return null;
	}
	
	public synchronized void removeItem(int position) {
		if (mInfoList != null && mInfoList.size() > position) {
			mInfoList.remove(position);
		}
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	public synchronized void swapItem(int from, int to) {
		if (mInfoList != null) {
			ItemData fd = mInfoList.get(from);
			mInfoList.remove(from);
			mInfoList.add(from, mInfoList.get(to));
			mInfoList.remove(to);
			mInfoList.add(to, fd);
		}
	}
	
	public synchronized void moveItem(int from, int to) {
		if (mInfoList != null) {
			ItemData fd = mInfoList.get(from);
			mInfoList.remove(from);
			mInfoList.add(to, fd);
//			mInfoList.remove(to);
//			mInfoList.add(to, fd);
		}
	}

	public synchronized void addItem(ItemData data)
	{
		if (mInfoList != null)
		{
			mInfoList.add(data);
		}
	}
	public synchronized void addItem(int position, ItemData data)
	{
		if (mInfoList != null)
		{
			mInfoList.add(position, data);
		}
	}


	public synchronized void addItem(List<ItemData> datas)
	{
		if (mInfoList != null)
		{
			mInfoList.addAll(datas);
		}
	}

	public synchronized void addItem(int position,
			List<ItemData> datas)
	{
		if (mInfoList != null)
		{
			mInfoList.addAll(position, datas);
		}
	}

	public synchronized void clearAllData()
	{
		if (mInfoList != null)
		{
			mInfoList.clear();
		}
		mAdapaterIndex = 0;
	}

	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {

		if (getCount() == 0) {
			return null;
		}
		View v = convertView;
		ViewHolder holder = null;
		if (v == null || mForceNewView) {
			v = newListItemView(parent);
			holder = newViewHolder(v, position);
			v.setTag(holder);

		} else {
			holder = (ViewHolder) v.getTag();
		}
	//	if (holder.position != position) 
		{
			setListItemData(holder, position);
		}
		return v;
	}
	
	protected abstract ViewHolder newViewHolder(View v, int position);
	
	protected abstract View newListItemView(ViewGroup parent);
//	{
//		View v = LayoutInflater.from(mContext).inflate(
//		R.layout.p_friends_list_item, parent, false);
//
//		holder.leftLayout = Eq.find(v, R.id.left_item_layout);
//		holder.rightLayout = Eq.find(v, R.id.right_item_layout);
//		
//		holder.face = Eq.find(v, R.id.face);
//		holder.name = Eq.find(v, R.id.name);
//		holder.sex = Eq.find(v, R.id.sex);
//		holder.said = Eq.find(v, R.id.said);
//		
//		if (mAdapterMode == clock) {
//			holder.message_me = Eq.find(v, R.id.message_me);
//			holder.face_me = Eq.find(v, R.id.face_me);
//		}
//	}

	protected abstract void setListItemData(ViewHolder holder, int position);

	public void handleMessageToAdapter(Message msg) {

		int position = msg.what * (-1);
		int index = position / 1000000;
		position = position - index * 1000000 - 1;
		String tag = "" + index + "#" + position + "#";
	
		ImageView ivt = (ImageView) mMyListView.findViewWithTag(tag);
		if (ivt != null) {
			ivt.setImageDrawable((Drawable) msg.obj);
		}
	}

	public void addAdapaterIndex() {
		mAdapaterIndex++;
	}

	protected String getViewTag(int position) {
//		Log.e(TAG, "shen, Adpater::getViewTag() mAdapaterIndex="
//				+ mAdapaterIndex + "; position=" + position);
		return mAdapaterIndex + "#" + position + "#";
	}
	
	
	public String toString_name() {
		return null;
	}

	public String toString_value() {
		return null;
	}
	
	public void resetAllItem() {
	}
}
