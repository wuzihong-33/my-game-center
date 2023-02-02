package com.mygame.xinyue.logic.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.db.entity.manager.ArenaManager;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import com.mygame.gateway.message.rpc.RPCEvent;
import com.mygame.gateway.message.rpc.RPCEventContext;

@GameMessageHandler
public class RPCBusinessHandler {
    private Logger logger = LoggerFactory.getLogger(RPCBusinessHandler.class);
//    @RPCEvent(ConsumeDiamondRPCRequest.class)
//    public void consumDiamond(RPCEventContext<ArenaManager> ctx,ConsumeDiamondRPCRequest request) {
//         logger.debug("收到扣钻石的rpc请求");
//         ConsumeDiamonRPCResponse response = new ConsumeDiamonRPCResponse();
//         ctx.sendResponse(response);
//    }
}
