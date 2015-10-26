package com.xgf.inspection.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgf.inspection.R;
import com.xgf.inspection.entity.ImageValue;
import com.xgf.inspection.ui.utils.ListItemClickHelp;

public class GvShowAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ImageValue> mDatas;

	private boolean isAddDisappear;

	private ListItemClickHelp mCallback;

	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> mIsSelected = new HashMap<Integer, Boolean>();

	public GvShowAdapter(Context context, ArrayList<ImageValue> data,
			ListItemClickHelp callback) {

		this.context = context;
		this.mDatas = data;
		this.mCallback = callback;
	}

	public void initCheck() {
		for (int i = 0; i < mDatas.size(); i++) {
			getmIsSelected().put(i, false);
		}
	}

	public void initChecked() {
		for (int i = 0; i < mDatas.size(); i++) {
			getmIsSelected().put(i, true);
		}
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View currentView, ViewGroup arg2) {
		HolderView holderView = null;
		if (currentView == null) {
			holderView = new HolderView();
			currentView = LayoutInflater.from(context).inflate(
					R.layout.gv_common_item, null);
			holderView.iconIv = (ImageView) currentView
					.findViewById(R.id.gv_common_iv);
			holderView.cb = (CheckBox) currentView
					.findViewById(R.id.gv_common_cb);
			// holderView.nameTv = (TextView) currentView
			// .findViewById(R.id.gv_common_name_tv);

			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}
		holderView.iconIv.setImageBitmap(mDatas.get(position).getBitmap());
		holderView.cb.setVisibility(View.VISIBLE);

		final int tempPosition = position;
		final View view = currentView;
		final int which = holderView.cb.getId();

		holderView.cb.setChecked(getmIsSelected().get(position));
		holderView.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mCallback.onClick(view, buttonView, tempPosition, which,
						isChecked);
			}
		});
		return currentView;
	}

	public class HolderView {

		private ImageView iconIv;

		private CheckBox cb;

		private TextView nameTv;

	}

	public boolean isAddDisappear() {
		return isAddDisappear;
	}

	public void setAddDisappear(boolean isAddDisappear) {
		this.isAddDisappear = isAddDisappear;
	}

	public static HashMap<Integer, Boolean> getmIsSelected() {
		return mIsSelected;
	}

	public static void setmIsSelected(HashMap<Integer, Boolean> mIsSelected) {
		GvShowAdapter.mIsSelected = mIsSelected;
	}

}
