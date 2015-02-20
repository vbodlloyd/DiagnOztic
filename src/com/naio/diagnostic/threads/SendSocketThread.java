package com.naio.diagnostic.threads;

import java.io.IOException;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.net.NetClient;

public class SendSocketThread extends Thread {

	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private byte[] bytes;
	private NetClient netClient;
	private int port;

	public SendSocketThread(byte[] bytes) {
		this.bytes = bytes;
		this.stop = true;
	}

	public SendSocketThread(int port) {
		this.stop = true;
		this.port = port;
	}

	public void setBytes(byte[] bytes) {
		synchronized (lock1) {
			this.bytes = bytes;
			lock1.notify();
		}
	}

	public void run() {
		netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		while (stop) {
			synchronized (lock1) {

				try {
					lock1.wait();
					if (netClient.getOut() != null) {
						netClient.getOut().write(bytes);
						Thread.sleep(10, 0);
					} else
						Thread.sleep(10, 0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(boolean stop) {
		synchronized (lock2) {
			this.stop = stop;
		}

	}

}
