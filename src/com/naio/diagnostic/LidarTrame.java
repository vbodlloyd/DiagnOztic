package com.naio.diagnostic;

import java.util.Arrays;

import net.sourceforge.juint.UInt16;
import net.sourceforge.juint.UInt8;

public class LidarTrame extends Trame {

	private byte[] distance = new byte[542];
	private byte[] albedo = new byte[271];

	private boolean instantiate;

	public LidarTrame(byte[] naio01, byte id, byte[] size, byte[] payload,
			byte[] checksum) {
		super(naio01, id, size, payload, checksum);

	}

	public LidarTrame(byte[] data) {
		super(data);
		if (data == null) {
			return;
		}
		instantiate = true;
		distance = Arrays.copyOf(data, 542);
		albedo = Arrays.copyOfRange(data, 542, data.length
				- Config.LENGHT_CHECKSUM);
	}

	public String show() {
		if (instantiate) {
			String txt = "";
			String txt2 = "";
			for (int i = 0; i < 542; i = i + 2) {
				txt += UInt16.valueOfLittleEndian(
						new byte[] { distance[i], distance[i + 1] }).toString()
						+ ",";
				txt2 += (new UInt8(albedo[i / 2])).toString() + ",";
			}
			return "distance :" + txt + "  albedo:" + txt2;
		}
		return null;
	}

	public UInt16[] data_uint16() {
		UInt16[] uint = new UInt16[180];
		for (int i = 50; i < 230; i++) {

			uint[179 - (i - 50)] = UInt16.valueOfLittleEndian(new byte[] {
					(new UInt8(distance[i * 2])).byteValue(),
					(new UInt8(distance[i * 2 - 1]).byteValue()) });
		}
		return uint;
	}
}