package com.naio.diagnostic;

import java.nio.ByteBuffer;
import java.util.Date;

public class GPSTrame extends Trame {

	private byte[] time = new byte[8];
	private byte[] lat = new byte[8];
	private byte[] lon = new byte[8];
	private byte[] alt = new byte[8];

	private byte unit;
	private byte numberOfSat;
	private byte quality;
	private byte[] groundSpeed = new byte[8];
	private boolean instantiate;

	public GPSTrame(byte[] naio01, byte id, byte[] size, byte[] payload,
			byte[] checksum) {
		super(naio01, id, size, payload, checksum);

	}

	public GPSTrame(byte[] data) {
		super(data);
		if (data == null) {
			return;
		}
		instantiate = true;
		time = new byte[] { data[18], data[17], data[16], data[15], data[14],
				data[13], data[12], data[11] };
		lat = new byte[] { data[26], data[25], data[24], data[23], data[22],
				data[21], data[20], data[19] };
		lon = new byte[] { data[34], data[33], data[32], data[31], data[30],
				data[29], data[28], data[27] };
		alt = new byte[] { data[42], data[41], data[40], data[39], data[38],
				data[37], data[36], data[35] };
		unit = data[43];
		numberOfSat = data[44];
		quality = data[45];
		groundSpeed = new byte[] { data[53], data[52], data[51], data[50],
				data[49], data[48], data[47], data[46] };

	}

	public String show() {
		if (instantiate) {
			Date date = new Date((long) ((ByteBuffer.wrap(time).getDouble(0))));
			return "" + date.toString() + "___lat:"
					+ ByteBuffer.wrap(lat).getDouble(0) + "___lon:"
					+ ByteBuffer.wrap(lon).getDouble(0) + "__alt:"
					+ ByteBuffer.wrap(alt).getDouble(0) + "__unit:" + unit
					+ "___nbrSat:" + numberOfSat + "___quality:" + quality
					+ "___groundSpeed:"
					+ ByteBuffer.wrap(groundSpeed).getDouble(0);
		}
		return null;
	}

	/**
	 * @return the time
	 */
	public byte[] getTime() {
		return time;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return ByteBuffer.wrap(lat).getDouble(0);
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return ByteBuffer.wrap(lon).getDouble(0);
	}

	/**
	 * @return the alt
	 */
	public byte[] getAlt() {
		return alt;
	}

	/**
	 * @return the unit
	 */
	public byte getUnit() {
		return unit;
	}

	/**
	 * @return the quality
	 */
	public byte getQuality() {
		return quality;
	}

	/**
	 * @return the groundSpeed
	 */
	public byte[] getGroundSpeed() {
		return groundSpeed;
	}
}
