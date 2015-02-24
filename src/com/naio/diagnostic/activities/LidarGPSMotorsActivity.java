package com.naio.diagnostic.activities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naio.diagnostic.R;
import com.naio.diagnostic.opengl.OpenGLES20Fragment;
import com.naio.diagnostic.threads.ReadSocketThread;
import com.naio.diagnostic.threads.SendSocketThread;
import com.naio.diagnostic.trames.GPSTrame;
import com.naio.diagnostic.trames.LidarTrame;
import com.naio.diagnostic.trames.LogTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.MyMoveListenerForAnalogueView;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.opengl.MyGLSurfaceView;
import com.naio.views.AnalogueView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class LidarGPSMotorsActivity extends FragmentActivity {
	private static final int MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private OpenGLES20Fragment openglfragment;
	private TrameDecoder trameDecoder;
	private NewMemoryBuffer memoryBufferLidar;
	private ReadSocketThread readSocketThreadLidar;
	private Handler handler = new Handler();
	private SendSocketThread sendSocketThreadMotors;
	private GoogleMap map;
	private ReadSocketThread readSocketThreadMap;
	private NewMemoryBuffer memoryBufferMap;
	private boolean firstTimeDisplayTheMap;
	private List<LatLng> listPointMap;
	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

	private NewMemoryBuffer memoryBufferLog;

	private ReadSocketThread readSocketThreadLog;

	private SendSocketThread sendSocketThreadActuators;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// change the color of the action bar
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.lidar_gps_motors_activity);

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
			trameDecoder = new TrameDecoder();
			memoryBufferLidar = new NewMemoryBuffer();
			memoryBufferLog = new NewMemoryBuffer();
			memoryBufferMap = new NewMemoryBuffer();
			listPointMap = new ArrayList<LatLng>();
			firstTimeDisplayTheMap = true;
			readSocketThreadMap = new ReadSocketThread(memoryBufferMap,
					Config.PORT_GPS);
			readSocketThreadLidar = new ReadSocketThread(memoryBufferLidar,
					Config.PORT_LIDAR);
			readSocketThreadLog = new ReadSocketThread(memoryBufferLog,
					Config.PORT_LOG);
			sendSocketThreadMotors = new SendSocketThread(Config.PORT_MOTORS);
			sendSocketThreadActuators = new SendSocketThread(Config.PORT_ACTUATOR);
			DataManager.getInstance().setPoints_position_oz("");
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			// initialize the fragments
			openglfragment = new OpenGLES20Fragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.list, openglfragment).addToBackStack(null)
					.commit();
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map_frag)).getMap();
			set_the_analogueView();
			set_the_actuator_button();

			// start the threads
			readSocketThreadLidar.start();
			readSocketThreadMap.start();
			readSocketThreadLog.start();
			sendSocketThreadMotors.start();
			sendSocketThreadActuators.start();

			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DataManager.getInstance().write_in_file(this);
		readSocketThreadLidar.setStop(false);
		
		readSocketThreadMap.setStop(false);
		
		readSocketThreadLog.setStop(false);
		
		sendSocketThreadMotors.setStop(false);
		
		sendSocketThreadActuators.setStop(false);
		
		handler.removeCallbacks(runnable);
	}

	private void read_the_queue() {
		display_lidar_info();
		display_gps_info();
		display_lidar_lines();
		handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}

	private void display_gps_info() {
		GPSTrame gps = (GPSTrame) trameDecoder.decode(memoryBufferMap
				.getPollFifo());
		
		if (gps != null) {
		
			TextView altitude = (TextView) findViewById(R.id.textview_altitude);
			altitude.setText("Altitude:" + gps.getAlt() + " m");
			TextView vitesse = (TextView) findViewById(R.id.textview_groundspeed);
			vitesse.setText("Vitesse:" + gps.getGroundSpeed() + " km/h");
			DataManager.getInstance().write_in_log("alt and vitesse : "+gps.getAlt()+"---"+gps.getGroundSpeed()+"\n");
			map.clear();
			LatLng latlng = new LatLng(gps.getLat(), gps.getLon());
			PolylineOptions option = new PolylineOptions().width(5)
					.color(Color.BLUE).addAll(listPointMap);
			map.addPolyline(option);
			listPointMap.add(latlng);
			DataManager.getInstance().addPoints_position_oz(
					latlng.latitude + "#" + latlng.longitude + "%");
			map.addMarker(new MarkerOptions().position(latlng).title("Oz"));
			if (firstTimeDisplayTheMap) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
				firstTimeDisplayTheMap = false;
			}
		}
	}

	private void display_lidar_info() {
		LidarTrame lidar = (LidarTrame) trameDecoder.decode(memoryBufferLidar
				.getPollFifo());
		if (lidar != null) {
			((MyGLSurfaceView) openglfragment.getView())
					.update_with_uint16(lidar.data_uint16());
		}
	}

	private void display_lidar_lines() {

		LogTrame log = (LogTrame) trameDecoder.decode(memoryBufferLog
				.getPollFifo());
		if (log != null) {
			if (log.getType() == 1)
				((MyGLSurfaceView) openglfragment.getView()).update_line();
		}
	}

	private void set_the_analogueView() {
		AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
		analView.setOnMoveListener(new MyMoveListenerForAnalogueView(
				sendSocketThreadMotors));
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

	private void set_the_actuator_button() {
		Button btn = (Button) findViewById(R.id.actuator_down);
		Button btn2 = (Button) findViewById(R.id.actuator_up);
		btn.setOnTouchListener(new OnTouchListener() {
			byte[] byteDown = new byte[] { 78, 65, 73, 79, 48, 49, 0xf, 1, 0, 0, 0, 2,
					0, 0, 0, 0 };
		

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sendSocketThreadActuators.setBytes(byteDown);
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}

			

		});

		btn2.setOnTouchListener(new OnTouchListener() {
	
			byte[] byteDown = new byte[] { 78, 65, 73, 79, 48, 49, 0xf, 1, 0, 0, 0, 1,
					0, 0, 0, 0 };
			private Handler mHandler;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sendSocketThreadActuators.setBytes(byteDown);
					
					break;
				case MotionEvent.ACTION_UP:
	
					break;
				}
				return false;
			}

			

		});
	}
}
