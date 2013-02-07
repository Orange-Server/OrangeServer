/**
 * OrangeServer - Package: net.orange_server.orangeserver.player
 * Created: 2013/01/01 22:29:06
 */
package net.orange_server.orangeserver.player;

import java.io.File;
import java.util.ArrayList;

import net.orange_server.orangeserver.OrangeServer;
import net.orange_server.orangeserver.storage.Database;
import net.syamn.utils.LogUtil;
import net.syamn.utils.ParseUtil;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * PlayerData (PlayerData.java)
 * @author syam(syamn)
 */
public class PlayerData{
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String dataDir = "userData";
    
    private final String playerName;
    private int playerID = 0;
    private YamlConfiguration conf = new YamlConfiguration();
    private File file;
    private boolean saved = true;
    
    /* Transient status */
    // --> moved to OrangePlayer
    
    /* Saves values*/
    // infos:
    private String lastIP = "";
    
    // powers:
    private ArrayList<Power> powers = new ArrayList<Power>();
    
    /* ************************** */

    public PlayerData(final String playerName){
        this.playerName = playerName;
        String fileName = OrangeServer.getInstance().getDataFolder() + SEPARATOR + dataDir + SEPARATOR + playerName + ".yml";
        load(new File(fileName));
    }
    private PlayerData(final String playerName, final File file){
        this.playerName = playerName;
        load(file);
    }
    
    private boolean load(final File file){
        this.file = file;
        if (!file.exists()){
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            if (!save(true)){
                throw new IllegalStateException("Could not create plaer data file: " + file.getPath());
            }
        }
        
        try {
            conf = new YamlConfiguration();
            conf.load(file);
            
            // load infos
            ConfigurationSection csi = conf.getConfigurationSection("infos");
            if (csi != null){
                this.lastIP = csi.getString("last-ip", "");
            }
            
            // load powers
            ConfigurationSection csp = conf.getConfigurationSection("powers");
            powers.clear();
            if (csp != null){
                for (final Power p : Power.values()){
                    if (csp.getBoolean(p.toString(), false)){
                        this.powers.add(p);
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return loadDB(); // next, load from mysql
    }
    private boolean loadDB(){
        if (true) return true; //TODO not implemented
        
        final Database db = Database.getInstance();
        if (db == null || !db.isConnected()){
            LogUtil.warning("Could not load " + playerName + " data! Not connected to DB!");
            return false;
        }
        
        playerID = db.getInt("SELECT `player_id` FROM `user_id` WHERE `player_name` = ?", playerName);
        if (playerID == 0){
            // not exists on base db, add as new player
            db.write("INSERT INTO `user_id` (`player_name`) VALUES (?)", playerName);
            playerID = db.getInt("SELECT `player_id` FROM `user_id` WHERE `player_name` = ?", playerName);
            if (playerID == 0){
                LogUtil.warning("Could not add new player data: " + playerName);
                return false;
            }else{
                LogUtil.info("Added new player data to web database: " + playerName); // TODO for debug?
            }
        }
        
        return true;
    }
    
    public boolean save(final boolean force){        
        if (!saved || force){
            try{
                // save infos
                ConfigurationSection csi = conf.createSection("infos");
                csi.set("last-ip", lastIP);
                
                // save powers
                ConfigurationSection csp = conf.createSection("powers");
                for (final Power p : Power.values()){
                    csp.set(p.toString(), this.hasPower(p));
                }
                
                conf.save(file);
            }catch (Exception ex){
                ex.printStackTrace();
                return false;
            }
        }
        return saveDB(); // next, save to mysql
    }
    
    private boolean saveDB(){
        // TODO nothing to do atm
        return true;
    }
    
    public String getPlayerName(){
        return this.playerName;
    }
    
    @Deprecated
    public int getPlayerID(){
        return this.playerID;
    }
    
    public static PlayerData getDataIfExists(final String playerName){
        final String fileName = OrangeServer.getInstance().getDataFolder() + SEPARATOR + dataDir + SEPARATOR + playerName + ".yml";
        final File file = new File(fileName);
        return (file.exists()) ? new PlayerData(playerName, file) : null;
    }
    
    /* Getter/Setter */
    
    // infos:
    // last-ip
    public void setLastIP(final String ip){
        this.lastIP = ip;
        saved = false;
    }
    public String getLastIP(){
        return this.lastIP;
    }
    
    // powers:
    public boolean hasPower(final Power power){
        return powers.contains(power);
    }
    public void addPower(final Power power/*, final int level*/){
        if (!hasPower(power)){
            powers.add(power);
            saved = false;
        }
    }
    public void removePower(final Power power){
        powers.remove(power);
        saved = false;
    }
}
