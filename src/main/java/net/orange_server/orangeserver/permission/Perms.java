/**
 * OrangeServer - Package: net.orange_server.orangeserver
 * Created: 2012/12/28 13:39:58
 */
package net.orange_server.orangeserver.permission;

import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * Perms (Perms.java)
 * @author syam(syamn)
 */
public enum Perms {
    /* 権限ノード */
    // Item Commands

    // Tp Commands
    
    // Server Commands
    
    // Player Commands
    
    // World Commands
    
    // Database Commands

    // Other / Admin Commands
    TRUST ("admin.trust"),
    ORANGESERVER ("admin.orangeserver"),
    LOG ("admin.log"),
    LOG_HIDE ("admin.log.hide"),
    
    // Spec Permissions
    HIDE_GEOIP ("spec.hide_geoip"),
    
    // Feature
    
    // Bypass permissions
    
    // Tab color
    TAB_RED ("tab.red"),
    TAB_PURPLE ("tab.purple"),
    TAB_AQUA ("tab.aqua"),
    TAB_NONE ("tab.none"),
    TAB_GRAY ("tab.gray"),
    
    // Rei's Minimap
    ;

    // ノードヘッダー
    final String HEADER = "orange.";
    private String node;

    /**
     * コンストラクタ
     *
     * @param node
     *            権限ノード
     */
    Perms(final String node) {
        this.node = HEADER + node;
    }

    /**
     * 指定したプレイヤーが権限を持っているか
     *
     * @param player
     *            Permissible. Player, CommandSender etc
     * @return boolean
     */
    public boolean has(final Permissible perm) {
        if (perm == null)
            return false;
        //return perm.hasPermission(node); // only support SuperPerms
        return PermissionManager.hasPerm(perm, this);
    }
    
    /**
     * Send message to players has this permission.
     * @param message send message.
     */
    public void message(final String message){
        for (final Player player : Bukkit.getServer().getOnlinePlayers()){
            if (this.has(player)){
                Util.message(player, message);
            }
        }
    }
    
    public String getNode(){
        return this.node;
    }
}