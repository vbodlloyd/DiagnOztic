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

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

public class LogTrame extends Trame {

	private byte[] message;
	private byte type;
	private byte[] ligne_a_x = new byte[4];
	private byte[] ligne_a_y = new byte[4];
	private byte[] ligne_b_x = new byte[4];
	private byte[] ligne_b_y = new byte[4];
	private final Object lock = new Object();
	private ArrayList<float[][]> arrayListPoint = new ArrayList<float[][]>();

	public LogTrame(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER + 2;

		type = data[Config.LENGHT_FULL_HEADER];
		if (type == 1) {
			synchronized (lock) {

				if (data.length == (data[Config.LENGHT_FULL_HEADER + 1]
						* Config.LINES_SIZE_IN_BYTES
						+ Config.LENGHT_FULL_HEADER + Config.ID_BYTES_FOR_LINES + Config.LENGHT_CHECKSUM)) {

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
		}
		if (type == 2) {
			if (data.length > Config.LENGHT_FULL_HEADER + 2
					+ Config.LENGHT_CHECKSUM) {
				byte[] datacopy = Arrays.copyOfRange(data,
						Config.LENGHT_FULL_HEADER + 1, data.length
								- Config.LENGHT_CHECKSUM);

				DataManager.getInstance().fifoImage.offer(datacopy);

			}
		}
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
