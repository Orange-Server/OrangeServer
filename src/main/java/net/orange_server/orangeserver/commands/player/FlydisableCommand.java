/**
 * OrangeServer - Package: net.orange_server.orangeserver.commands.player
 * Created: 2013/05/13 23:15:35
 */
package net.orange_server.orangeserver.commands.player;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.commands.BaseCommand;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.orange_server.orangeserver.worker.FlymodeWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * FlydisableCommand (FlydisableCommand.java)
 * @author syam(syamn)
 */
public class FlydisableCommand extends BaseCommand{
    public FlydisableCommand(){
        bePlayer = false;
        name = "flydisable";
        perm = Perms.FLYDISABLE;
        argLength = 1;
        usage = "<player> <- disable specified players flymode";
    }
    
    public void execute() throws CommandException{
        final String issuedBy = (isPlayer) ? PlayerManager.getPlayer(player).getName() : "&cCONSOLE";
        final FlymodeWorker worker = FlymodeWorker.getInstance();
        
        final Map<String, Integer> players = worker.getFlymodePlayers();
        if (players.isEmpty()){
            throw new CommandException("&c現在飛行権が有効なプレイヤーはいません");
        }
        
        String target = args.get(0).trim();
        boolean found = false;
        for (final String actualName : players.keySet()){
            if (actualName.equalsIgnoreCase(target)){
                target = actualName;
                found = true;
                break;
            }
        }        
        if (!found){
            throw new CommandException("&cそのプレイヤーは飛行権が有効ではありません！");
        }
        
        worker.disableFlymode(target);
        worker.denialPlayer(target);
        
        final Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer != null && targetPlayer.isOnline()){
            Util.message(targetPlayer, "&cあなたの飛行権は " + issuedBy + " によって取り消されました。");
            Util.message(targetPlayer, "&c再購入するには、" + OSHelper.getInstance().getConfig().getFlymodeDenialTimeInMinutes() + "分間待つ必要があります。");
        }
        
        LogUtil.info(sender.getName() + " disabled flymode of player " + target);
        OrangeServerUtil.sendlog(sender, issuedBy + " &aが " + target + "の飛行権限を取り消しました");
    }
}
