package com.naio.diagnostic.activities;

import java.util.Arrays;

import com.naio.diagnostic.R;

import com.naio.diagnostic.threads.ReadSocketThread;

import com.naio.diagnostic.trames.LogTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.MemoryBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CameraActivity extends FragmentActivity {
	private static final int MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private TrameDecoder trameDecoder;

	private Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

	private MemoryBuffer memoryBufferLog;
	private ReadSocketThread readSocketThreadLog;

	private ImageView imageview;

	private int nbrImage;

	private ImageView imageview_r;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// change the color of the action bar
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.camera_activity);

		getSupportFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {
					public void onBackStackChanged() {
						int backCount = getSupportFragmentManager()
								.getBackStackEntryCount();
						if (backCount == 0) {
							finish();
						}
					}
				});

		if (savedInstanceState == null) {
			nbrImage = 0;
			imageview = (ImageView) findViewById(R.id.imageview);
			imageview_r = (ImageView) findViewById(R.id.imageview_r);
			imageview.setOnClickListener(new OnClickListener() {
				boolean fullsize = false;

				@Override
				public void onClick(View v) {

					if (!fullsize) {
						v.startAnimation(AnimationUtils.loadAnimation(
								v.getContext(), R.animator.animation_imageview));
						imageview
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.MATCH_PARENT));

					} else {
						v.startAnimation(AnimationUtils.loadAnimation(
								v.getContext(),
								R.animator.animation_imageview_return));
						imageview
								.setLayoutParams(new LinearLayout.LayoutParams(
										320, 240));

					}
					fullsize = !fullsize;
				}
			});
			
			imageview_r.setOnClickListener(new OnClickListener() {
				boolean fullsize = false;

				@Override
				public void onClick(View v) {

					if (!fullsize) {
						v.startAnimation(AnimationUtils.loadAnimation(
								v.getContext(), R.animator.animation_imageview));
						imageview_r
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.MATCH_PARENT));

					} else {
						v.startAnimation(AnimationUtils.loadAnimation(
								v.getContext(),
								R.animator.animation_imageview_return));
						imageview_r
								.setLayoutParams(new LinearLayout.LayoutParams(
										320, 240));

					}
					fullsize = !fullsize;
				}
			});
			trameDecoder = new TrameDecoder();

			memoryBufferLog = new MemoryBuffer();

			readSocketThreadLog = new ReadSocketThread(memoryBufferLog,
					Config.PORT_LOG);

			DataManager.getInstance().setPoints_position_oz("");
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			readSocketThreadLog.start();

			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// DataManager.getInstance().write_in_file(this);

		readSocketThreadLog.setStop(false);

	}

	private void read_the_queue() {

		display_image();
		handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}

	private void display_image() {
		LogTrame log = (LogTrame) trameDecoder.decode(memoryBufferLog
				.getPollFifo());
		byte[] data = DataManager.getInstance().getPollFifoImage();
		if (data == null)
			return;

		byte[] dataf = Arrays.copyOfRange(data, 2, data.length);
		Bitmap bm = BitmapFactory.decodeByteArray(dataf, 0, dataf.length);
		Log.e("typeLidarGps", "nbr image :" + nbrImage++);

		if (data[0] == 0) {
			imageview.setImageBitmap(bm);
		} else {

			imageview_r.setImageBitmap(bm);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.animator.animation_end2,
				R.animator.animation_end1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
