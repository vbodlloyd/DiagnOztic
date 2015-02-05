package com.naio.diagnostic.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naio.diagnostic.R;
import com.naio.diagnostic.R.id;
import com.naio.diagnostic.R.layout;
import com.naio.diagnostic.threads.ReadSocketThread;
import com.naio.diagnostic.trames.GPSTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class GoogleMapV2Activity extends Activity {
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;
	private TrameDecoder trameDecoder;
	private MemoryBuffer memoryBuffer;
	private ReadSocketThread readSocketThread;
	private Handler handler= new Handler();
	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}


	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);
		trameDecoder = new TrameDecoder();
		memoryBuffer = new MemoryBuffer();
		readSocketThread = new ReadSocketThread(memoryBuffer, Config.PORT_GPS);
		readSocketThread.start();
		handler.postDelayed(runnable, 20);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map_frag)).getMap();

		if (map != null) {
			Marker hamburg = map.addMarker(new MarkerOptions()
					.position(HAMBURG).title("Oz"));
		}
		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}
	
	private void read_the_queue() {
		GPSTrame gps = (GPSTrame) trameDecoder.decode(memoryBuffer.getPollFifo());
		if (gps != null) {
			map.clear();
			LatLng latlng = new LatLng(gps.getLat(), gps.getLon());
			map.addMarker(new MarkerOptions()
					.position(latlng).title("Oz"));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
			// Zoom in, animating the camera.
			//map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		}
		handler.postDelayed(runnable, 64);//15FPS
		
	}
}