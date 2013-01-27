/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener
 * Created: 2012/12/31 3:18:20
 */
package net.orange_server.orangeserver.listener;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.feature.GeoIP;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.player.OrangePlayer;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
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
        
        String msg = event.getJoinMessage();
        
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
