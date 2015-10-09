package com.xgf.inspection.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xgf.inspection.R;
import com.xgf.inspection.entity.ImageValue;

public class GvAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ImageValue> data;

	public GvAdapter(Context context, ArrayList<ImageValue> data) {

		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
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
			holderView.nameTv = (TextView) currentView
					.findViewById(R.id.gv_common_name_tv);

			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		if (position != data.size() - 1) {
			holderView.iconIv.setImageBitmap(data.get(position).getBitmap());
		}

		holderView.nameTv.setText(data.get(position).getUrl());

		return currentView;
	}

	public class HolderView {

		private ImageView iconIv;

		private TextView nameTv;

	}

}
