package com.core.messages;

import com.core.MD5Creator;

public class MessageExecuteOrReject extends FIXMessage {
	private int		actionLength;
	private String	messageAction;
	private int		id;

	public MessageExecuteOrReject(String messageType, int marketId, String messageAction, int id) {
		super(messageType, marketId);
		this.messageAction = messageAction;
		this.id = id;
		setChecksum(getMsgMD5());
	}

	public MessageExecuteOrReject(FIXMessage copy) {
		super(copy);
	}

	public MessageExecuteOrReject() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		setChecksum(getMsgMD5());
	}

	public int getActionLength() {
		return actionLength;
	}

	public String getMessageAction() {
		return messageAction;
	}

	public void setMessageAction(String messageAction) {
		this.messageAction = messageAction;
		actionLength = messageAction.length();
	}

	public String getMsgMD5() {
		return MD5Creator.createMD5FromObject(String.valueOf(id).concat(messageAction));
	}

	@Override
	public String toString() {
		return "MessageSellOrBuy {" +
				"MSG_TYPE = '" + getMessageType() + "'" +
				"|MSG_ACTION = '" + messageAction + "'" +
				"|ID = " + id +
				"|MARKET_ID = " + getMarketId() +
				"|CHECKSUM = '" + getChecksum() + "'" +
				'}';
	}
}
