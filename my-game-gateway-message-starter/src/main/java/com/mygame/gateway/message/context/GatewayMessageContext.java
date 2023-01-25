package com.mygame.gateway.message.context;


import com.mygame.db.entity.Player;
import com.mygame.game.common.IGameMessage;
import com.mygame.game.messagedispatcher.IGameChannelContext;

// 疑惑：这个类做什么用啊？？？？
public class GatewayMessageContext implements IGameChannelContext {
    @Override
    public void sendMessage(IGameMessage gameMessage) {
        
    }

    @Override
    public <T> T getRequest() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public long getPlayerId() {
        return 0;
    }
//    private IGameMessage requestMessage;
//    private AbstractGameChannelHandlerContext ctx;
//    @Deprecated
//    private Player player;// 这里的Player只是为了兼容前面的测试代码，在实例开发中，可以去掉这个参数
//    @Deprecated
//    private PlayerManager playerManager;// 这里是为了兼容前面的测试代码，在实际开发中，可以去掉
//    private T dataMaanger;
//
//    // 这里面的Player和PlayerManager参数是为了兼容前面的测试代码，在实际应用中可以去掉
//    public GatewayMessageContext(T dataManager, Player player, PlayerManager playerManager, IGameMessage requestMessage, AbstractGameChannelHandlerContext ctx) {
//        this.requestMessage = requestMessage;
//        this.ctx = ctx;
//        this.playerManager = playerManager;
//        this.player = player;
//        this.dataMaanger = dataManager;
//    }
//
//    public T getDataMaanger() {
//        return dataMaanger;
//    }
//
//    @Override
//    public void sendMessage(IGameMessage response) {
//        if (response != null) {
//            wrapResponseMessage(response);
//            ctx.writeAndFlush(response);
//        }
//    }
//    private void wrapResponseMessage(IGameMessage response) {
//        GameMessageHeader responseHeader = response.getHeader();
//        GameMessageHeader requestHeader = this.requestMessage.getHeader();
//        responseHeader.setClientSendTime(requestHeader.getClientSendTime());
//        responseHeader.setClientSeqId(requestHeader.getClientSeqId());
//        responseHeader.setPlayerId(requestHeader.getPlayerId());
//        responseHeader.setServerSendTime(System.currentTimeMillis());
//        responseHeader.setToServerId(requestHeader.getFromServerId());
//        responseHeader.setFromServerId(requestHeader.getToServerId());
//        responseHeader.setVersion(requestHeader.getVersion());
//    }
//    /**
//     * 将同一条消息广播给本服的所有人
//     * 疑惑：为什么不放到eventDispatchService？？？？？
//     *
//     */
//    public void broadcastMessage(IGameMessage message) {
//        if(message != null) {
//            ctx.gameChannel().getEventDispathService().broadcastMessage(message);
//        }
//    }
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
//    public <E> DefaultPromise<E> newPromise() {
//        return new DefaultPromise<>(ctx.executor());
//    }
//    public  DefaultPromise<IGameMessage> newRPCPromise() {
//        return new DefaultPromise<>(ctx.executor());
//    }
//
//    public Player getPlayer() {
//        return player;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <E> E getRequest() {
//        return (E) this.requestMessage;
//    }
//
//    @Override
//    public String getRemoteHost() {
//        return this.requestMessage.getHeader().getAttribute().getClientIp();
//    }
//
//
//
//    @Override
//    public long getPlayerId() {
//        return this.requestMessage.getHeader().getPlayerId();
//    }
//
//    public PlayerManager getPlayerManager() {
//        return playerManager;
//    }

}
