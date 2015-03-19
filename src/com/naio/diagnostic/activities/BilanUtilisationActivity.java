package com.naio.diagnostic.activities;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naio.diagnostic.R;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BilanUtilisationActivity extends FragmentActivity {

	private GoogleMap map;
	private HashMap<Integer, double[]> hashmap;
	private String readfile;
	private ArrayList<String> arrayDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.bilan_activity);
		readfile = "";
		try {
			readfile = DataManager.getInstance().getStringFromFile(this,
					Config.FILE_SAVE_GPS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hashmap = new HashMap<Integer, double[]>();
		fill_the_hashmap_with_gps_values();

		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map_frag_frag)).getMap();

		display_the_spinner();

	}

	private void fill_the_hashmap_with_gps_values() {
		arrayDate = new ArrayList<String>();
		String[] all_messages_gps = readfile.split("\n");
		int idx_for_hashmap = 0;
		for (int i = 0; i < all_messages_gps.length; i++) {
			String[] message_gps_parse = all_messages_gps[i].split("-");
			if (message_gps_parse.length > 2) {
				String[] gps = (message_gps_parse[3]).split("%");
				arrayDate.add(message_gps_parse[0]);
				double[] double_lat_lng = new double[(gps.length - 1) * 2];
				int j = 0;
				for (String str : gps) {
					String lat = str.split("#")[0];
					String lon = str.split("#")[1];
					double_lat_lng[j++] = Double.parseDouble(lat);
					if (j >= gps.length) {
						break;
					}
					double_lat_lng[j++] = Double.parseDouble(lon);
				}
				hashmap.put(idx_for_hashmap++, double_lat_lng);
			}
		}

	}

	private void display_the_spinner() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, arrayDate);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// display the path that oz went by and add marker at the
				// beginning and the end
				double[] gps = hashmap.get(position);
				ArrayList<LatLng> listPointMap = new ArrayList<LatLng>();
				map.clear();
				LatLng lastlat = null;
				for (int i = 0; i < gps.length; i = i + 2) {
					if (gps[i] == 0.0f || gps[i + 1] == 0.0f) {
						continue;
					}
					LatLng latlng = new LatLng(gps[i], gps[i + 1]);
					PolylineOptions option = new PolylineOptions().width(5)
							.color(Color.BLUE).addAll(listPointMap);
					map.addPolyline(option);
					listPointMap.add(latlng);
					if (i == 0) {
						map.addMarker(new MarkerOptions().position(latlng)
								.title("Oz Debut"));
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(
								latlng, 18));
					}
					lastlat = latlng;
				}
				map.addMarker(new MarkerOptions()
						.position(lastlat)
						.title("Oz Fin")
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
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

}
