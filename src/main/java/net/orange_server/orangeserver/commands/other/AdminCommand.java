/**
 * OrangeServer - Package: net.orange_server.orangeserver.commands.other
 * Created: 2013/01/18 15:22:18
 */
package net.orange_server.orangeserver.commands.other;

import net.orange_server.orangeserver.commands.BaseCommand;
import net.orange_server.orangeserver.permission.Perms;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * AdminCommand (AdminCommand.java)
 * @author syam(syamn)
 */
public class AdminCommand extends BaseCommand {
    public AdminCommand() {
        bePlayer = false;
        name = "admin";
        perm = Perms.ADMIN;
        argLength = 0;
        usage = "<- admin commands";
    }
    
    @Override
    public void execute() throws CommandException {
        //bcast
        if (args.size() >= 2 && args.get(0).equalsIgnoreCase("bcast")) {
            args.remove(0);
            Util.broadcastMessage(StrUtil.join(args, " "));
            return;
        }
        
        Util.message(sender, "&cUnknown sub-command!");
    }
}
