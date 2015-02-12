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

import net.sourceforge.juint.UInt8;

import android.util.Log;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

public class LogTrame extends Trame {

	private byte[] message;
	private byte type;
	private byte[] ligne_a_x = new byte[8];
	private byte[] ligne_a_y = new byte[8];
	private byte[] ligne_b_x = new byte[8];
	private byte[] ligne_b_y = new byte[8];
	private final Object lock = new Object();
	private ArrayList<double[][]> arrayListPoint = new ArrayList<double[][]>();

	public LogTrame(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER + 2;

		type = data[Config.LENGHT_FULL_HEADER];
		if (type == 1) {
			synchronized (lock) {
				Log.e("typenombreligne", ""
						+ data[Config.LENGHT_FULL_HEADER + 1] +"  lenght :"+data.length);
				if (data.length == (data[Config.LENGHT_FULL_HEADER + 1] * 32
						+ Config.LENGHT_FULL_HEADER + 2 + Config.LENGHT_CHECKSUM)) {

					for (int i = 0; i < data[Config.LENGHT_FULL_HEADER + 1]; i++) {
						ligne_a_x = new byte[] { data[offset + 7],
								data[offset + 6], data[offset + 5],
								data[offset + 4], data[offset + 3],
								data[offset + 2], data[offset + 1],
								data[offset] };
						ligne_a_y = new byte[] { data[offset + 15],
								data[offset + 14], data[offset + 13],
								data[offset + 12], data[offset + 11],
								data[offset + 10], data[offset + 9],
								data[offset + 8] };
						ligne_b_x = new byte[] { data[offset + 23],
								data[offset + 22], data[offset + 21],
								data[offset + 20], data[offset + 19],
								data[offset + 18], data[offset + 17],
								data[offset + 16] };
						ligne_b_y = new byte[] { data[offset + 31],
								data[offset + 30], data[offset + 29],
								data[offset + 28], data[offset + 27],
								data[offset + 26], data[offset + 25],
								data[offset + 24] };
						offset = offset + 32;
						arrayListPoint.add(getPoints());
					}
					DataManager.getInstance().fifoLines.add(arrayListPoint);
				}
			}
		}
		if (type == 2) {
			if (data.length > Config.LENGHT_FULL_HEADER + 1
					+ Config.LENGHT_CHECKSUM) {
				Log.e("type", "image  " + data.length);

				byte[] datacopy = Arrays.copyOfRange(data,
						Config.LENGHT_FULL_HEADER + 1, data.length
								- Config.LENGHT_CHECKSUM);

				DataManager.getInstance().fifoImage.offer(datacopy);

			}
		}
	}

	public double[] getPointA() {
		double[] points = new double[2];
		points[0] = ByteBuffer.wrap(ligne_a_x).getDouble(0);
		points[1] = ByteBuffer.wrap(ligne_a_y).getDouble(0);
		return points;
	}

	public double[] getPointB() {
		double[] points = new double[2];
		points[0] = ByteBuffer.wrap(ligne_b_x).getDouble(0);
		points[1] = ByteBuffer.wrap(ligne_b_y).getDouble(0);
		return points;
	}

	public double[][] getPoints() {
		double[][] points = new double[2][2];
		points[0] = getPointB();
		points[1] = getPointA();
		return points;
	}

	

	public int getType() {
		return type;
	}

}
