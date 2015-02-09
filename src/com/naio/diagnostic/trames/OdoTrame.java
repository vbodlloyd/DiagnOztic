package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class OdoTrame extends Trame {
	private byte frontRight;
	private byte rearRight;
	private byte rearLeft;
	private byte frontLeft;
	private boolean instantiate;

	public OdoTrame(byte[] data) {
		super(data);
		frontRight = data[Config.LENGHT_FULL_HEADER];
		rearRight = data[Config.LENGHT_FULL_HEADER + 1];
		rearLeft = data[Config.LENGHT_FULL_HEADER + 2];
		frontLeft = data[Config.LENGHT_FULL_HEADER + 3];
		instantiate = true;
	}

	public String show() {
		if (instantiate) {
			return "front right:" + frontRight + " rear right:" + rearRight
					+ " rear left:" + rearLeft + " front left:" + frontLeft;
		}
		return null;
	}
}
