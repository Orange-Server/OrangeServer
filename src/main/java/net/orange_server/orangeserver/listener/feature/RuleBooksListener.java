/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener.feature
 * Created: 2013/02/25 19:32:27
 */
package net.orange_server.orangeserver.listener.feature;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.syamn.rulebooks.events.TransactionEvent;
import net.syamn.utils.LogUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * RuleBooksListener (RuleBooksListener.java)
 * @author syam(syamn)
 */
public class RuleBooksListener implements Listener{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTransaction(final TransactionEvent event){
        if (!OSHelper.getInstance().isEnableEcon() || !event.isPaid()){
            return;
        }
        
        double price = event.getPrice();
        if (price > 0 && !OrangeServerUtil.addToServerEconAccount(price)){
            LogUtil.warning("Could not add money to server econ account!");
        }
    }
}
