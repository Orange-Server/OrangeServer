/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener
 * Created: 2013/01/11 6:28:51
 */
package net.orange_server.orangeserver.listener;

import net.orange_server.orangeserver.OrangeServer;

import org.bukkit.event.Listener;

/**
 * BlockListener (BlockListener.java)
 * @author syam(syamn)
 */
public class BlockListener implements Listener{
    private OrangeServer plugin;
    public BlockListener (final OrangeServer plugin){
        this.plugin = plugin;
    }
}
