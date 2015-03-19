package com.naio.diagnostic.activities;

import com.naio.diagnostic.R;
import com.naio.diagnostic.threads.ReadSocketThread;
import com.naio.diagnostic.trames.OdoTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.NewMemoryBuffer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class DecisionIAActivity extends FragmentActivity {
	private static final int MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private TrameDecoder trameDecoder;

	private Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

	private TextView odo_display;
	private NewMemoryBuffer memoryBufferOdo;
	private ReadSocketThread readSocketThreadOdo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.form));
		setContentView(R.layout.decision_activity);
		memoryBufferOdo = new NewMemoryBuffer();
		odo_display = (TextView) findViewById(R.id.text_odometrie);
		trameDecoder = new TrameDecoder();

		memoryBufferOdo = new NewMemoryBuffer();

		readSocketThreadOdo = new ReadSocketThread(memoryBufferOdo,
				Config.PORT_ODO);
		readSocketThreadOdo.start();

		handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}
	
	private void read_the_queue() {
		display_odo();
		handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
	}

	private void display_odo() {
		OdoTrame odo = (OdoTrame) trameDecoder.decode(memoryBufferOdo
				.getPollFifo());
		if (odo != null) {
			odo_display.setText(odo.show());
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.animator.animation_end2,
				R.animator.animation_end1);
	}
	
}
