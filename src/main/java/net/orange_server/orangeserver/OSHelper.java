/**
 * OrangeServer - Package: net.orange_server.orangeserver
 * Created: 2013/01/04 13:02:22
 */
package net.orange_server.orangeserver;

import net.orange_server.orangeserver.feature.GeoIP;
import net.orange_server.orangeserver.listener.feature.MCBansListener;
import net.orange_server.orangeserver.permission.PermissionManager;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.storage.ConfigurationManager;
import net.orange_server.orangeserver.storage.Database;
import net.orange_server.orangeserver.storage.I18n;
import net.orange_server.orangeserver.utils.plugin.DynmapHandler;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.orange_server.orangeserver.worker.FlymodeWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.queue.ConfirmQueue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * OSHelper (OSHelper.java)
 * @author syam(syamn)
 */
public class OSHelper {
    private static long mainThreadID;
    private static long pluginStarted;
    private static OSHelper instance = new OSHelper();
    
    public static OSHelper getInstance(){
        return instance;
    }
    public static void dispose(){
        instance = null;
    }
    
    private OrangeServer plugin;
    private ConfigurationManager config;
    private int flymodeTaskID = -1;
    private boolean isEnableEcon = false;
    
    private boolean enabledMCB = false;
    private static boolean enabledMCBlistener = false;
    
    /**
     * プラスグインの初期化時と有効化時に呼ばれる
     */
    private void init(){
        // loadconfig
        try {
            config.loadConfig(true);
        } catch (Exception ex) {
            LogUtil.warning("an error occured while trying to load the config file.");
            ex.printStackTrace();
        }
        
        Plugin test = plugin.getServer().getPluginManager().getPlugin("MCBans");
        if (test != null && test.isEnabled()){
            if (!enabledMCBlistener){
                plugin.getServer().getPluginManager().registerEvents(new MCBansListener(plugin), plugin);
                LogUtil.info("MCBans integration is enabled!");
                enabledMCBlistener = true;
            }
            enabledMCB = true;
        }else{
            enabledMCB = false;
        }
        
        // connect database
        Database db = Database.getInstance(plugin);
        db.createStructure();
        
        // worker
        net.orange_server.orangeserver.worker.FlymodeWorker.getInstance(); // Flymode worker
        flymodeTaskID = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin, FlymodeWorker.getInstance().getTask(), 0, 20).getTaskId();
        
        PermissionManager.setupPermissions(plugin); // init permission
        
        // dynmap
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                DynmapHandler.createInstance();
            }
        }, 20L);
        
        // coloring tab list
        for (final Player p  : plugin.getServer().getOnlinePlayers()){
            OrangeServerUtil.changeTabColor(p);
        }
        
        // queue, create instance
        ConfirmQueue.getInstance();
        
        // Mapping already online players
        PlayerManager.clearAll();
        for (final Player player : Bukkit.getOnlinePlayers()){
            PlayerManager.addPlayer(player);
        }
    }
    
    public void setMainPlugin(final OrangeServer plugin){
        mainThreadID = Thread.currentThread().getId();
        this.plugin = plugin;
        this.config = new ConfigurationManager(plugin);
        
        init();
    }
    
    public void disableAll(){
        if (flymodeTaskID != -1){
            plugin.getServer().getScheduler().cancelTask(flymodeTaskID);
            flymodeTaskID = -1;
        }
        
        net.orange_server.orangeserver.worker.FlymodeWorker.dispose();
        
        if (DynmapHandler.getInstance() != null){
            DynmapHandler.getInstance().deactivate();
        }
        DynmapHandler.dispose();
        ConfirmQueue.dispose();
        GeoIP.dispose();
        Database.dispose(); // conn close
    }
    
    /**
     * プラグインをリロードする
     */
    public synchronized void reload(){
        disableAll();
        System.gc();
        init();
        
        try {
            I18n.setCurrentLanguage(config.getLanguage());
        } catch (Exception ex) {
            LogUtil.warning("An error occured while trying to load the language file!");
            ex.printStackTrace();
        }
    }
    
    // Economy getter/setter
    public void setEnableEcon(final boolean enable){
        this.isEnableEcon = enable;
    }
    public boolean isEnableEcon(){
        return this.isEnableEcon;
    }
    
    /**
     * 設定マネージャを返す
     *
     * @return ConfigurationManager
     */
    public ConfigurationManager getConfig() {
        return config;
    }
}
