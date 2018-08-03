package com.core.messages;

public class MessageSellOrBuy extends FIXMessage {
	private int		id;
	private int		instrumentLength;
	private String	instrument;
	private int		price;

	public MessageSellOrBuy(String messageType, int marketId, int id, String instrument, int price) {
		super(messageType, marketId);
		this.id = id;
		this.instrument = instrument;
		this.price = price;
		setChecksum("checksum");
	}

	public MessageSellOrBuy(FIXMessage copy) {
		super(copy);
	}

	public MessageSellOrBuy() {}

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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getInstrumentLength() {
		return instrumentLength;
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"| MSG_TYPE = '" + getMessageType() + "'" +
				"| ID = " + id +
				"| INSTRUMENT = '" + instrument + "'" +
				"| MARKET_ID = " + getMarketId() +
				"| PRICE = " + price +
				"| CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}
}
