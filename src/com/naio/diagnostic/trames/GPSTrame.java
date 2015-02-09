package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import java.util.Date;

import com.naio.diagnostic.utils.Config;


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
		int offset = Config.LENGHT_FULL_HEADER;
		time = new byte[] { data[offset+7], data[offset+6], data[offset+5], data[offset+4], data[offset+3],
				data[offset+2], data[offset+1], data[offset] };
		lat = new byte[] { data[offset+15], data[offset+14], data[offset+13], data[offset+12], data[offset+11],
				data[offset+10], data[offset+9], data[offset+8] };
		lon = new byte[] { data[offset+23], data[offset+22], data[offset+21], data[offset+20], data[offset+19],
				data[offset+18], data[offset+17], data[offset+16] };
		alt = new byte[] { data[offset+31], data[offset+30], data[offset+29], data[offset+28], data[offset+27],
				data[offset+26], data[offset+25], data[offset+24] };
		unit = data[offset+32];
		numberOfSat = data[offset+33];
		quality = data[offset+34];
		groundSpeed = new byte[] { data[offset+42], data[offset+41], data[offset+40], data[offset+39],
				data[offset+38], data[offset+37], data[offset+36], data[offset+35] };

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
	public double getAlt() {
		return ByteBuffer.wrap(alt).getDouble(0);
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
	public double getGroundSpeed() {
		return ByteBuffer.wrap(groundSpeed).getDouble(0);
	}
}
