package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class ActuatorTrame extends Trame {

	private byte value;
	private boolean instantiate;

	public ActuatorTrame(byte[] data) {
		super(data);
		value = data[Config.LENGHT_FULL_HEADER];
		instantiate = true;
	}

	public String show() {
		if (instantiate) {
			return "value:" + value;
		}
		return null;
	}

}
