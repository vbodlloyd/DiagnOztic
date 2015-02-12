package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class TrameDecoder {

	public Trame decode(byte[] pollFifo) {
		
		if(pollFifo == null){
			return null;
		}
		switch (pollFifo[Config.LENGHT_HEADER]) {
		case Config.ID_GPS:
			return new GPSTrame(pollFifo);
		case Config.ID_LIDAR:
			return new LidarTrame(pollFifo);
		case Config.ID_ACCELERO:
			return new AcceleroTrame(pollFifo);
		case Config.ID_ACTUATOR:
			return new ActuatorTrame(pollFifo);
		case Config.ID_GYRO:
			return new GyroTrame(pollFifo);
		case Config.ID_LOG:
			return new LogTrame(pollFifo);
		case Config.ID_MOTORS:
		default:
			break;
		}
		
		return null;
				
	}

}
