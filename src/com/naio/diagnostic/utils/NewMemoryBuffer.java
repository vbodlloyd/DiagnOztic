package com.naio.diagnostic.utils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import net.sourceforge.juint.Int32;

/**
 * Garde en mémoire la fin d'une trame non fini et la complete avec la suivante.
 * La classe contient une fifo contennant ces trames.
 * 
 * @author bodereau
 * 
 */
public class NewMemoryBuffer {
	public ConcurrentLinkedQueue<byte[]> fifo = new ConcurrentLinkedQueue<byte[]>();

	private byte[] memoryBytes = new byte[] {};

	private int payloadSize;

	private int CurrentBufferPos;
	private byte[] WorkingBuffer;

	private int CurrentMaxPacketSize;

	private int CurrentPacketSize;

	public NewMemoryBuffer() {
		CurrentBufferPos = 0;
		WorkingBuffer = new byte[1024 * 1024 * 3];
		CurrentPacketSize = 0;
	}

	public void addToFifo(byte[] bytes, int bytesRead) {
		
		//byte[] bytes = bytess.clone();
		//Log.e("index","iamhere");
		try {
			int idx = 0;
			
			while (idx < bytesRead) {
				this.WorkingBuffer[this.CurrentBufferPos] = bytes[idx];
				//Log.e("index",""+idx + "  read:"+bytesRead);
				// TEST PROTOCOL VERSION
				if (this.CurrentBufferPos == 5) {
					if (!ValidateProtocol(0, this.WorkingBuffer)) {
						for (int shiftIdx = 0; shiftIdx <= this.CurrentBufferPos; shiftIdx++) {
							this.WorkingBuffer[shiftIdx] = this.WorkingBuffer[shiftIdx + 1];
						}

						this.CurrentBufferPos--;
					}
				}
				// REACH PROTOCOL PACKET ID
				else if (this.CurrentBufferPos == 6) {
					// NOTHING
					byte packetId = GetPacketId(0, this.WorkingBuffer);

					this.CurrentMaxPacketSize = GetMaxPacketLength(packetId);
				}
				//
				else if (this.CurrentBufferPos == 11) {
					byte[] size = new byte[] { this.WorkingBuffer[7],
							this.WorkingBuffer[8], this.WorkingBuffer[9],
							this.WorkingBuffer[10] };
					this.CurrentPacketSize = ByteBuffer.wrap(size).getInt();

					// TODO:Trouble !!!!!!
					if (this.CurrentPacketSize > this.CurrentMaxPacketSize) {
						this.CurrentPacketSize = this.CurrentMaxPacketSize;
					}
				}
				// ALL PACKET RECEIVED TRY TO DECODE
				else if (this.CurrentBufferPos == Config.LENGHT_FULL_HEADER
						+ this.CurrentPacketSize + Config.LENGHT_CHECKSUM - 1) {
					fifo.offer(Arrays.copyOfRange(this.WorkingBuffer, 0,
							this.CurrentBufferPos));
					DataManager.getInstance().write_in_log("paquet finish : "+ this.CurrentBufferPos);

					this.CurrentBufferPos = -1;
				}

				this.CurrentBufferPos++;
				idx++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public byte[] getPollFifo() {
		for (int i = 0; i < fifo.size() - 1; i++) {
			fifo.poll();
		}
		return fifo.peek();
	}

	private static int GetMaxPacketLength(byte packetId) {
		int maxPacketLenght = 0;

		switch (packetId) {
		case (byte) Config.ID_MOTORS:
			maxPacketLenght = 2;
			break;
		case (byte) Config.ID_LOG:
			maxPacketLenght = 2048;
			break;
		/*
		 * case (byte)PacketIds.WatchDogPacketId: maxPacketLenght = (uint)32;
		 * break;
		 */
		case (byte) Config.ID_GPS:
			maxPacketLenght = Config.LENGHT_TRAME_GPS;
			break;
		case (byte) Config.ID_LIDAR:
			maxPacketLenght = Config.LENGHT_TRAME_LIDAR;
			break;
		/*
		 * case (byte)PacketIds.RemotePacketId: maxPacketLenght = (uint)(15);
		 * break; case (byte)PacketIds.CameraPacketId: maxPacketLenght =
		 * (uint)(748*480*3+32); break; case (byte)PacketIds.OdoPacketId:
		 * maxPacketLenght = (uint)(4); break; case
		 * (byte)PacketIds.MagnetoPacketId: maxPacketLenght = (uint)(3*2);
		 * break; case (byte)PacketIds.GyroPacketId: maxPacketLenght = (uint)(3
		 * * 2); break; case (byte)PacketIds.AcceleroPacketId: maxPacketLenght =
		 * (uint)(3 * 2); break;
		 */
		case (byte) Config.ID_ACTUATOR:
			maxPacketLenght = Config.LENGHT_TRAME_ACTUATOR;
			break;

		/*
		 * case (byte)PacketIds.KeypadPacketId: maxPacketLenght = (uint)(1);
		 * break; case (byte)PacketIds.LedPacketId: maxPacketLenght = (uint)(1);
		 * break; case (byte)PacketIds.ScreenPacketId: maxPacketLenght =
		 * (uint)(32); break; case (byte)PacketIds.SpeakerPacketId:
		 * maxPacketLenght = (uint)(6); break;
		 */
		}

		return maxPacketLenght;
	}

	private static byte GetPacketId(int start, byte[] buffer) {
		byte messageId = 0;
		
		if (buffer.length >= (7 + start)) {
			messageId = buffer[6];
		}
		DataManager.getInstance().write_in_log("id du paquet : "+ messageId);
		return messageId;
	}

	private static Boolean ValidateProtocol(int start, byte[] buffer) {
		Boolean isValid = false;

		if (buffer.length > (5 + start)) {
			int mem = 0, idx = 0;
			Byte[] receiveProtocolVersion = new Byte[] { buffer[0 + start],
					buffer[1 + start], buffer[2 + start], buffer[3 + start],
					buffer[4 + start], buffer[5 + start] };
			for (byte bit : receiveProtocolVersion) {
				if (bit == 78) {
					mem = 1;
				} else if (bit == 65 && mem == 1) {
					mem = 2;
				} else if (bit == 73 && mem == 2) {
					mem = 3;
				} else if (bit == 79 && mem == 3) {
					mem = 4;
					return true;
				} else {
					mem = 0;
				}
				idx++;
			}

		}

		return isValid;
	}

}
