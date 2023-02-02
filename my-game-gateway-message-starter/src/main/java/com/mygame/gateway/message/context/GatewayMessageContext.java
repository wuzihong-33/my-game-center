package com.mygame.gateway.message.context;


import com.mygame.db.entity.Player;
import com.mygame.db.entity.manager.PlayerManager;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.IGameMessage;
import com.mygame.game.messagedispatcher.IGameChannelContext;
import com.mygame.gateway.message.channel.AbstractGameChannelHandlerContext;
import io.netty.util.concurrent.DefaultPromise;

// 疑惑：这个类做什么用啊？？？？

/**
 * 对AbstractGameChannelHandlerContext和IGameMessage的包装
 * 业务处理方法参数固定形式：参数1：IGameMessage；参数2：GatewayMessageContext
 * 类似于netty inbound Handler的：void channelRead(ChannelHandlerContext ctx, Object msg)
 * 对于netty来说，pipeline的传递是通过ChannelHandlerContext来实现的，fireXxx向后传，writeXxx写消息
 * ChannelHandlerContext包装了handler，以及pipeline，而pipeline持有了channel
 */
public class GatewayMessageContext<T> implements IGameChannelContext {
    private IGameMessage requestMessage;
    private AbstractGameChannelHandlerContext ctx; 
    private T dataManager;
//
//    // 这里面的Player和PlayerManager参数是为了兼容前面的测试代码，在实际应用中可以去掉
    
    public GatewayMessageContext(T dataManager, IGameMessage requestMessage, AbstractGameChannelHandlerContext ctx) {
        this.requestMessage = requestMessage;
        this.ctx = ctx;
        this.dataManager = dataManager;
    }
//
//    public T getDataMaanger() {
//        return dataMaanger;
//    }
//

    /**
     * 向通道写响应
     * @param response
     */
    @Override
    public void sendMessage(IGameMessage response) {
        if (response != null) {
            wrapResponseMessage(response);
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 往通道发送广播请求
     * @param message
     */
//    public void broadcastMessage(IGameMessage message) {
//        if(message != null) {
//            ctx.gameChannel().getEventDispathService().broadcastMessage(message);
//        }
//    }
//    
//    public void broadcastMessage(IGameMessage message,long...playerIds) {
//        ctx.gameChannel().getEventDispathService().broadcastMessage(message,playerIds);
//    }
//
//    public  Future<IGameMessage> sendRPCMessage(IGameMessage rpcRequest, Promise<IGameMessage> callback) {
//        if (rpcRequest != null) {
//            rpcRequest.getHeader().setPlayerId(ctx.gameChannel().getPlayerId());
//            ctx.writeRPCMessage(rpcRequest, callback);
//        } else {
//            throw new NullPointerException("RPC消息不能为空");
//        }
//        return callback;
//    }
//
//    
//    public void sendRPCMessage(IGameMessage rpcRequest) {
//        if (rpcRequest != null) {
//            ctx.writeRPCMessage(rpcRequest, null);
//        } else {
//            throw new NullPointerException("RPC消息不能为空");
//        }
//    }
//
//
//    public Future<Object> sendUserEvent(Object event, Promise<Object> promise, long playerId) {
//        ctx.gameChannel().getEventDispathService().fireUserEvent(playerId, event, promise);
//        return promise;
//    }
//
//
    
    public <E> DefaultPromise<E> newPromise() {
        return new DefaultPromise<>(ctx.executor());
    }

    public DefaultPromise<IGameMessage> newRPCPromise() {
        return new DefaultPromise<>(ctx.executor());
    }



    @SuppressWarnings("unchecked")
    @Override
    public <E> E getRequest() {
        return (E) this.requestMessage;
    }

    @Override
    public String getRemoteHost() {
        return this.requestMessage.getHeader().getAttribute().getClientIp();
    }

    @Override
    public long getPlayerId() {
        return this.requestMessage.getHeader().getPlayerId();
    }
//
//    public PlayerManager getPlayerManager() {
//        return PlayerManager;
//    }

    private void wrapResponseMessage(IGameMessage response) {
        GameMessageHeader responseHeader = response.getHeader();
        GameMessageHeader requestHeader = this.requestMessage.getHeader();
        
        responseHeader.setClientSendTime(requestHeader.getClientSendTime());
        responseHeader.setClientSeqId(requestHeader.getClientSeqId());
        responseHeader.setPlayerId(requestHeader.getPlayerId());
        responseHeader.setServerSendTime(System.currentTimeMillis());
        responseHeader.setToServerId(requestHeader.getFromServerId());
        responseHeader.setFromServerId(requestHeader.getToServerId());
        responseHeader.setVersion(requestHeader.getVersion());
    }
}
