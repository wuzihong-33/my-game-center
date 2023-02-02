package com.mygame.xinyue.logic.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.db.entity.Player;
import com.mygame.db.entity.manager.PlayerManager;
import com.mygame.game.common.IGameMessage;
import com.mygame.game.message.xinyue.BuyArenaChallengeTimesMsgRequest;
import com.mygame.game.message.xinyue.EnterGameMsgRequest;
import com.mygame.game.message.xinyue.EnterGameMsgResponse;
import com.mygame.game.message.xinyue.GetArenaPlayerListMsgRequest;
import com.mygame.game.message.xinyue.GetArenaPlayerListMsgResponse;
import com.mygame.game.message.xinyue.GetArenaPlayerListMsgResponse.ArenaPlayer;
import com.mygame.game.message.xinyue.GetPlayerByIdMsgRequest;
import com.mygame.game.message.xinyue.GetPlayerByIdMsgResponse;
import com.mygame.game.message.xinyue.rpc.ConsumeDiamondMsgRequest;
import com.mygame.game.message.xinyue.rpc.ConsumeDiamondMsgResponse;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.game.messagedispatcher.GameMessageMapping;
import com.mygame.gateway.message.context.GatewayMessageContext;
import com.mygame.gateway.message.context.UserEvent;
import com.mygame.gateway.message.context.UserEventContext;
//import com.mygame.xinyue.logic.event.GetArenaPlayerEvent;
//import com.mygame.xinyue.logic.event.GetPlayerInfoEvent;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

@GameMessageHandler
public class PlayerLogicHandler {
    private Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

    @UserEvent(IdleStateEvent.class)
    public void idleStateEvent(UserEventContext<PlayerManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        logger.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

//    @UserEvent(GetPlayerInfoEvent.class)
//    public void getPlayerInfoEvent(UserEventContext<PlayerManager> ctx, GetPlayerInfoEvent event, Promise<Object> promise) {
//        GetPlayerByIdMsgResponse response = new GetPlayerByIdMsgResponse();
//        Player player = ctx.getDataManager().getPlayer();
//        response.getBodyObj().setPlayerId(player.getPlayerId());
//        response.getBodyObj().setNickName(player.getNickName());
//        Map<String, String> heros = new HashMap<>();
//        player.getHeros().forEach((k, v) -> {// 复制处理一下，防止对象安全溢出。
//            heros.put(k, v);
//        });
//        // response.getBodyObj().setHeros(this.player.getHeros());不要使用这种方式，它会把这个map传递到其它线程
//        response.getBodyObj().setHeros(heros);
//        promise.setSuccess(response);
//    }

    @GameMessageMapping(EnterGameMsgRequest.class)
    public void enterGame(EnterGameMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        logger.info("接收到客户端进入游戏请求：{}", request.getHeader().getPlayerId());
        EnterGameMsgResponse response = new EnterGameMsgResponse();
        response.getBodyObj().setNickname("叶孤城");
        response.getBodyObj().setPlayerId(1);
        ctx.sendMessage(response);
    }


//    @GameMessageMapping(GetPlayerByIdMsgRequest.class)
//    public void getPlayerById(GetPlayerByIdMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
//        long playerId = request.getBodyObj().getPlayerId();
//        DefaultPromise<Object> promise = ctx.newPromise();
//        GetPlayerInfoEvent event = new GetPlayerInfoEvent(playerId);
//        ctx.sendUserEvent(event, promise, playerId).addListener(new GenericFutureListener<Future<? super Object>>() {
//            @Override
//            public void operationComplete(Future<? super Object> future) throws Exception {
//                if (future.isSuccess()) {
//                    GetPlayerByIdMsgResponse response = (GetPlayerByIdMsgResponse) future.get();
//                    ctx.sendMessage(response);
//                } else {
//                    logger.error("playerId {} 数据查询失败", playerId, future.cause());
//                }
//            }
//        });
//    }

    
    /**
     * 购买竞技场挑战次数（需要消耗一定的钻石数）
     * @param request
     * @param ctx
     */
    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class)
    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        ConsumeDiamondMsgRequest consumeDiamondMsgRequest = new ConsumeDiamondMsgRequest();
        Promise<IGameMessage> promise = ctx.newPromise();
        promise.addListener(new GenericFutureListener<Future<IGameMessage>>() {
            @Override
            public void operationComplete(Future<IGameMessage> future) throws Exception {
                if (future.isSuccess()) {
                    ConsumeDiamondMsgResponse rpcResponse = (ConsumeDiamondMsgResponse) future.get();
                    if(rpcResponse.getHeader().getErrorCode() == 0) {
                        // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                    }
                } else {
                    logger.error("竞技场扣除钻石失败",future.cause());
                    //向客户端返回错误码
                }
            }
        });
        // 向核心服发送扣除钻石数、购买挑战次数的rpc请求
//        ctx.sendRPCMessage(consumeDiamondMsgRequest, promise);
    }


    private List<Long> getAreanPlayerIdList() {
        return Arrays.asList(2L, 3L, 4L);// 模拟竞技场列表playerId
    }

//    @GameMessageMapping(GetArenaPlayerListMsgRequest.class)
//    public void getArenaPlayerList(GetArenaPlayerListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
//        List<Long> playerIds = this.getAreanPlayerIdList();// 获取本次要显示的PlayerId
//        List<ArenaPlayer> arenaPlayers = new ArrayList<>(playerIds.size());
//        playerIds.forEach(playerId -> {// 遍历所有的PlayerId，向他们对应的GameChannel发送查询事件
//            GetArenaPlayerEvent getArenaPlayerEvent = new GetArenaPlayerEvent(playerId);
//            Promise<Object> promise = ctx.newPromise();// 注意，这个promise不能放到for循环外面，一个Promise只能被setSuccess一次。
//            ctx.sendUserEvent(getArenaPlayerEvent, promise, playerId).addListener(new GenericFutureListener<Future<? super Object>>() {
//                @Override
//                public void operationComplete(Future<? super Object> future) throws Exception {
//                    if (future.isSuccess()) {// 如果执行成功，获取执行的结果
//                        ArenaPlayer arenaPlayer = (ArenaPlayer) future.get();
//                        arenaPlayers.add(arenaPlayer);
//                    } else {
//                        arenaPlayers.add(null);
//                    }
//                    if (arenaPlayers.size() == playerIds.size()) {// 如果数量相等，说明所有的事件查询都已执行成功，可以返回给客户端数据了。
//                        List<ArenaPlayer> result = arenaPlayers.stream().filter(c -> c != null).collect(Collectors.toList());
//                        GetArenaPlayerListMsgResponse response = new GetArenaPlayerListMsgResponse();
//                        response.getBodyObj().setArenaPlayers(result);
//                        ctx.sendMessage(response);
//                    }
//                }
//            });
//        });
//    }
//
//    @UserEvent(GetArenaPlayerEvent.class)
//    public void getArenaPlayer(UserEventContext<PlayerManager> utx, GetArenaPlayerEvent event, Promise<Object> promise) {
//        ArenaPlayer arenaPlayer = new ArenaPlayer();
//        Player player = utx.getDataManager().getPlayer();
//        arenaPlayer.setPlayerId(player.getPlayerId());
//        arenaPlayer.setNickName(player.getNickName());
//        Map<String, String> heros = new HashMap<>();
//        player.getHeros().forEach((k, v) -> {// 复制处理一下，防止对象安全溢出。
//            heros.put(k, v);
//        });
//        arenaPlayer.setHeros(heros);
//    }
    
    
    
}
