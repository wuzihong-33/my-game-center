package com.mygame.game.message.xinyue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.message.xinyue.GetArenaPlayerListMsgResponse.ResponseBody;

@GameMessageMetadata(messageId = 203, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetArenaPlayerListMsgResponse extends AbstractJsonGameMessage<ResponseBody> {
    public static class ResponseBody {
        private List<ArenaPlayer> arenaPlayers;

        public List<ArenaPlayer> getArenaPlayers() {
            return arenaPlayers;
        }

        public void setArenaPlayers(List<ArenaPlayer> arenaPlayers) {
            this.arenaPlayers = arenaPlayers;
        }


    }
    public static class ArenaPlayer {
        private long playerId;
        private String nickName;
        private Map<String, String> heros = new HashMap<>();

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public Map<String, String> getHeros() {
            return heros;
        }

        public void setHeros(Map<String, String> heros) {
            this.heros = heros;
        }

    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
