package com.mygame.gateway.message.channel;

import com.mygame.game.common.GameMessagePackage;


public interface IMessageSendFactory {
    void sendMessage(GameMessagePackage gameMessagePackage, GameChannelPromise promise);
}
