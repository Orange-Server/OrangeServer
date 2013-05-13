/**
 * SakuraCmd - Package: net.syamn.sakuracmd.worker
 * Created: 2013/01/12 18:45:12
 */
package net.orange_server.orangeserver.worker;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.player.OrangePlayer;
import net.orange_server.orangeserver.player.PlayerData;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.player.Power;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.syamn.utils.LogUtil;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * FlymodeWorker (FlymodeWorker.java)
 * @author syam(syamn)
 */
public class FlymodeWorker {
    private final ConcurrentHashMap<String, Integer> flymodePlayers = new ConcurrentHashMap<String, Integer>();
    private final ConcurrentHashMap<String, Integer> denialPlayers = new ConcurrentHashMap<String, Integer>();
    
    private static FlymodeWorker instance;
    private OrangeServer plugin;
    private FlymodeTask task;
    
    private FlymodeWorker(){
        this.task = new FlymodeTask();
    }
    
    public static FlymodeWorker getInstance(){
        if (instance == null){
            synchronized (FlymodeWorker.class) {
                if (instance == null){
                    instance = new FlymodeWorker();
                    instance.plugin = OrangeServer.getInstance();
                }
            }
        }
        return instance;
    }
    public static void dispose(){
        if (instance != null && instance.flymodePlayers != null && instance.flymodePlayers.size() > 0){
            for (final String name : instance.flymodePlayers.keySet()){
                final Player p = Bukkit.getPlayerExact(name);
                if (p != null && p.isOnline()){
                    instance.changeFlyMode(p, false);
                    Util.message(p, "&cあなたの飛行権はプラグイン更新のため一時的に停止されました");
                }
            }
        }
        
        instance.plugin = null;
        instance = null;
    }
    
    public void onPluginEnabled(){
        if (flymodePlayers.size() > 0){
            int now = TimeUtil.getCurrentUnixSec().intValue();
            for (final Map.Entry<String, Integer> entry : flymodePlayers.entrySet()){
                if ((entry.getValue() - now) <= 0){
                    continue;
                }
                
                final Player player = Bukkit.getPlayerExact(entry.getKey());
                if (player != null && player.isOnline()){
                    changeFlyMode(player, true);
                    Util.message(player, "&aプラグインが有効になり、あなたの飛行権が復活しました");
                }
            }
        }
    }
    
    public FlymodeTask getTask(){
        return this.task;
    }
    
    public void enableFlymode(final OrangePlayer sp, final int minute){
        if (sp == null || sp.getPlayer() == null){
            throw new IllegalArgumentException("player must not be null!");
        }
        
        int expired = TimeUtil.getCurrentUnixSec().intValue() + (minute * 60);
        flymodePlayers.put(sp.getPlayer().getName(), expired);
        sp.addPower(Power.FLYMODE);
        
        changeFlyMode(sp.getPlayer(), true);
    }
    
    public void disableFlymode(final String name){
        if (flymodePlayers.containsKey(name)){
            flymodePlayers.remove(name);
        }
        
        PlayerData data = PlayerManager.getDataIfOnline(name);
        if (data == null){
            data = PlayerManager.getData(name);
        }
        
        data.removePower(Power.FLYMODE);
        changeFlyMode(Bukkit.getPlayerExact(name), false);
        //SakuraCmdUtil.changeFlyMode(Bukkit.getPlayerExact(name), false);
    }
    
    public void checkRestoreFlymode(final OrangePlayer sp){
        if (sp == null || sp.getPlayer() == null){
            throw new IllegalArgumentException("player must not be null!");
        }
        
        final Player player = sp.getPlayer();
        
        if (!flymodePlayers.containsKey(player.getName())){ // don't check permission
            sp.removePower(Power.FLYMODE);
            changeFlyMode(player, false);
            return;
        }
        // player has flymode power
        
        changeFlyMode(player, true);
    }
    
    public String getRemainTime(final String name){
        if (!flymodePlayers.containsKey(name)){
            throw new IllegalArgumentException(name + " is not in flymode players map!");
        }
        
        final int remain = flymodePlayers.get(name) - TimeUtil.getCurrentUnixSec().intValue();
        if (remain <= 0){
            return "0秒";
        }
        return TimeUtil.getReadableTimeBySecond(remain);
    }
    public String getRemainTime(final Player player){
        return getRemainTime(player.getName());
    }
    
    public void changeFlyMode(final Player player, final boolean enable){
        if (player == null){
            return;
        }
        
        if (enable){
            OrangeServerUtil.changeFlyMode(player, true);
        }else{
            if (!player.getGameMode().equals(GameMode.CREATIVE)){
                OrangeServerUtil.changeFlyMode(player, false);
            }
        }
    }
    
    // denial players
    public void denialPlayer(final String name){
        final int denialSeconds = OSHelper.getInstance().getConfig().getFlymodeDenialTimeInMinutes() * 60;
        denialPlayers.put(name, TimeUtil.getCurrentUnixSec().intValue() + denialSeconds);
    }
    public boolean isDenied(final String name){
        final Integer period = denialPlayers.get(name);
        if (period == null) return false;
        
        if (period.intValue() <= TimeUtil.getCurrentUnixSec().intValue()){
            denialPlayers.remove(name);
            return false;
        }else{
            return true;
        }
    }
    public int getRemainsDenialSeconds(final String name){
        if (!isDenied(name)) return 0;
        return (denialPlayers.get(name).intValue() - TimeUtil.getCurrentUnixSec().intValue()); 
    }    
    
    // call async
    class FlymodeTask implements Runnable{
        @Override
        public void run() {
            // don't run when flymodePlayers.size == 0
            if (flymodePlayers.size() == 0){
                return;
            }
            
            int curr = TimeUtil.getCurrentUnixSec().intValue();
            for (final Entry<String, Integer> entry : flymodePlayers.entrySet()){
                final String name = entry.getKey();
                int remain = entry.getValue() - curr;
                
                if (remain <= 0){
                    flymodePlayers.remove(name);
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override public void run(){
                            disableFlymode(name);
                        }
                    }, 0L);
                    sendNotify(name, " &f[&c+&f] &aあなたの飛行モードが終了しました！");
                    LogUtil.info("Player " + name + " is expired flying mode!");
                    OrangeServerUtil.sendlog("&6" + name + " の飛行権限が期限切れで終了しました");
                }
                else if (remain <= 5 || remain == 10 || remain == 30 || remain == 60){
                    sendNotify(name, " &f[&c+&f] &6あと &b" + remain + "秒 &6で飛行モードが終了します");
                }
            }
        }
        
        private void sendNotify(final String name, final String msg){
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    final Player p = Bukkit.getPlayerExact(name);
                    if (p != null && p.isOnline()){
                        Util.message(p, msg);
                    }
                }
            }, 0L);
        }
    }
    
    public Map<String, Integer> getFlymodePlayers(){
        return Collections.unmodifiableMap(flymodePlayers);
    }
    public void addFlymodePlayersMap(final String name, final Integer expire){
        flymodePlayers.put(name.trim(), expire);
    }
    public int getPlayersCount(){
        return flymodePlayers.size();
    }
}
