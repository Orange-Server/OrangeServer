/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener.feature
 * Created: 2013/01/25 0:27:36
 */
package net.orange_server.orangeserver.listener.feature;

import net.orange_server.orangeserver.OrangeServer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.mcbans.firestar.mcbans.events.PlayerGlobalBanEvent;

/**
 * MCBansListener (MCBansListener.java)
 * @author syam(syamn)
 */
public class MCBansListener implements Listener{
    private OrangeServer plugin;
    public MCBansListener (final OrangeServer plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerGlobalBan(final PlayerGlobalBanEvent event){
        String s = event.getReason() + " - dispute @ mcbans.com (" + event.getSenderName()+")";
        event.setReason(s);
    }
}
