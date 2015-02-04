package com.naio.diagnostic;

import com.naio.diagnostic.AnalogueView.OnMoveListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}

	};
	private Handler handler = new Handler();
	private ReadSocketThread readSocketThread;
	private MemoryBuffer memoryBuffer;
	private TrameDecoder trameDecoder;
	private SendSocketThread sst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		trameDecoder = new TrameDecoder();
		AnalogueView analView = (AnalogueView) findViewById(R.id.analogueView1);
		Button buton = ((Button) findViewById(R.id.button_left_motor));
		sst = new SendSocketThread();
		sst.start();
		buton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2,
						-127, 127, 0, 0, 0, 0 };
				sst.setBytes(b);

				return false;
			}
		});
		Button buton2 = ((Button) findViewById(R.id.button_forward_motor));
		buton2.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2,
						127, 127, 0, 0, 0, 0 };
				SendSocketThread sst = new SendSocketThread(b);
				sst.start();
				return false;

			}
		});

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
				byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2,
						xa, ya, 0, 0, 0, 0 };
				Log.e("motor", "xa:" + xa + "  ya:" + ya + "  speed:"
						+ padSpeed + "  diff:" + padDiff + "  bearing:"
						+ bearing);
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
				byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2,
						xa, ya, 0, 0, 0, 0 };
				Log.e("motor2", "xa:" + xa + "  ya:" + ya + "  speed:"
						+ padSpeed + "  diff:" + padDiff);
				sst.setBytes(b);

			}
		});
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

	public void button_test_clicked(View v) {
		/*
		 * socketThread = new SocketThread(); socketThread.giveView(this);
		 * socketThread.start();
		 */
		memoryBuffer = new MemoryBuffer();
		readSocketThread = new ReadSocketThread(memoryBuffer,Config.PORT_GPS);
		readSocketThread.start();
		handler.postDelayed(runnable, 10);

	}

	public void button_stop_clicked(View v) {
		if (readSocketThread != null)
			readSocketThread.setStop(false);
		if (sst != null)
			sst.setStop(false);
		handler.removeCallbacks(runnable);
	}

	public void button_go_motor_clicked(View v) {
		byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2, -127,
				127, 0, 0, 0, 0 };
		SendSocketThread sst = new SendSocketThread(b);
		sst.start();
	}

	public void button_gof_motor_clicked(View v) {
		byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2, 127,
				127, 0, 0, 0, 0 };
		SendSocketThread sst = new SendSocketThread(b);
		sst.start();
	}

	private void read_the_queue() {

		String txt = (trameDecoder.decode(memoryBuffer.getPollFifo())).show();
		if (txt != null) {
			Log.e("lidar",txt);
			((TextView) findViewById(R.id.socket_response)).setText(txt);
		}
		handler.postDelayed(runnable, 10);
	}

}
