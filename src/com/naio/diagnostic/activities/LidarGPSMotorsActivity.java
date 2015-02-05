package com.naio.diagnostic.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naio.diagnostic.R;
import com.naio.diagnostic.threads.ReadSocketThread;
import com.naio.diagnostic.threads.SendSocketThread;
import com.naio.diagnostic.trames.GPSTrame;
import com.naio.diagnostic.trames.LidarTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.AnalogueView;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.AnalogueView.OnMoveListener;
import com.naio.opengl.MyGLSurfaceView;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

public class LidarGPSMotorsActivity extends FragmentActivity {
	private OpenGLES20Fragment openglfragment;
	private TrameDecoder trameDecoder;
	private MemoryBuffer memoryBuffer;
	private ReadSocketThread readSocketThread;
	private Handler handler = new Handler();
	private SendSocketThread sst;
	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};
	private GoogleMap map;
	private ReadSocketThread readSocketThreadMap;
	private MemoryBuffer memoryBufferMap;
	private boolean firstTime;
	private List<LatLng> listPoint;
	private Polyline polyline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Resources res = getResources();

		getActionBar().setBackgroundDrawable(res.getDrawable(R.drawable.form));
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
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			openglfragment = new OpenGLES20Fragment();
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.list, openglfragment).addToBackStack(null)
					.commit();
			trameDecoder = new TrameDecoder();
			memoryBuffer = new MemoryBuffer();
			firstTime = true;
			readSocketThread = new ReadSocketThread(memoryBuffer,
					Config.PORT_LIDAR);
			readSocketThread.start();
			memoryBufferMap = new MemoryBuffer();
			readSocketThreadMap = new ReadSocketThread(memoryBufferMap,
					Config.PORT_GPS);
			readSocketThreadMap.start();
			listPoint = new ArrayList<LatLng>();
			sst = new SendSocketThread();
			sst.start();
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map_frag)).getMap();
			handler.postDelayed(runnable, 10);
			AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
			analView.setOnMoveListener(new OnMoveListener() {

				@Override
				public void onMaxMoveInDirection(int padDiff, int padSpeed) {
					int bearing = padDiff * 127 / 180;
					byte xa = 0;
					byte ya = 0;
					if (padSpeed >= 0) {
						if (padSpeed + bearing > 127)
							xa = (byte) 127;
						else {
							if (padSpeed + bearing < -127)
								xa = (byte) -127;
							else
								xa = (byte) (padSpeed + bearing);
						}

						if (padSpeed - bearing < -127)
							ya = (byte) -127;
						else {
							if (padSpeed - bearing > 127)
								ya = (byte) 127;
							else
								ya = (byte) (padSpeed - bearing);
						}

					} else {
						if (padSpeed - bearing < -127)
							xa = (byte) -127;
						else {
							if (padSpeed - bearing > 127)
								xa = (byte) 127;
							else
								xa = (byte) (padSpeed - bearing);
						}
						if (padSpeed + bearing > 127)
							ya = (byte) 127;
						else {
							if (padSpeed + bearing < -127)
								ya = (byte) -127;
							else
								ya = (byte) (padSpeed + bearing);
						}
					}
					byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0,
							2, xa, ya, 0, 0, 0, 0 };
					sst.setBytes(b);
				}

				@Override
				public void onHalfMoveInDirection(int padDiff, int padSpeed) {
					int bearing = padDiff * 127 / 180;
					byte xa = 0;
					byte ya = 0;
					if (padSpeed >= 0) {
						if (padSpeed + bearing > 127)
							xa = (byte) 127;
						else {
							if (padSpeed + bearing < -127)
								xa = (byte) -127;
							else
								xa = (byte) (padSpeed + bearing);
						}

						if (padSpeed - bearing < -127)
							ya = (byte) -127;
						else {
							if (padSpeed - bearing > 127)
								ya = (byte) 127;
							else
								ya = (byte) (padSpeed - bearing);
						}

					} else {
						if (padSpeed - bearing < -127)
							xa = (byte) -127;
						else {
							if (padSpeed - bearing > 127)
								xa = (byte) 127;
							else
								xa = (byte) (padSpeed - bearing);
						}
						if (padSpeed + bearing > 127)
							ya = (byte) 127;
						else {
							if (padSpeed + bearing < -127)
								ya = (byte) -127;
							else
								ya = (byte) (padSpeed + bearing);
						}
					}
					byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0,
							2, xa, ya, 0, 0, 0, 0 };
					sst.setBytes(b);

				}
			});
		}
	}
@Override
public void onBackPressed() {
	super.onBackPressed();
	readSocketThread.setStop(false);
	readSocketThreadMap.setStop(false);
	sst.setStop(false);
}
	private void read_the_queue() {
		LidarTrame lidar = (LidarTrame) trameDecoder.decode(memoryBuffer
				.getPollFifo());
		if (lidar != null) {
			((MyGLSurfaceView) openglfragment.getView())
					.update_with_uint16(lidar.data_uint16());
		}
		GPSTrame gps = (GPSTrame) trameDecoder.decode(memoryBufferMap
				.getPollFifo());
		if (gps != null) {
			map.clear();
			LatLng latlng = new LatLng(gps.getLat(), gps.getLon());
			PolylineOptions option = new PolylineOptions().width(5).color(Color.BLUE).addAll(listPoint);
			polyline = map.addPolyline(option);
			listPoint.add(latlng);
			map.addMarker(new MarkerOptions().position(latlng).title("Oz"));
			if (firstTime) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
				firstTime = false;
				
			}
			
			// Zoom in, animating the camera.
			// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		}
		handler.postDelayed(runnable, 64);// 15FPS

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
