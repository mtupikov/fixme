package com.core.decoders;

import com.core.messages.MessageSellOrBuy;
import com.core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class SellOrBuyDecoder extends ReplayingDecoder<MessageSellOrBuy> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		MessageSellOrBuy msg = new MessageSellOrBuy();
		msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
		if (msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
			msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString())) {
			msg.setId(in.readInt());
			msg.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
			msg.setMarketId(in.readInt());
			msg.setQuantity(in.readInt());
			msg.setPrice(in.readInt());
			msg.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
			out.add(msg);
		}
	}
}

