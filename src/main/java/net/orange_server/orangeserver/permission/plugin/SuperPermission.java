/**
 * OrangeServer - Package: net.orange_server.orangeserver.permission.plugin
 * Created: 2013/01/04 3:15:06
 */
package net.orange_server.orangeserver.permission.plugin;

import java.util.Set;

import net.orange_server.orangeserver.exception.NotSupportedException;
import net.orange_server.orangeserver.permission.IPermissionPlugin;
import net.orange_server.orangeserver.permission.Perms;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * SuperPermission (SuperPermission.java)
 * @author syam(syamn)
 */
public class SuperPermission implements IPermissionPlugin{
    public SuperPermission(){
        
    }

    @Override
    public String getPrefix(Player player) {
        return "";
    }

    @Override
    public String getSuffix(Player player) {
        return "";
    }

    @Override
    public Set<Player> getPlayers(String groupName) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public String getGroupName(Player player) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public boolean isInGroup(Player player, String groupName) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public boolean hasPerm(Permissible sender, Perms perm) {
        return this.hasPerm(sender, perm.getNode());
    }

    @Override
    public boolean hasPerm(Permissible sender, String node) {
        if(!(sender instanceof Player)){
            return true;
        }
        
        return sender.hasPermission(node);
    }
}
