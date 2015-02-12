package com.naio.diagnostic.activities;

import com.naio.diagnostic.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class HubActivity extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.hub2_activity);
		
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
	
	public void go_to_bilan_activity(View v){
		Intent intent = new Intent(this, BilanUtilisationActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.animation3, R.animator.animation2);
	}
	
	public void go_to_lidar_gps_control_activity(View v){
		Intent intent = new Intent(this, LidarGPSMotorsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.animation1, R.animator.animation2);
	}
	
	public void go_to_camera_activity(View v){
		Intent intent = new Intent(this, CameraActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.animation1, R.animator.animation2);
	}
}
