package com.naio.diagnostic.activities;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.naio.diagnostic.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BilanUtilisationFragment extends Fragment {

	private GoogleMap map;
	private ArrayList<LatLng> listPointMap;
	private boolean firstTimeDisplayTheMap;
	private static View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
		
		double[] gps = getArguments().getDoubleArray("gps");
		listPointMap = new ArrayList<LatLng>();
		try {
			view = inflater.inflate(R.layout.bilan_activity, container, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		map = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map_frag_frag)).getMap();
		
		for (int i = 0; i < gps.length; i=i+2) {
			LatLng latlng = new LatLng(gps[i], gps[i + 1]);
			PolylineOptions option= new PolylineOptions().width(5)
					.color(Color.BLUE).addAll(listPointMap);
			map.addPolyline(option);
			listPointMap.add(latlng);
			if(i == 0 || i == gps.length - 2){
				map.addMarker(new MarkerOptions().position(latlng).title("Oz"));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
			}
		}

		

		return view;
	}
}