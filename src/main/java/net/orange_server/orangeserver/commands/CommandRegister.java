/**
 * OrangeServer - Package: net.orange_server.orangeserver.commands
 * Created: 2012/12/29 6:22:06
 */
package net.orange_server.orangeserver.commands;

import java.util.HashSet;
import java.util.Set;

import net.orange_server.orangeserver.commands.other.AdminCommand;
import net.orange_server.orangeserver.commands.other.ConfirmCommand;
import net.orange_server.orangeserver.commands.other.OrangeServerCommand;

/**
 * CommandRegister (CommandRegister.java)
 * @author syam(syamn)
 */
public class CommandRegister {
    private static Set<BaseCommand> getCommands(){
        Set<BaseCommand> cmds = new HashSet<BaseCommand>();
        
        // Item Commands
        
        // Teleport Commands
        
        // Server Commands
        
        // Player Commands
        
        // World Commands
        
        // Database Commands
        
        // Other Commands
        cmds.add(new AdminCommand());
        cmds.add(new OrangeServerCommand());
        cmds.add(new ConfirmCommand());
        
        return cmds;
    }
    
    public static void registerCommands(final CommandHandler handler){
        Set<BaseCommand> cmds = getCommands();
        
        for (final BaseCommand cmd : cmds){
            handler.registerCommand(cmd);
        }
    }
}
