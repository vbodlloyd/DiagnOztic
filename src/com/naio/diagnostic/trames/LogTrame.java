package com.naio.diagnostic.trames;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

public class LogTrame extends Trame {

	private byte[] message;
	private byte type;
	private byte[] ligne_a_x = new byte[4];
	private byte[] ligne_a_y = new byte[4];
	private byte[] ligne_b_x = new byte[4];
	private byte[] ligne_b_y = new byte[4];
	private byte[] points2d_x = new byte[4];
	private byte[] points2d_y = new byte[4];
	static final private Integer TYPE_LINES = 1;
	static final private Integer TYPE_IMAGES = 2;
	static final private Integer TYPE_DECISION = 3;
	static final private Integer TYPE_CAPTOR = 4;
	static final private Integer TYPE_POINTS2D_CAMERA = 5;
	static final private Integer TYPE_POINTS3D_CAMERA = 6;
	private final Object lock = new Object();
	private ArrayList<float[][]> arrayListPoint = new ArrayList<float[][]>();
	private ArrayList<float[]> arrayListPoints2D = new ArrayList<float[]>();

	public LogTrame(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER + Config.ID_BYTES_FOR_LOG;
		Log.e("sizethree",""+ data.length);
		type = data[Config.LENGHT_FULL_HEADER];
		if (type == TYPE_LINES) {
			synchronized (lock) {

				if (data.length == (data[Config.LENGHT_FULL_HEADER + 1]
						* Config.LINES_SIZE_IN_BYTES
						+ Config.LENGHT_FULL_HEADER + Config.ID_BYTES_FOR_LOG + Config.LENGHT_CHECKSUM)) {

					for (int i = 0; i < data[Config.LENGHT_FULL_HEADER + 1]; i++) {
						ligne_a_x = new byte[] { data[offset + 3],
								data[offset + 2], data[offset + 1],
								data[offset] };
						ligne_a_y = new byte[] { data[offset + 7],
								data[offset + 6], data[offset + 5],
								data[offset + 4] };
						ligne_b_x = new byte[] { data[offset + 11],
								data[offset + 10], data[offset + 9],
								data[offset + 8] };
						ligne_b_y = new byte[] { data[offset + 15],
								data[offset + 14], data[offset + 13],
								data[offset + 12] };
						offset = offset + Config.LINES_SIZE_IN_BYTES;
						arrayListPoint.add(getPoints());
					}
					DataManager.getInstance().fifoLines.offer(arrayListPoint);
				}
			}
		} else if (type == TYPE_IMAGES) {
			synchronized (lock) {
				if (data.length > Config.LENGHT_FULL_HEADER
						+ Config.ID_BYTES_FOR_LOG + Config.LENGHT_CHECKSUM) {
					/*byte[] datacopy = Arrays.copyOfRange(data,
							Config.LENGHT_FULL_HEADER + 1, data.length
									- Config.LENGHT_CHECKSUM);*/
					DataManager.getInstance().fifoImage.offer(data);
					//Log.e("truc long",""+Arrays.toString(datacopy));
				}
			}
		} else if (type == TYPE_POINTS2D_CAMERA) {
			synchronized (lock) {
				Log.e("h and w",""+ data.length + " visé : " +(data[Config.LENGHT_FULL_HEADER + 1]
						* Config.POINTS2D_SIZE_IN_BYTES
						+ Config.LENGHT_FULL_HEADER + Config.ID_BYTES_FOR_LOG
						+ Config.BYTES_SIZE_W_H + Config.LENGHT_CHECKSUM) );
				if (data.length == (data[Config.LENGHT_FULL_HEADER + 1]
						* Config.POINTS2D_SIZE_IN_BYTES
						+ Config.LENGHT_FULL_HEADER + Config.ID_BYTES_FOR_LOG
						+ Config.BYTES_SIZE_W_H + Config.LENGHT_CHECKSUM)-1) {

					byte[] width = new byte[] { data[offset], data[offset+1] };
					byte[] height = new byte[] { data[offset + 2],
							data[offset + 3] };
					offset += 4;
					float[] dim = new float[2];
					dim[0] = (float) ByteBuffer.wrap(width).getChar(0);
					dim[1] = (float) ByteBuffer.wrap(height).getChar(0);
					Log.e("h and w",""+ dim[0]+"---"+dim[1]);
					arrayListPoints2D.add(dim);
					for (int i = 0; i < data[Config.LENGHT_FULL_HEADER + 1]; i++) {
						points2d_x = new byte[] { data[offset + 3],
								data[offset + 2], data[offset + 1],
								data[offset] };
						points2d_y = new byte[] { data[offset + 7],
								data[offset + 6], data[offset + 5],
								data[offset + 4] };
						offset = offset + Config.POINTS2D_SIZE_IN_BYTES;
						arrayListPoints2D.add(getPoint2D());
					}
					DataManager.getInstance().fifoPoints2D
							.offer(arrayListPoints2D);
				}
			}
		}
	}

	public float[] getPoint2D() {
		float[] points = new float[2];
		points[0] = ByteBuffer.wrap(points2d_x).getFloat(0);
		points[1] = ByteBuffer.wrap(points2d_y).getFloat(0);
		return points;
	}

	public float[] getPointA() {
		float[] points = new float[2];
		points[0] = ByteBuffer.wrap(ligne_a_x).getFloat(0);
		points[1] = ByteBuffer.wrap(ligne_a_y).getFloat(0);
		return points;
	}

	public float[] getPointB() {
		float[] points = new float[2];
		points[0] = ByteBuffer.wrap(ligne_b_x).getFloat(0);
		points[1] = ByteBuffer.wrap(ligne_b_y).getFloat(0);
		return points;
	}

	public float[][] getPoints() {
		float[][] points = new float[2][2];
		points[0] = getPointB();
		points[1] = getPointA();
		return points;
	}

	public int getType() {
		return type;
	}

}
