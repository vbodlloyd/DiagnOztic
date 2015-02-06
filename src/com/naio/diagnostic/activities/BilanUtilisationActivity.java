package com.naio.diagnostic.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.naio.diagnostic.R;
import com.naio.diagnostic.utils.MyPagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class BilanUtilisationActivity extends FragmentActivity {
	private ViewPager pager;
	private MyPagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		// setContentView(R.layout.bilan_activity);
		String readfile = "";
		try {
			readfile = getStringFromFile("bilan.naio");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(R.layout.viewpager);// Création de la liste de
		// Fragments que fera
		// défiler le PagerAdapter
		List fragments = new Vector();

		// Ajout des Fragments dans la liste
		String[] parse = readfile.split("\n");
		for (int i = parse.length-1; i < parse.length; i++) {
			Bundle bundle = new Bundle();
			String[] gps = (parse[i].split("-")[3]).split("%");
			double[] duble = new double[(gps.length-1)*2];
			int j = 0;
			for(String str : gps){
				String lat = str.split("#")[0];
				String lon = str.split("#")[1];
				duble[j++] = Double.parseDouble(lat);
				if(j >= gps.length){break;}
				duble[j++] = Double.parseDouble(lon);
			}
			bundle.putDoubleArray("gps", duble);
			fragments.add(Fragment.instantiate(this,
					BilanUtilisationFragment.class.getName(),bundle));
		}

		// Création de l'adapter qui s'occupera de l'affichage de la
		// liste de Fragments
		this.mPagerAdapter = new MyPagerAdapter(
				super.getSupportFragmentManager(), fragments);
		pager = (ViewPager) super.findViewById(R.id.viewpager);
		// Affectation de l'adapter au ViewPager
		pager.setAdapter(this.mPagerAdapter);
		pager.setOffscreenPageLimit(1);

	}

	public String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public String getStringFromFile(String filePath) throws Exception {
		File fl = new File(this.getFilesDir(), filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.animator.animation_end2,
				R.animator.animation_end1);
	}

	public static void write_in_file(Context ctx) {
		File gpxfile = new File(ctx.getFilesDir(), "bilan.naio");
		Date date = new Date();
		FileWriter writer;
		try {
			writer = new FileWriter(gpxfile,true);
			writer.append(date.toString() + "-15-" + HubActivity.metre_parcouru
					+ "-" + HubActivity.points_position_oz + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
