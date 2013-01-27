/**
 * OrangeServer - Package: net.orange_server.orangeserver.commands.other
 * Created: 2013/01/08 15:42:46
 */
package net.orange_server.orangeserver.commands.other;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.commands.BaseCommand;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.storage.I18n;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * OrangeServerCommand (OrangeServerCommand.java)
 * @author syam(syamn)
 */
public class OrangeServerCommand extends BaseCommand{
    public OrangeServerCommand(){
        bePlayer = false;
        name = "orangeserver";
        perm = Perms.ORANGESERVER;
        argLength = 0;
        usage = "<- admin commands";
    }
    
    public void execute() throws CommandException{
        if (args.size() < 1){
            throw new CommandException("&c引数が足りません！");
        }
        final String func = args.remove(0);
        
        // reload
        if (func.equalsIgnoreCase("reload")){
            //PlayerManager.saveAll();
            //Util.message(sender, "&aPlayer data saved!");
            if (args.size() > 0 && args.get(0).equalsIgnoreCase("all")){
                OSHelper.getInstance().reload();
                Util.message(sender, "&aOrangeServer plugin reloaded!");
            }else{
                try {
                    OSHelper.getInstance().getConfig().loadConfig(false);
                    I18n.setCurrentLanguage(OSHelper.getInstance().getConfig().getLanguage());
                } catch (Exception ex) {
                    if (isPlayer){
                        Util.message(sender, "&cError occred while reloading configuration! Check server console!");
                    }
                    LogUtil.warning("an error occured while trying to load the config file.");
                    ex.printStackTrace();
                    return;
                }
                Util.message(sender, "&aOrangeServer configuration & locale messages reloaded!");
            }
            return; // reload
        }
        
        // save
        if (func.equalsIgnoreCase("save")){
            //PlayerManager.saveAll(true);
            //Util.message(sender, "&aPlayer data force saved!");
            throw new CommandException("&cNot implemented!");
            // return;
        }
        
        throw new CommandException("&c引数が不正です！");
    }
}