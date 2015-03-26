package com.naio.diagnostic.activities;

import com.naio.diagnostic.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * HubActivity is the main activity of the app.
 * It displays 4 buttons, which lead to anothers activities.
 * @author bodereau
 *
 */
public class HubActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.hub2_activity);
		getWindow()
		.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
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
		overridePendingTransition(R.animator.animation4, R.animator.animation2);
	}
	
	public void go_to_decision_activity(View v){
		Intent intent = new Intent(this, DecisionIAActivity.class);
		startActivity(intent);
		overridePendingTransition(R.animator.animation5, R.animator.animation2);
	}
}
