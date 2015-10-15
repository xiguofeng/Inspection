package com.xgf.inspection.photo.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xgf.inspection.R;
import com.xgf.inspection.entity.ImageValue;
import com.xgf.inspection.network.logic.AppLogic;
import com.xgf.inspection.photo.cropimage.CropHelper;
import com.xgf.inspection.photo.utils.OSUtils;
import com.xgf.inspection.ui.adapter.GvShowAdapter;
import com.xgf.inspection.ui.utils.ListItemClickHelp;
import com.xgf.inspection.ui.view.CustomGridView;
import com.xgf.inspection.ui.view.dialog.widget.AlertDialog;
import com.xgf.inspection.utils.DeviceUuidFactory;
import com.xgf.inspection.utils.FileUtils;
import com.xgf.inspection.utils.ImageUtils;

public class GalleryShowActivity extends Activity implements OnClickListener,
		ListItemClickHelp {

	private CropHelper mCropHelper;

	private Context mContext;

	private CustomGridView mImageGv;
	private ArrayList<ImageValue> mImageList = new ArrayList<ImageValue>();
	private GvShowAdapter mAdapter;

	private TextView mSubmitTv;
	private LinearLayout mDelLl;
	private LinearLayout mAddLl;

	private HashMap<String, Boolean> mSelect = new HashMap<String, Boolean>();
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
		mContext = GalleryShowActivity.this;
		initView();
		initData();
	}

	private void initView() {
		mSubmitTv = (TextView) findViewById(R.id.gallery_submit_tv);
		mDelLl = (LinearLayout) findViewById(R.id.gallery_bottom_menu_del_ll);
		mAddLl = (LinearLayout) findViewById(R.id.gallery_bottom_menu_add_ll);
		mSubmitTv.setOnClickListener(this);
		mDelLl.setOnClickListener(this);
		mAddLl.setOnClickListener(this);

		mImageGv = (CustomGridView) findViewById(R.id.gallery_gv);
		mAdapter = new GvShowAdapter(mContext, mImageList, this);
		mAdapter.setAddDisappear(false);
		mImageGv.setAdapter(mAdapter);

		mImageGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// if (position == mImageList.size() - 1) {
				//
				// }
			}
		});
	}

	private void initData() {
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
					String timeStr = String.valueOf(System.currentTimeMillis());
					String localUrl = OSUtils.getSdCardDirectory() + "/ins/"
							+ timeStr + ".png";
					mCropHelper.savePhoto(data, localUrl);
					imageValue.setId(timeStr);
					imageValue.setLocalUrl(localUrl);
					mImageList.add(imageValue);

					if (mImageList.size() >= 3) {
						isComplete = true;
						mAddLl.setBackgroundColor(getResources()
								.getColor(R.color.gray_search_bg));
					}
					mAdapter.initCheck();
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

	private void add() {
		if (!isComplete) {
			mCropHelper.startCamera();
		}
	}

	private void del() {
		boolean isHasSelect = false;
		for (Map.Entry<String, Boolean> entry : mSelect.entrySet()) {
			if (entry.getValue()) {
				String id = entry.getKey();
				isHasSelect = true;
				for (int i = 0; i < mImageList.size(); i++) {
					if (mImageList.get(i).getId().equals(id)) {
						File file = new File(mImageList.get(i).getLocalUrl());
						com.xgf.inspection.photo.utils.FileUtils
								.deleteAllFiles(file);
						mImageList.remove(i);
					}
				}

			}
		}
		mSelect.clear();
		if (!isHasSelect) {
			Toast.makeText(mContext, "请选择图片！", Toast.LENGTH_SHORT).show();
			return;
		}

		ArrayList<ImageValue> imageList = new ArrayList<ImageValue>();
		for (int i = 0; i < mImageList.size(); i++) {
			imageList.add(mImageList.get(i));
			mSelect.put(mImageList.get(i).getId(), false);
		}

		mImageList.clear();
		mImageList.addAll(imageList);
		if (isHasSelect) {
			isComplete = false;
			mAdapter.setAddDisappear(false);
			mAddLl.setBackgroundColor(getResources()
					.getColor(R.color.red_btn_bg));
		}
		mAdapter.initCheck();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gallery_submit_tv: {
			submint();
			break;
		}
		case R.id.gallery_bottom_menu_del_ll: {
			del();
			break;
		}
		case R.id.gallery_bottom_menu_add_ll: {
			add();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onClick(View item, View widget, int position, int which,
			boolean isCheck) {
		mSelect.put(mImageList.get(position).getId(), isCheck);
		if (isCheck) {
			mDelLl.setBackgroundColor(getResources()
					.getColor(R.color.blue_loding));
		}
		boolean isHasAllSelect = true;
		boolean isHasSelect = true;
		for (Map.Entry<String, Boolean> entry : mSelect.entrySet()) {
			if (!entry.getValue()) {
				isHasAllSelect = false;
			}
			if (entry.getValue()) {
				isHasSelect = true;
			}
		}

		if (!isHasSelect) {
			mDelLl.setBackgroundColor(getResources().getColor(R.color.gray_search_bg));
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			new AlertDialog(GalleryShowActivity.this)
					.builder()
					.setTitle(getString(R.string.prompt))
					.setMsg(getString(R.string.exit_str))
					.setPositiveButton(getString(R.string.confirm),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									File file = new File(OSUtils
											.getSdCardDirectory() + "/ins/");
									com.xgf.inspection.photo.utils.FileUtils
											.deleteAllFiles(file);
									finish();
								}
							})
					.setNegativeButton(getString(R.string.cancal),
							new OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
