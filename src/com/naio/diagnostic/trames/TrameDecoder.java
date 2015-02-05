package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class TrameDecoder {

	public Trame decode(byte[] pollFifo) {
		
		if(pollFifo == null){
			return null;
		}
		switch (pollFifo[6]) {
		case Config.ID_GPS:
			return new GPSTrame(pollFifo);
		case Config.ID_LIDAR:
			return new LidarTrame(pollFifo);
		case Config.ID_MOTORS:
		default:
			break;
		}
		return new Trame(pollFifo);
				
	}

}
