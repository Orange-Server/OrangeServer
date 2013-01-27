/**
 * OrangeServer - Package: net.orange_server.orangeserver.player
 * Created: 2013/01/01 23:18:45
 */
package net.orange_server.orangeserver.player;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

/**
 * PlayerManager (PlayerManager.java)
 * @author syam(syamn)
 */
public class PlayerManager {
    private static ConcurrentHashMap<String, OrangePlayer> players = new ConcurrentHashMap<String, OrangePlayer>();
    
    public static OrangePlayer addPlayer(final Player player){
        OrangePlayer sPlayer = players.get(player.getName());
        
        if (sPlayer != null){
            sPlayer.setPlayer(player);
        }else{
            sPlayer = new OrangePlayer(player);
            players.put(player.getName(), sPlayer);
        }
        
        return sPlayer;
    }
    
    public static void remove(String playerName){
        players.remove(playerName);
    }
    
    public static void clearAll(){
        players.clear();
    }
    
    public static OrangePlayer getPlayer(final Player player){
        return getPlayer(player.getName());
    }
    public static OrangePlayer getPlayer(final String playerName){
        return players.get(playerName);
    }
    
    public static PlayerData getData(final String playerName){
        final OrangePlayer sp = players.get(playerName);
        if (sp != null){
            return sp.getData();
        }
        
        return PlayerData.getDataIfExists(playerName);
    }
    
    public static PlayerData getData(final Player player){
        return getData(player.getName());
    }
    
    public static PlayerData getDataIfOnline(final String playerName){
        final OrangePlayer sp = players.get(playerName);
        if (sp != null && sp.getPlayer() != null && sp.getPlayer().isOnline()){
            return sp.getData();
        }
        return null;
    }
    
    /**
     * プレイヤーデータをすべて保存する
     */
    public static void saveAll(final boolean force){
        for (final OrangePlayer sp : players.values()){
            sp.getData().save(force);
        }
    }
    public static void saveAll(){
        saveAll(false);
    }
}
