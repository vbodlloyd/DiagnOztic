package com.naio.diagnostic.utils;

import com.naio.diagnostic.threads.SendSocketThread;
import com.naio.views.AnalogueView.OnMoveListener;

public class MyMoveListenerForAnalogueView implements OnMoveListener {
	private SendSocketThread sendSocketThreadMotors;
	private byte left;
	private byte right;
	private static final Object lock = new Object();

	public MyMoveListenerForAnalogueView(SendSocketThread sendSocketThreadMotors) {
		this.sendSocketThreadMotors = sendSocketThreadMotors;
	}

	public void sendMotorsCommand() {
		synchronized (lock) {

			byte[] b = new byte[] { 78, 65, 73, 79, 48, 49, 1, 0, 0, 0, 2,
					left, right, 0, 0, 0, 0 };
			sendSocketThreadMotors.setBytes(b);
		}
	}

	@Override
	public void onMaxMoveInDirection(int padDiff, int padSpeed) {
		int bearing = padDiff * 127 / 180;
		byte xa = 0;
		byte ya = 0;
		if (padSpeed >= 0) {
			if (padSpeed + bearing > 127)
				xa = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					xa = (byte) -127;
				else
					xa = (byte) (padSpeed + bearing);
			}

			if (padSpeed - bearing < -127)
				ya = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					ya = (byte) 127;
				else
					ya = (byte) (padSpeed - bearing);
			}

		} else {
			if (padSpeed - bearing < -127)
				xa = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					xa = (byte) 127;
				else
					xa = (byte) (padSpeed - bearing);
			}
			if (padSpeed + bearing > 127)
				ya = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					ya = (byte) -127;
				else
					ya = (byte) (padSpeed + bearing);
			}
		}
		synchronized (lock) {

			left = xa;
			right = ya;
		}

	}

	@Override
	public void onHalfMoveInDirection(int padDiff, int padSpeed) {
		int bearing = padDiff * 127 / 180;
		byte xa = 0;
		byte ya = 0;
		if (padSpeed >= 0) {
			if (padSpeed + bearing > 127)
				xa = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					xa = (byte) -127;
				else
					xa = (byte) (padSpeed + bearing);
			}

			if (padSpeed - bearing < -127)
				ya = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					ya = (byte) 127;
				else
					ya = (byte) (padSpeed - bearing);
			}

		} else {
			if (padSpeed - bearing < -127)
				xa = (byte) -127;
			else {
				if (padSpeed - bearing > 127)
					xa = (byte) 127;
				else
					xa = (byte) (padSpeed - bearing);
			}
			if (padSpeed + bearing > 127)
				ya = (byte) 127;
			else {
				if (padSpeed + bearing < -127)
					ya = (byte) -127;
				else
					ya = (byte) (padSpeed + bearing);
			}
		}
		synchronized (lock) {

			left = xa;
			right = ya;
		}

	}

}
