/**
 * OrangeServer - Package: net.orange_server.orangeserver.player
 * Created: 2013/01/01 22:27:55
 */
package net.orange_server.orangeserver.player;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.permission.PermissionManager;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.storage.ConfigurationManager;
import net.orange_server.orangeserver.worker.FlymodeWorker;
import net.syamn.utils.Util;

import org.bukkit.entity.Player;

/**
 * OrangePlayer (OrangePlayer.java)
 * @author syam(syamn)
 */
public class OrangePlayer {
    private final ConfigurationManager config;
    
    private Player player;
    private PlayerData data;
    
    /* *** Status ******* */
    public OrangePlayer(final Player player){
        this.player = player;
        this.data = new PlayerData(player.getName());
        
        this.config = OSHelper.getInstance().getConfig();
    }
    public Player getPlayer(){
        return this.player;
    }
    public OrangePlayer setPlayer(final Player player){
        initStatus();
        this.player = player;
        // Validate player instance
        if (!player.getName().equalsIgnoreCase(this.data.getPlayerName())){
            throw new IllegalStateException("Wrong player instance! Player: " + player.getName() + " Data: " + this.data.getPlayerName());
        }
        return this;
    }
    
    public String getName(boolean hideStatus){
        if (player == null){
            throw new IllegalStateException("Null Player!");
        }
        
        if (config.getUseNamePrefix()){
            final String prefix = getPrefix(hideStatus);
            String suffix = PermissionManager.getSuffix(player);
            suffix = (suffix == null) ? "" : Util.coloring(suffix);
            
            if (config.getUseDisplayname()){
                return prefix + player.getDisplayName() + suffix;
            }else{
                return prefix + player.getName() + suffix;
            }
        }else{
            return (config.getUseDisplayname()) ? player.getDisplayName() : player.getName();
        }
    }
    public String getName(){
        return getName(false);
    }
    
    public String getPrefix(boolean hideStatus){
        String prefix = PermissionManager.getPrefix(player);
        if (prefix == null) prefix = "";
        if (hideStatus){
            return Util.coloring(prefix);
        }
        
        String status = "";
        
        return Util.coloring(status + prefix);
    }
    public String getPrefix(){
        return getPrefix(false);
    }
    
    public PlayerData getData(){
        return data;
    }
    
    public void initStatus(){
        //this.isAfk = false;
    }
    
    public void restorePowers(){
        // Flymode power
        FlymodeWorker.getInstance().checkRestoreFlymode(this);
    }
    
    private void removePowerNotPerms(final Power power, final Perms perms){
        if (getPlayer() != null && hasPower(power) && !perms.has(getPlayer())){
            removePower(power);
        }
    }
    
    /* *** Status getter/setter */
    
    // infos:
    
    // Powers:
    public boolean hasPower(final Power power){
        return this.data.hasPower(power);
    }
    public void addPower(final Power power){
        this.data.addPower(power);
    }
    public void removePower(final Power power){
        this.data.removePower(power);
    }
    
    @Override
    public boolean equals(final Object obj){
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof OrangePlayer)){
            return false;
        }
        final OrangePlayer sp = (OrangePlayer) obj;
        
        if (this.player == null){
            if (sp.player != null){
                return false;
            }
        }else if(sp == null || !this.player.getName().equals(sp.getName())){
            return false;
        }
        
        return true;
    }
}
