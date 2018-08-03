package com.core.messages;

public class MessageAcceptConnection extends FIXMessage {
	private int		id;

	public MessageAcceptConnection(String messageType, int marketId, int id) {
		super(messageType, marketId);
		this.id = id;
		setChecksum("checksum");
	}

	public MessageAcceptConnection() {}

	public MessageAcceptConnection(FIXMessage copy) {
		super(copy);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"MSG_TYPE = '" + getMessageType() + "'" +
				"| ID = " + id +
				"| CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}
}
