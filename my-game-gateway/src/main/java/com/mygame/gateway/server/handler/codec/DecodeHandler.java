package com.mygame.gateway.server.handler.codec;

import com.mygame.common.utils.CompressUtil;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 协议格式：
     * 消息总长度(int 4) 
     * 长度检验码(int 4) 
     * 消息序列号(int 4) 
     * 消息号(int 4) 
     * 服务ID(2)
     * 客户端发送时间(long 8)
     * 版本号(int 4) 
     * 是否压缩(byte 1) 
     * body(变长)
 */
public class DecodeHandler extends ChannelInboundHandlerAdapter {
    private String aesSecret;//对称加密密钥
    
    public void setAesSecret(String aesSecret) {
        this.aesSecret = aesSecret;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            int messageSize = byteBuf.readInt();
            int clientSeqId = byteBuf.readInt();
            int messageId = byteBuf.readInt();
            int serviceId = byteBuf.readShort();
            long clientSendTime = byteBuf.readLong();
            int version = byteBuf.readInt();
            int compress = byteBuf.readByte();
            byte[] body = null;
            if (byteBuf.readableBytes() > 0) {
                body = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(body);
                if(this.aesSecret != null && messageId != 1) {//如果密钥不为空，且不是认证消息，对消息体解密
//                    body = AESUtils.decode(aesSecret, body);
                }
                if (compress == 1) {
                    body = CompressUtil.decompress(body);
                }
            }
            GameMessageHeader header = new GameMessageHeader();
            header.setClientSendTime(clientSendTime);
            header.setClientSeqId(clientSeqId);
            header.setMessageId(messageId);
            header.setServiceId(serviceId);
            header.setMessageSize(messageSize);
            header.setVersion(version);
            GameMessagePackage gameMessagePackage = new GameMessagePackage();
            gameMessagePackage.setHeader(header);
            gameMessagePackage.setBody(body);
            
            // 解码之后往后传
            ctx.fireChannelRead(gameMessagePackage);
        } finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
