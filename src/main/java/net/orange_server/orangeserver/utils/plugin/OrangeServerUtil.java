/**
 * OrangeServer - Package: net.orange_server.orangeserver.utils.plugin
 * Created: 2013/01/10 16:19:43
 */
package net.orange_server.orangeserver.utils.plugin;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.permission.Perms;
import net.syamn.utils.economy.EconomyUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * OrangeServerUtil (OrangeServerUtil.java)
 * @author syam(syamn)
 */
public class OrangeServerUtil {
    /**
     * 権限によってTabリストの表示色を変更する
     * @param player
     */
    public static void changeTabColor(final Player player){
        ChatColor color = null;
        if (Perms.TAB_RED.has(player)){
            color = ChatColor.RED;
        }
        else if (Perms.TAB_PURPLE.has(player)){
            color = ChatColor.LIGHT_PURPLE;
        }
        else if (Perms.TAB_AQUA.has(player)){
            color = ChatColor.AQUA;
        }
        else if (Perms.TAB_NONE.has(player)){
            color = null;
        }
        else if (Perms.TAB_GRAY.has(player)){
            color = ChatColor.GRAY;
        }
        changeTabColor(player, color);
    }
    public static void changeTabColor(final Player player, final ChatColor color){
        if (color != null){
            String newName = color.toString() + player.getDisplayName();
            if (newName.length() > 16){
                newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
            }
            player.setPlayerListName(newName);
        }else{
            player.setPlayerListName(player.getDisplayName());
        }
    }
    
    public static void changeFlyMode(final Player player, final boolean enable){
        if (player == null) return;
        
        if (enable){
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFallDistance(1F);
        }else{
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFallDistance(0.0F);
        }
    }
    
    public static void sendlog(final String msg){
        Perms.LOG.message("&c[通知]&f " + msg);
    }
    public static void sendlog(final Permissible target, final String msg){
        if (Perms.LOG_HIDE.has(target)){
            return;
        }
        Perms.LOG.message("&c[通知]&f " + msg);
    }
    
    public static boolean addToServerEconAccount(final double amount){
        if (amount < 0D){
            return false;
        }
        if (OSHelper.getInstance().getConfig().getUseEconomy()){
            String acc = OSHelper.getInstance().getConfig().getServerEconAccount();
            return EconomyUtil.addMoney(acc, amount);
        }
        return false;
    }
}
