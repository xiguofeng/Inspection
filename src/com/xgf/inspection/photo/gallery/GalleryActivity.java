package com.xgf.inspection.photo.gallery;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xgf.inspection.R;
import com.xgf.inspection.entity.ImageValue;
import com.xgf.inspection.photo.cropimage.CropHelper;
import com.xgf.inspection.photo.utils.OSUtils;
import com.xgf.inspection.ui.adapter.GvAdapter;
import com.xgf.inspection.ui.view.CustomGridView;

public class GalleryActivity extends Activity implements OnClickListener {

	private CropHelper mCropHelper;

	private Context mContext;

	private CustomGridView mImageGv;
	private ArrayList<ImageValue> mImageList = new ArrayList<ImageValue>();
	private GvAdapter mAdapter;
	private ImageValue mAddImageValue;

	private boolean isComplete = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		mContext = GalleryActivity.this;
		initView();
		initData();
	}

	private void initView() {
		mImageGv = (CustomGridView) findViewById(R.id.gallery_gv);
		mAdapter = new GvAdapter(mContext, mImageList);
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
							mCropHelper.savePhoto(data,
									OSUtils.getSdCardDirectory() + "/ins/" + i
											+ ".png");
						}
						mImageList.clear();
						mImageList.addAll(imageList);
						mImageList.add(imageValue);
						mImageList.add(mAddImageValue);
					} else {
						mCropHelper.savePhoto(data,
								OSUtils.getSdCardDirectory() + "/ins/" + "2"
										+ ".png");
						mImageList.set(2, imageValue);
						isComplete = true;
					}
					mAdapter.notifyDataSetChanged();

				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

	}

}
