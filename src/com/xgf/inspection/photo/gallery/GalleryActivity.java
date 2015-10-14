package com.xgf.inspection.photo.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xgf.inspection.R;
import com.xgf.inspection.entity.ImageValue;
import com.xgf.inspection.network.logic.AppLogic;
import com.xgf.inspection.photo.cropimage.CropHelper;
import com.xgf.inspection.photo.utils.OSUtils;
import com.xgf.inspection.ui.adapter.GvAdapter;
import com.xgf.inspection.ui.utils.ListItemClickHelp;
import com.xgf.inspection.ui.view.CustomGridView;
import com.xgf.inspection.utils.DeviceUuidFactory;
import com.xgf.inspection.utils.FileUtils;
import com.xgf.inspection.utils.ImageUtils;

public class GalleryActivity extends Activity implements OnClickListener,
		ListItemClickHelp {

	private CropHelper mCropHelper;

	private Context mContext;

	private CustomGridView mImageGv;
	private ArrayList<ImageValue> mImageList = new ArrayList<ImageValue>();
	private GvAdapter mAdapter;
	private ImageValue mAddImageValue;

	private LinearLayout mSubmitLl;
	private LinearLayout mDelLl;

	private HashMap<Integer, Boolean> mSelect = new HashMap<Integer, Boolean>();
	private boolean isComplete = false;

	private String mQrCode;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {

			case AppLogic.SEND_RECORD_SUC: {
			}
			case AppLogic.SEND_RECORD_FAIL: {
				break;
			}
			case AppLogic.SEND_RECORD_EXCEPTION: {
				break;
			}

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		FileUtils.makeDirectory(FileUtils.BASE_PATH);
		mContext = GalleryActivity.this;
		initView();
		initData();
	}

	private void initView() {
		mSubmitLl = (LinearLayout) findViewById(R.id.gallery_bottom_menu_submit_ll);
		mDelLl = (LinearLayout) findViewById(R.id.gallery_bottom_menu_del_ll);
		mSubmitLl.setOnClickListener(this);
		mDelLl.setOnClickListener(this);

		mImageGv = (CustomGridView) findViewById(R.id.gallery_gv);
		mAdapter = new GvAdapter(mContext, mImageList, this);
		mAdapter.setAddDisappear(false);
		mImageGv.setAdapter(mAdapter);

		mImageGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == mImageList.size() - 1) {
					if (!isComplete) {
						mCropHelper.startCamera();
					}
				}
			}
		});
	}

	private void initData() {
		mAddImageValue = new ImageValue();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.add);
		mAddImageValue.setBitmap(bitmap);
		mImageList.add(mAddImageValue);
		mCropHelper = new CropHelper(this, OSUtils.getSdCardDirectory()
				+ "/head.png");

		mQrCode = getIntent().getExtras().getString("QrCode");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("onActivityResult", requestCode + "**" + resultCode);
		if (requestCode == RESULT_CANCELED) {
			return;
		} else {
			switch (requestCode) {
			case CropHelper.HEAD_FROM_ALBUM:
				mCropHelper.getDataFromAlbum(data);
				Log.e("onActivityResult", "接收到图库图片");
				break;
			case CropHelper.HEAD_FROM_CAMERA:
				mCropHelper.getDataFromCamera(data);
				Log.e("onActivityResult", "接收到拍照图片");
				break;
			case CropHelper.HEAD_SAVE_PHOTO:
				if (data != null && data.getParcelableExtra("data") != null) {
					ImageValue imageValue = new ImageValue();
					imageValue.setBitmap((Bitmap) data
							.getParcelableExtra("data"));
					ArrayList<ImageValue> imageList = new ArrayList<ImageValue>();
					if (mImageList.size() < 3) {
						for (int i = 0; i < mImageList.size() - 1; i++) {
							imageList.add(mImageList.get(i));
						}
						String localUrl = OSUtils.getSdCardDirectory()
								+ "/ins/"
								+ String.valueOf(System.currentTimeMillis())
								+ ".png";
						mCropHelper.savePhoto(data, localUrl);
						mImageList.clear();
						mImageList.addAll(imageList);
						mImageList.add(imageValue);
						mImageList.add(mAddImageValue);
					} else {
						String localUrl = OSUtils.getSdCardDirectory()
								+ "/ins/"
								+ String.valueOf(System.currentTimeMillis())
								+ ".png";
						mCropHelper.savePhoto(data, localUrl);
						mImageList.set(2, imageValue);
						isComplete = true;
						mAdapter.setAddDisappear(true);
					}
					mAdapter.notifyDataSetChanged();

				}
				break;
			default:
				break;
			}
		}
	}

	private void submint() {
		if (isComplete) {
			DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(
					mContext);
			AppLogic.SendWirePoleCheckRecord(mContext, mHandler,
					deviceUuidFactory.uuid.toString(), mQrCode, String
							.valueOf(System.currentTimeMillis()), "phote_one",
					ImageUtils
							.Bitmap2StrByBase64(mImageList.get(0).getBitmap()));
		}

	}

	private void del() {
		boolean isHasSelect = false;
		for (Map.Entry<Integer, Boolean> entry : mSelect.entrySet()) {
			if (entry.getValue()) {
				isHasSelect = true;
				mImageList.remove(entry.getKey());
				File file = new File(mImageList.get(entry.getKey())
						.getLocalUrl());
				com.xgf.inspection.photo.utils.FileUtils.deleteAllFiles(file);
			}
		}
		if (!isHasSelect) {
			Toast.makeText(mContext, "请选择图片！", Toast.LENGTH_SHORT).show();
			return;
		}

		ArrayList<ImageValue> imageList = new ArrayList<ImageValue>();
		for (int i = 0; i < mImageList.size(); i++) {
			imageList.add(mImageList.get(i));
		}

		mImageList.clear();
		mImageList.addAll(imageList);
		if (isHasSelect) {
			isComplete = false;
			mAdapter.setAddDisappear(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gallery_bottom_menu_submit_ll: {
			submint();
			break;
		}
		case R.id.gallery_bottom_menu_del_ll: {
			del();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onClick(View item, View widget, int position, int which,
			boolean isCheck) {
		mSelect.put(position, isCheck);

		boolean isHasAllSelect = true;
		boolean isHasSelect = true;
		for (Map.Entry<Integer, Boolean> entry : mSelect.entrySet()) {
			if (!entry.getValue()) {
				isHasAllSelect = false;
			}
			if (entry.getValue()) {
				isHasSelect = true;
			}
		}
		if (!isHasAllSelect) {
			mSubmitLl.setBackgroundColor(getResources().getColor(
					R.color.gray_bg));
		}

		if (!isHasSelect) {
			mDelLl.setBackgroundColor(getResources().getColor(R.color.gray_bg));
		}

	}
}
