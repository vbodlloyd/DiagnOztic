package com.naio.diagnostic.trames;

public class Trame {
	private byte[] naio01 = new byte[6];
	private byte id ;
	private byte[] size = new byte[4];
	private byte[] payload;
	private byte[] checksum = new byte[4];
	
	public Trame(byte[] naio01, byte id, byte[] size, byte[] payload, byte[] checksum) {
		super();
		this.naio01 = naio01;
		this.id = id;
		this.size = size;
		this.payload = payload;
		this.checksum = checksum;
	}
	
	public Trame(byte[] data){
		
	}
	
	/**
	 * @return the naio01
	 */
	public byte[] getNaio01() {
		return naio01;
	}
	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}
	/**
	 * @return the size
	 */
	public byte[] getSize() {
		return size;
	}
	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return payload;
	}

	public byte[] getChecksum() {
		return checksum;
	}

	public String show() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
