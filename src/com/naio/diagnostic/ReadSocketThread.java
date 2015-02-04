package com.naio.diagnostic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sourceforge.juint.UInt8;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

public class ReadSocketThread extends Thread {

	private NetClient netClient;
	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	private char[] bytes;
	private int memory;
	public ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private MemoryBuffer memoryBuffer;
	private int port;

	public ReadSocketThread(MemoryBuffer memoryBuffer,int port) {
		this.port = port;
		this.memoryBuffer = memoryBuffer;
		queue = new ConcurrentLinkedQueue<String>();
		this.stop = true;
		memory = 0;
	}

	public void run() {
		String message = "";
		int charsRead = 0;
		byte[] buffer = new byte[Config.BUFFER_SIZE];
		netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		try {
			while (this.stop) {

				if ((charsRead = netClient.getIn().read(buffer)) != -1) {
					memoryBuffer.addToFifo(buffer, charsRead);
				}else{
					try {
						Thread.sleep(0, 1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public String getOnePoll() {
		synchronized (lock1) {
			return queue.poll();
		}
	}

}
