/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener
 * Created: 2013/01/06 17:52:27
 */
package net.orange_server.orangeserver.listener;

import net.orange_server.orangeserver.OrangeServer;

import org.bukkit.event.Listener;

/**
 * EntityListener (EntityListener.java)
 * @author syam(syamn)
 */
public class EntityListener implements Listener{
    private OrangeServer plugin;
    public EntityListener (final OrangeServer plugin){
        this.plugin = plugin;
    }
}
