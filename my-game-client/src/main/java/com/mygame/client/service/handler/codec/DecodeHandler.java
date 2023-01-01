package com.mygame.client.service.handler.codec;

import com.mygame.common.utils.CompressUtil;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端解码类
 */
public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(DecodeHandler.class);
    private String aesScreteKey;// 对称加密的密钥

    public void setAesScreteKey(String aesScreteKey) {
        this.aesScreteKey = aesScreteKey;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            // 依次读出
            int messageSize = buf.readInt();
            int clientSeqId = buf.readInt();
            int messageId = buf.readInt();
            long serverSendTime = buf.readLong();
            int version = buf.readInt();
            int compress = buf.readByte();
            int errorCode = buf.readInt();
            byte[] body = null;
            if (errorCode == 0 && buf.readableBytes() > 0) {// 读取包体数据
                body = new byte[buf.readableBytes()];// 剩下的字节都是body数据
                buf.readBytes(body);
//                if (this.aesScreteKey != null && messageId != 1) {// 如果对称加密 密钥不为null，对消息解密
//                    body = AESUtils.decode(aesScreteKey, body);
//                }
                if (compress == 1) {// 如果包体压缩了，接收时需要解压
                    body = CompressUtil.decompress(body);
                }
            }
            GameMessageHeader header = new GameMessageHeader();
            header.setClientSeqId(clientSeqId);
            header.setErrorCode(errorCode);
            header.setMessageId(messageId);
            header.setServerSendTime(serverSendTime);
            header.setVersion(version);
            header.setMessageSize(messageSize);

            GameMessagePackage gameMessagePackage = new GameMessagePackage();// 构造数据包
            gameMessagePackage.setHeader(header);
            gameMessagePackage.setBody(body);
            logger.debug("接收服务器消息,大小：{}:<-{}", messageSize, header);
            ctx.fireChannelRead(gameMessagePackage);// 往后传
        } finally {
            // 如果buf不是从堆内存分配，还是从直接内存中分配的，需要手动释放，否则，会造成内存泄露。
            ReferenceCountUtil.release(buf);
        }
    }
    
}
