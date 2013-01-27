/**
 * OrangeServer - Package: net.orange_server.orangeserver.listener.feature
 * Created: 2013/01/25 0:27:36
 */
package net.orange_server.orangeserver.listener.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import net.orange_server.orangeserver.OrangeServer;
import net.syamn.utils.LogUtil;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.mcbans.firestar.mcbans.events.PlayerBannedEvent;

/**
 * MCBansListener (MCBansListener.java)
 * @author syam(syamn)
 */
public class MCBansListener implements Listener{
    private OrangeServer plugin;
    public MCBansListener (final OrangeServer plugin){
        this.plugin = plugin;
    }
    
    //@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBanned(final PlayerBannedEvent event){
        throw new NotImplementedException();
    }
}
