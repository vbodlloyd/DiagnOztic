package com.naio.diagnostic.trames;

import android.util.Log;

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
		case Config.ID_ODO:
			return new OdoTrame(pollFifo);
		case Config.ID_MOTORS:
			break;
		case Config.ID_REMOTE :
			return new RemoteTrame(pollFifo);
		case Config.ID_KEYPAD :
			return new KeyPadTrame(pollFifo);
		case Config.ID_SPEAKER :
			return new SpeakerTrame(pollFifo);
		case Config.ID_LED :
			return new LedTrame(pollFifo);
		case Config.ID_SCREEN :
			return new ScreenTrame(pollFifo);
		case Config.ID_MAGNETO :
			return new MagnetoTrame(pollFifo);
		case Config.ID_WATCHDOG :
			return new WatchdogTrame(pollFifo);
		default:
			break;
		}
		
		return null;
				
	}

}
