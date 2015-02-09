package com.naio.diagnostic.activities;

import java.util.ArrayList;
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
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.MyMoveListenerForAnalogueView;
import com.naio.opengl.MyGLSurfaceView;
import com.naio.views.AnalogueView;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

public class LidarGPSMotorsActivity extends FragmentActivity {
	private OpenGLES20Fragment openglfragment;
	private TrameDecoder trameDecoder;
	private MemoryBuffer memoryBufferLidar;
	private ReadSocketThread readSocketThreadLidar;
	private Handler handler = new Handler();
	private SendSocketThread sendSocketThreadMotors;
	private GoogleMap map;
	private ReadSocketThread readSocketThreadMap;
	private MemoryBuffer memoryBufferMap;
	private boolean firstTimeDisplayTheMap;
	private List<LatLng> listPointMap;
	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

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
			memoryBufferLidar = new MemoryBuffer();
			memoryBufferMap = new MemoryBuffer();
			listPointMap = new ArrayList<LatLng>();
			firstTimeDisplayTheMap = true;
			readSocketThreadMap = new ReadSocketThread(memoryBufferMap,
					Config.PORT_GPS);
			readSocketThreadLidar = new ReadSocketThread(memoryBufferLidar,
					Config.PORT_LIDAR);
			sendSocketThreadMotors = new SendSocketThread();
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

			// start the threads
			readSocketThreadLidar.start();
			readSocketThreadMap.start();
			sendSocketThreadMotors.start();

			handler.postDelayed(runnable, 10);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DataManager.getInstance().write_in_file(this);
		readSocketThreadLidar.setStop(false);
		readSocketThreadMap.setStop(false);
		sendSocketThreadMotors.setStop(false);
	}

	private void read_the_queue() {
		display_lidar_info();
		display_gps_info();
		handler.postDelayed(runnable, 64);// 15FPS
	}

	private void display_gps_info() {
		GPSTrame gps = (GPSTrame) trameDecoder.decode(memoryBufferMap
				.getPollFifo());
		if (gps != null) {
			TextView altitude = (TextView) findViewById(R.id.textview_altitude);
			altitude.setText("Altitude:" + gps.getAlt()+" m");
			TextView vitesse = (TextView) findViewById(R.id.textview_groundspeed);
			vitesse.setText("Vitesse:" + gps.getGroundSpeed()+" km/h");
			map.clear();
			LatLng latlng = new LatLng(gps.getLat(), gps.getLon());
			PolylineOptions option = new PolylineOptions().width(5)
					.color(Color.BLUE).addAll(listPointMap);
			map.addPolyline(option);
			listPointMap.add(latlng);
			DataManager.getInstance().addPoints_position_oz(latlng.latitude + "#" + latlng.longitude + "%");
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
	
	private void set_the_analogueView() {
		AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
		analView.setOnMoveListener(new MyMoveListenerForAnalogueView(sendSocketThreadMotors));
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
		overridePendingTransition(R.animator.animation_end2, R.animator.animation_end1);
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
