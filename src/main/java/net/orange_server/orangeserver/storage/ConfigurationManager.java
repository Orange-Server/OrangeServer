/**
 * OrangeServer - Package: net.orange_server.orangeserver
 * Created: 2012/12/28 13:39:51
 */
package net.orange_server.orangeserver.storage;

import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.feature.GeoIP;
import net.syamn.utils.LogUtil;
import net.syamn.utils.SakuraLib;
import net.syamn.utils.economy.EconomyUtil;
import net.syamn.utils.file.FileStructure;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * ConfigurationManager (ConfigurationManager.java)
 * @author syam(syamn)
 */
public class ConfigurationManager {
    /* Current config.yml file version */
    private final int latestVersion = 1;
    
    // logPrefix
    private static final String logPrefix = OrangeServer.logPrefix;
    private static final String msgPrefix = OrangeServer.msgPrefix;
    
    private FileConfiguration conf;
    private File pluginDir;
    
    private final OrangeServer plugin;
    
    /**
     * Constructor
     * @param plugin
     */
    public ConfigurationManager(final OrangeServer plugin){
        this.plugin = plugin;
        this.pluginDir = plugin.getDataFolder();
    }
    
    /**
     * Load configuration
     * @param initialLoad
     * @throws Exception
     */
    public void loadConfig(final boolean initialLoad) throws Exception{
        FileStructure.createDir(pluginDir);
        
        File file = new File(pluginDir, "config.yml");
        if (!file.exists()){
            FileStructure.extractResource("/config.yml", pluginDir, false, false, plugin);
            LogUtil.info(logPrefix + "config.yml is not found! Created default config.yml!");
        }
        
        plugin.reloadConfig();
        conf = plugin.getConfig();
        
        checkver(conf.getInt("ConfigVersion", 1));
        
        // setup Vault economy
        if (getUseEconomy()){
            RegisteredServiceProvider<Economy> econProv = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (econProv != null) {
                SakuraLib.setEconomy(econProv.getProvider());
                OSHelper.getInstance().setEnableEcon(true);
                LogUtil.info("Enabled economy hookup! Using Vault (" + EconomyUtil.getEconomyName() + ") for economy plugin!");
            }else{
                OSHelper.getInstance().setEnableEcon(false);
                LogUtil.warning("Could not hook to economy plugin!");
            }
        }else{
            OSHelper.getInstance().setEnableEcon(false);
        }
        
        // setup geoIP
        if (getUseGeoIP()){
            new GeoIP(plugin).init();
        }
    }
    
    /**
     * Check configuration file version
     * @param ver
     */
    private void checkver(final int ver){
     // compare configuration file version
        if (ver < latestVersion) {
            // first, rename old configuration
            final String destName = "oldconfig-v" + ver + ".yml";
            String srcPath = new File(pluginDir, "config.yml").getPath();
            String destPath = new File(pluginDir, destName).getPath();
            try {
                FileStructure.copyTransfer(srcPath, destPath);
                LogUtil.info("Copied old config.yml to " + destName + "!");
            } catch (Exception ex) {
                LogUtil.warning("Failed to copy old config.yml!");
            }

            // force copy config.yml and languages
            FileStructure.extractResource("/config.yml", pluginDir, true, false, plugin);
            // Language.extractLanguageFile(true);

            plugin.reloadConfig();
            conf = plugin.getConfig();

            LogUtil.info("Deleted existing configuration file and generate a new one!");
        }
    }
    
    /* ***** Begin Configuration Getters ************************** */
    // General
    public boolean getUseNamePrefix(){
        return conf.getBoolean("UseNamePrefix", true);
    }
    public boolean getUseDisplayname(){
        return conf.getBoolean("UseDisplayName", true);
    }
    public boolean getUseEconomy(){
        return conf.getBoolean("UseEconomy", true);
    }
    public String getServerEconAccount(){
        return conf.getString("ServerEconAccount", "Admin");
    }
            
    // Flymode
    public double getFlymodeCost(){
        return conf.getDouble("Flymode.Cost", 1000.0D);
    }
    public int getFlymodeTimeInMinutes(){
        return conf.getInt("Flymode.TimeInMinutes", 20);
    }
    public int getFlymodePlayersLimit(){
        return conf.getInt("Flymode.PlayerLimit", 5);
    }
    
    // GeoIP
    public boolean getUseGeoIP(){
        return conf.getBoolean("UseGeoIP", true);
    }
    public boolean getUseCityDB(){
        return conf.getBoolean("UseCityDB", true);
    }
    public boolean getUseSimpleFormatOnJoin(){
        return conf.getBoolean("UseSimpleFormatOnJoin", true);
    }
    public boolean getDownloadMissingDB(){
        return conf.getBoolean("DownloadMissingDB", true);
    }
    public String getCountryDBurl(){
        return conf.getString("CountryDB", "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
    }
    public String getCityDBurl(){
        return conf.getString("CityDB", "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
    }
    
    // Message
    public String getLanguage(){
        return conf.getString("language", "ja-jp");
    }
    
    // MySQL
    public String getMySqlAddress(){
        return conf.getString("MySQL.Server.Address", "localhost");
    }
    public int getMySqlPort(){
        return conf.getInt("MySQL.Server.Port", 3306);
    }
    public String getMySqlDB(){
        return conf.getString("MySQL.Database.Name", "DatabaseName");
    }
    public String getMySqlUser(){
        return conf.getString("MySQL.Database.User_Name", "UserName");
    }
    public String getMySqlPass(){
        return conf.getString("MySQL.Database.User_Password", "UserPassword");
    }
    
    // Permissions
    public String getPermissionCtrl(){
        return conf.getString("Permission", "PerimssionsEx");
    }
    
    // Debug
    public boolean isDebug(){
        return conf.getBoolean("Debug", false);
    }
}
