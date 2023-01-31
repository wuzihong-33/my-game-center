package com.mygame.game.bus;

import com.alibaba.fastjson.JSON;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.game.common.HeaderAttribute;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class GameMessageInnerDecoder {
    private final static int HEADER_FIX_LEN = 60;

    /**
     * 将GameMessagePackage包转换成byte[]
     * @param gameMessagePackage
     * @return
     */
    public static byte[] sendMessage(GameMessagePackage gameMessagePackage) {
        int initialCapacity = HEADER_FIX_LEN;
        GameMessageHeader header = gameMessagePackage.getHeader();

        String headerAttJson = JSON.toJSONString(header.getAttribute());//把包头的属性类序列化为json
        byte[] headerAttBytes = headerAttJson.getBytes();
        initialCapacity += headerAttBytes.length;
        if (gameMessagePackage.getBody() != null) {
            initialCapacity += gameMessagePackage.getBody().length;
        }
        ByteBuf byteBuf =Unpooled.buffer(initialCapacity);//这里使用Unpooled创建ByteBuf，可以直接使用byteBuf.array();获取byte[]
        byteBuf.writeInt(initialCapacity);//依次写入包头的数据
        byteBuf.writeInt(header.getToServerId());
        byteBuf.writeInt(header.getFromServerId());
        byteBuf.writeInt(header.getSeqId());
        byteBuf.writeInt(header.getMessageId());
        byteBuf.writeInt(header.getServiceId());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeLong(header.getClientSendTime());
        byteBuf.writeLong(header.getServerSendTime());
        byteBuf.writeLong(header.getPlayerId());
        byteBuf.writeInt(headerAttBytes.length);
        byteBuf.writeBytes(headerAttBytes);
        byteBuf.writeInt(header.getErrorCode());
        byte[] value = null;
        if (gameMessagePackage.getBody() != null) {//写入包体信息
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(gameMessagePackage.getBody());//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
            ByteBuf allBuf = Unpooled.wrappedBuffer(byteBuf,bodyBuf);
            value = new byte[allBuf.readableBytes()];
            allBuf.readBytes(value);
        } else {
            value = byteBuf.array();
        }

        return value;
    }

    /**
     * 将byte[]转换成GameMessagePackage包
     * @param value
     * @return
     */
    public static GameMessagePackage readGameMessagePackage(byte[] value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(value);//直接使用byte[]包装为ByteBuf，减少一次数据复制
        //依次读取包头信息
        int messageSize = byteBuf.readInt();
        int toServerId = byteBuf.readInt();
        int fromServerId = byteBuf.readInt();
        int clientSeqId = byteBuf.readInt();
        int messageId = byteBuf.readInt();
        int serviceId = byteBuf.readInt();
        int version = byteBuf.readInt();
        long clientSendTime = byteBuf.readLong();
        long serverSendTime = byteBuf.readLong();
        long playerId = byteBuf.readLong();
        int headerAttrLength = byteBuf.readInt();
        HeaderAttribute headerAttr = null;
        if(headerAttrLength > 0) {//读取包头属性
            byte[] headerAttrBytes = new byte[headerAttrLength];
            byteBuf.readBytes(headerAttrBytes);
            String headerAttrJson = new String(headerAttrBytes);
            headerAttr = JSON.parseObject(headerAttrJson, HeaderAttribute.class);
        }
        int errorCode = byteBuf.readInt();
        byte[] body = null;
        if(byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        
        GameMessageHeader header = new GameMessageHeader();//向包头对象中添加数据
        header.setAttribute(headerAttr);
        header.setClientSendTime(clientSendTime);
        header.setSeqId(clientSeqId);
        header.setErrorCode(errorCode);
        header.setFromServerId(fromServerId);
        header.setMessageId(messageId);
        header.setMessageSize(messageSize);
        header.setPlayerId(playerId);
        header.setServerSendTime(serverSendTime);
        header.setServiceId(serviceId);
        header.setToServerId(toServerId);
        header.setVersion(version);
        GameMessagePackage gameMessagePackage = new GameMessagePackage();//创建消息对象
        gameMessagePackage.setHeader(header);
        gameMessagePackage.setBody(body);
        return gameMessagePackage;
    }
}
