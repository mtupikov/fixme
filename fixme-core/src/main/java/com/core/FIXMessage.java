package com.core;

public class FIXMessage {
	private int		typeLength;
	private String	messageType;
	private int		id;
	private int		instrumentLength;
	private String	instrument;
	private int		marketId;
	private int		price;
	private int		checksumLength;
	private String	checksum;

	public FIXMessage(String messageType, int id, String instrument, int marketId, int price, String checksum) {
		this.messageType = messageType;
		this.typeLength = messageType.length();
		this.id = id;
		this.instrument = instrument;
		this.instrumentLength = instrument.length();
		this.marketId = marketId;
		this.price = price;
		this.checksum = checksum;
		this.checksumLength = checksum.length();
	}

	public FIXMessage() {}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
		typeLength = messageType.length();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
		instrumentLength = instrument.length();
	}

	public int getMarketId() {
		return marketId;
	}

	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
		checksumLength = checksum.length();
	}

	public int getTypeLength() {
		return typeLength;
	}

	public int getInstrumentLength() {
		return instrumentLength;
	}

	public int getChecksumLength() {
		return checksumLength;
	}

	@Override
	public String toString() {
		return "FIX Message {" +
				"messageType = '" + messageType + '\'' +
				", id = '" + id + '\'' +
				", instrument = '" + instrument + '\'' +
				", marketId = '" + marketId + '\'' +
				", price = " + price +
				", checksum = '" + checksum + '\'' +
				'}';
	}
}
