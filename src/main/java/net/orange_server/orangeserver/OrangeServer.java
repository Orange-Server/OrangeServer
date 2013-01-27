/**
 * OrangeServer - Package: net.orange_server.orangeserver
 * Created: 2012/12/28 13:35:03
 */
package net.orange_server.orangeserver;

import java.io.IOException;
import java.util.List;

import net.orange_server.orangeserver.commands.CommandHandler;
import net.orange_server.orangeserver.commands.CommandRegister;
import net.orange_server.orangeserver.listener.BlockListener;
import net.orange_server.orangeserver.listener.EntityListener;
import net.orange_server.orangeserver.listener.PlayerListener;
import net.orange_server.orangeserver.manager.ServerManager;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.storage.I18n;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Metrics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * OrangeServer (OrangeServer.java)
 * @author syam(syamn)
 */
public class OrangeServer extends JavaPlugin{
    // ** Logger **
    public final static String logPrefix = "[OrangeServer] ";
    public final static String msgPrefix = "&c[OrangeServer] &f";

    // ** Commands **
    private CommandHandler commandHandler;
    
    // ** Managers **
    private ServerManager serverMan;

    // ** Private Classes **
    private OSHelper worker;

    // ** Static **
    //private static Database database;

    // ** Instance **
    private static OrangeServer instance;
    
    /**
     * プラグイン起動処理
     */
    @Override
    public void onEnable() {
        instance = this;
        LogUtil.init(this);
        
        worker = OSHelper.getInstance();
        worker.setMainPlugin(this);
        
        // I18n Init
        LogUtil.info("Loading language file: " + worker.getConfig().getLanguage());
        I18n.init(worker.getConfig().getLanguage());

        // Managers
        serverMan = new ServerManager(this);
        
        // Regist Listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new EntityListener(this), this);
        
        // features
        
        // commands
        commandHandler = new CommandHandler(this);
        CommandRegister.registerCommands(commandHandler);

        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        LogUtil.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

        setupMetrics(); // mcstats
    }

    /**
     * プラグイン停止処理
     */
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        
        // call disableAll, dispose all components
        worker.disableAll();
        
        // dispose main worker
        OSHelper.dispose();
        
        // Save player profiles
        PlayerManager.saveAll();
        
        // メッセージ表示
        PluginDescriptionFile pdfFile = this.getDescription();
        LogUtil.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return commandHandler.onCommand(sender, command, label, args);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commandHandler.onTabComplete(sender, command, alias, args);
    }
    
    /**
     * サーバマネージャを返す
     * @return
     */
    public ServerManager getServerManager(){
        return this.serverMan;
    }

    /**
     * Metricsセットアップ
     */
    private void setupMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            LogUtil.warning(logPrefix + "cant send metrics data!");
            ex.printStackTrace();
        }
    }

    /* getter */
    /**
     * SCHelperインスタンスを返す
     * @return OSHelper
     */
    public OSHelper getWorker(){
        return this.worker;
    }

    /**
     * インスタンスを返す
     *
     * @return SakuraCmdインスタンス
     */
    public static OrangeServer getInstance() {
        return instance;
    }
}
