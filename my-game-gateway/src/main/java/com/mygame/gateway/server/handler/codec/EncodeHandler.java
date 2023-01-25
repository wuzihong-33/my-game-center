package com.mygame.gateway.server.handler.codec;
//import com.mygame.common.utils.AESUtils;
import com.mygame.common.utils.CompressUtil;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.gateway.server.GatewayServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.beans.factory.annotation.Autowired;

public class EncodeHandler extends MessageToByteEncoder<GameMessagePackage> {
    private static final int GAME_MESSAGE_HEADER_LEN = 29;
    @Autowired
    private GatewayServerConfig serverConfig;
    
    private String aesSecret;// 对称加密密钥

    public void setAesSecret(String aesSecret) {
        this.aesSecret = aesSecret;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, GameMessagePackage msg, ByteBuf out) throws Exception {
        int messageSize = GAME_MESSAGE_HEADER_LEN;
        byte[] body = msg.getBody();
        int compress = 0;
        if (body != null) {
            if (body.length >= serverConfig.getCompressMessageSize()) {
                body = CompressUtil.compress(body);
                compress = 1;
            }
            if (this.aesSecret != null && msg.getHeader().getMessageId() != 1) {
//                 body = AESUtils.encode(aesSecret, body);
            }
            messageSize += body.length;
        }
        out.writeInt(messageSize);
        GameMessageHeader header = msg.getHeader();
        out.writeInt(header.getClientSeqId());
        out.writeInt(header.getMessageId());
        out.writeLong(header.getServerSendTime());
        out.writeInt(header.getVersion());
        out.writeByte(compress);
        out.writeInt(header.getErrorCode());
        if (body != null) {
            out.writeBytes(body);
        }
    }
}
