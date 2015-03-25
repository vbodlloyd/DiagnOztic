package com.naio.diagnostic.activities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.naio.diagnostic.R;

import com.naio.diagnostic.threads.ReadSocketThread;

import com.naio.diagnostic.trames.LogTrame;
import com.naio.diagnostic.trames.OdoTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.opengl.OpenGLRenderer;
import com.naio.opengl.SimplePlane;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;

public class CameraActivity extends FragmentActivity {
	private static final int MILLISECONDS_RUNNABLE = 64; // 64 for 15fps

	private TrameDecoder trameDecoder;

	private Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};

	private NewMemoryBuffer memoryBufferLog;
	private ReadSocketThread readSocketThreadLog;
	private ImageView imageview;
	private int nbrImage;
	private ImageView imageview_r;
	private NewMemoryBuffer memoryBufferOdo;
	private ReadSocketThread readSocketThreadOdo;
	private TextView odo_display;
	private ArrayList<SimplePlane> arrayPoints = new ArrayList<SimplePlane>();
	private static float scaleX = 3.5f;
	private static float scaleY = 2.5f;
	private float rapScaleX;
	private float rapScaleY;

	private SimplePlane plane;

	private OpenGLRenderer renderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		arrayPoints = new ArrayList<SimplePlane>();
		// change the color of the action bar
		/*
		 * getActionBar().setBackgroundDrawable(
		 * getResources().getDrawable(R.drawable.form));
		 */
		// Remove the title bar from the window.

		// Make the windows into full screen mode.
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setContentView(R.layout.camera_activity);
		// Create a OpenGL view.
		// GLSurfaceView view = (GLSurfaceView) findViewById(R.id.opengl_view);
		GLSurfaceView view = new GLSurfaceView(this);

		// Creating and attaching the renderer.
		renderer = new OpenGLRenderer();
		view.setRenderer(renderer);
		setContentView(view);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Create a new plane.
		plane = new SimplePlane(1, 1);
		plane.sx = scaleX;
		plane.sy = scaleY;
		plane.z = 0.0f;

		rapScaleX = scaleX / 2.5f;
		rapScaleY = scaleY / 2.5f;

		// Load the texture.
		plane.loadBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.fleche));

		// Add the plane to the renderer.
		renderer.addMesh(plane);

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
			/*
			 * nbrImage = 0; odo_display = (TextView)
			 * findViewById(R.id.odo_text); imageview = (ImageView)
			 * findViewById(R.id.imageview); imageview_r = (ImageView)
			 * findViewById(R.id.imageview_r);
			 */
			/*
			 * imageview.setOnClickListener(new OnClickListener() { boolean
			 * fullsize = false;
			 * 
			 * @Override public void onClick(View v) {
			 * 
			 * if (!fullsize) { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview)); imageview
			 * .setLayoutParams(new LinearLayout.LayoutParams(
			 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			 * 
			 * } else { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview_return));
			 * imageview .setLayoutParams(new LinearLayout.LayoutParams( 320,
			 * 240));
			 * 
			 * } fullsize = !fullsize; } });
			 * 
			 * imageview_r.setOnClickListener(new OnClickListener() { boolean
			 * fullsize = false;
			 * 
			 * @Override public void onClick(View v) {
			 * 
			 * if (!fullsize) { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview)); imageview_r
			 * .setLayoutParams(new LinearLayout.LayoutParams(
			 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			 * 
			 * } else { v.startAnimation(AnimationUtils.loadAnimation(
			 * v.getContext(), R.animator.animation_imageview_return));
			 * imageview_r .setLayoutParams(new LinearLayout.LayoutParams( 320,
			 * 240));
			 * 
			 * } fullsize = !fullsize; } });
			 */
			trameDecoder = new TrameDecoder();

			memoryBufferLog = new NewMemoryBuffer();
			memoryBufferOdo = new NewMemoryBuffer();

			readSocketThreadLog = new ReadSocketThread(memoryBufferLog,
					Config.PORT_LOG);
			readSocketThreadOdo = new ReadSocketThread(memoryBufferOdo,
					Config.PORT_ODO);

			DataManager.getInstance().setPoints_position_oz("");
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			readSocketThreadLog.start();
			readSocketThreadOdo.start();

			handler.postDelayed(runnable, MILLISECONDS_RUNNABLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// DataManager.getInstance().write_in_file(this);

		readSocketThreadLog.setStop(false);
		readSocketThreadOdo.setStop(false);
		handler.removeCallbacks(runnable);

	}

	private void read_the_queue() {
		display_image();
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

	private void display_image() {
		for (int waz = 0; waz < 2; waz++) {
			if (waz == 0) {
				LogTrame log = (LogTrame) trameDecoder.decode(memoryBufferLog
						.getPollAntepenultiemeFifo());
			}
			if (waz == 1) {
				LogTrame log = (LogTrame) trameDecoder.decode(memoryBufferLog
						.getPollFifo());
			}
			byte[] data = DataManager.getInstance().getPollFifoImage();
			if (data == null)
				return;
			byte[] dataf = Arrays.copyOfRange(data,
					Config.LENGHT_FULL_HEADER + 3, data.length
							- Config.LENGHT_CHECKSUM);
			if (data[Config.LENGHT_FULL_HEADER + 2 ] == 1) {
				Bitmap bm = BitmapFactory.decodeByteArray(dataf, 0,
						dataf.length);
				if (bm == null)
					return;

				plane.loadBitmap(bm);
			} else {// greyscale here
				/*byte[] dataf2 = Arrays.copyOfRange(dataf,
						6, dataf.length
								- Config.LENGHT_CHECKSUM);*/
				/*short width = ByteBuffer.wrap(new byte[]{dataf[1],dataf[0]}).getShort(0);
				short height = ByteBuffer.wrap(new byte[]{dataf[3],dataf[2]}).getShort(0);*/
				byte[] Bits = new byte[752 * 480 * 4 /*width*height*4*/]; // That's where the RGBA
														// array goes.
			
				/*if(dataf[4]== 0x1){//rgb
				 	if(dataf[5] == 0x1){
						int i;
						int j=0;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = dataf[j++];
							Bits[i * 4 + 1] = dataf[j++];
							Bits[i * 4 + 2] = (byte) dataf[j++];
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					}else id(dataf[5] == 0x2){
						int i;
						int j=0;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+1],dataf[j]}).getShort()/255);
							Bits[i * 4 + 1] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+3],dataf[j+2]}).getShort()/255);
							Bits[i * 4 + 2] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+5],dataf[j+4]}).getShort()/255);
							j+=6;
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					}
				}else if(dataf[4]==0x2){//bgr
					if(dataf[5] == 0x1){
						int i;
						int j=0;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = dataf[j+2];
							Bits[i * 4 + 1] = dataf[j+1];
							Bits[i * 4 + 2] = (byte) dataf[j];
							j+=3;
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					}
					}else id(dataf[5] == 0x2){
						int i;
						int j=0;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+5],dataf[j+4]}).getShort()/255);
							Bits[i * 4 + 1] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+3],dataf[j+2]}).getShort()/255);
							Bits[i * 4 + 2] = (byte)(ByteBuffer.wrap(new byte[]{dataf[j+1],dataf[j]}).getShort()/255);
							j+=6;
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					}
				}
				}else if(dataf[4]==0x3){//grey
					if(dataf[5] == 0x1){
						int i;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) dataf[i];
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					}else if(dataf[5] == 0x2){
						int i;
						int j=0;
						for (i = 0; i < dataf.length; i++) {
							Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) (ByteBuffer.wrap(new byte[]{dataf[j+1],dataf[j]}).getShort()/255);
							j+=2;
							Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
						}
					
					}
				}*/
				int i;
				for (i = 0; i < dataf.length; i++) {
					Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) dataf[i]; 
					Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
				}

				// Now put these nice RGBA pixels into a Bitmap object

				Bitmap bm = Bitmap.createBitmap(752, 480,
						Bitmap.Config.ARGB_8888);
				bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
				plane.loadBitmap(bm);
			}

			/*
			 * if (data[Config.LENGHT_FULL_HEADER + 1] == 0) {
			 * imageview.setImageBitmap(bm); } else {
			 * 
			 * imageview_r.setImageBitmap(bm);
			 * 
			 * }
			 */
			ArrayList<float[]> dataPoints2d = DataManager.getInstance()
					.getPollFifoPoints2D();
			if (dataPoints2d == null)
				return;
			float w = dataPoints2d.get(0)[0];
			float h = dataPoints2d.get(0)[1];
			float xa = 0, ya = 0;
			for (int i = 1; i < dataPoints2d.size(); i++) {
				float x = dataPoints2d.get(i)[0];
				float y = dataPoints2d.get(i)[1];

				if (arrayPoints.size() <= i - 1) {
					SimplePlane dott = new SimplePlane(0.1f, 0.1f);
					dott.z = 0.01f;
					if (2 * x / w - 1 >= 0)
						dott.x = 1.21f * rapScaleX * ((2 * (x + 10) / w) - 1);
					else
						dott.x = 1.21f * rapScaleX * ((2 * (x - 10) / w) - 1);
					if (2 * y / h - 1 >= 0)
						dott.y = 1.2f * rapScaleY * ((2 * (y + 10) / h) - 1);
					else
						dott.y = 1.2f * rapScaleY * ((2 * (y - 10) / h) - 1);
					dott.sx = 0.0625f;
					dott.sy = 0.0625f;
					/*
					 * dot.sx = 1/10; dot.sy = 1/10;
					 */
					dott.loadBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.end_point));
					arrayPoints.add(dott);
					renderer.addMesh(dott);
				} else {
					if (2 * x / w - 1 >= 0)
						arrayPoints.get(i - 1).x = 1.21f * rapScaleX
								* ((2 * (x + 10) / w) - 1);
					else
						arrayPoints.get(i - 1).x = 1.21f * rapScaleX
								* ((2 * (x - 10) / w) - 1);
					if (2 * y / h - 1 >= 0)
						arrayPoints.get(i - 1).y = 1.20f * rapScaleY
								* ((2 * (y + 10) / h) - 1);
					else
						arrayPoints.get(i - 1).y = 1.20f * rapScaleY
								* ((2 * (y - 10) / h) - 1);
				}
				if (i - 1 <= arrayPoints.size()
						&& i == (dataPoints2d.size() - 1)) {
					int s = arrayPoints.size();
					for (int j = (i - 1); j < s; j++) {
						arrayPoints.remove(j);
					}
				}
			}
		}
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
