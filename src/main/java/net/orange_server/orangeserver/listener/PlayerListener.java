/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener
 * Created: 2012/12/31 3:18:20
 */
package net.orange_server.orangeserver.listener;

import java.util.ArrayList;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.feature.GeoIP;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.player.OrangePlayer;
import net.orange_server.orangeserver.player.PlayerData;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.player.Power;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.orange_server.orangeserver.worker.FlymodeWorker;
import net.syamn.utils.Util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * PlayerListener (PlayerListener.java)
 * @author syam(syamn)
 */
public class PlayerListener implements Listener{
    private OrangeServer plugin;
    public PlayerListener (final OrangeServer plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final OrangePlayer op = PlayerManager.getPlayer(player);
        final ArrayList<String> notify = new ArrayList<String>();
        
        String msg = event.getJoinMessage();
        
        // restore powers
        op.restorePowers();
        if (op.hasPower(Power.FLYMODE)){
            notify.add("&bあなたはあと &a" + FlymodeWorker.getInstance().getRemainTime(player) + "間 &b飛行可能です");
        }
        
        // Use GeoIP if enabled
        if (OSHelper.getInstance().getConfig().getUseGeoIP() && !Perms.HIDE_GEOIP.has(player)){
            msg = event.getJoinMessage();
            if (msg != null){
                String geoStr = GeoIP.getInstance().getGeoIpString(player, OSHelper.getInstance().getConfig().getUseSimpleFormatOnJoin());
                event.setJoinMessage(msg + Util.coloring("&7") + " (" + geoStr + ")");
            }
        }
        
        // Change TabColor
        OrangeServerUtil.changeTabColor(player);
        
        // Send notify
        if (notify.size() > 0){
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    for (final String line : notify){
                        Util.message(player, line);
                    }
                    notify.clear();
                }
            }, 5L);
        }
        
        // Run async
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override public void run(){
                PlayerData data = op.getData();
                data.setLastIP(player.getAddress().getAddress().getHostAddress());
            }
        });
        
        // First join
        /*
        if (!player.hasPlayedBefore()) {
            final int unique = plugin.getServer().getOfflinePlayers().length;
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Util.broadcastMessage("&6現在のユニークビジター数: " + unique + " プレイヤー");
                }
            },
         */
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event){
        if (!event.getResult().equals(Result.ALLOWED)){
            return;
        }
        
        // Add to players list
        PlayerManager.addPlayer(event.getPlayer());
    }
}
