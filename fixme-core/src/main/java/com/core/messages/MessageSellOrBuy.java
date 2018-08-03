package com.core.messages;

import com.core.MD5Creator;

public class MessageSellOrBuy extends FIXMessage {
	private int		id;
	private int		instrumentLength;
	private String	instrument;
	private int		quantity;
	private int		price;

	public MessageSellOrBuy(String messageType, int marketId, int id, String instrument, int quantity, int price) {
		super(messageType, marketId);
		this.id = id;
		this.instrument = instrument;
		this.quantity = quantity;
		this.price = price;
		setChecksum(getMsgMD5());
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
		setChecksum(getMsgMD5());
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
		instrumentLength = instrument.length();
	}

	public void setInstrumentLength(int instrumentLength) {
		this.instrumentLength = instrumentLength;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public String getMsgMD5() {
		return MD5Creator.createMD5FromObject(String.valueOf(id).concat(instrument).concat(String.valueOf(quantity)));
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"MSG_TYPE = '" + getMessageType() + "'" +
				"|ID = " + id +
				"|INSTRUMENT = '" + instrument + "'" +
				"|MARKET_ID = " + getMarketId() +
				"|QUANTITY = " + quantity +
				"|PRICE = " + price +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}
}
