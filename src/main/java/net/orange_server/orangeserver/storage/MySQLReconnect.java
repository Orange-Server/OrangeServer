/**
 * OrangeServer - Package: net.orange_server.orangeserver.storage
 * Created: 2013/01/13 1:42:44
 */
package net.orange_server.orangeserver.storage;

import net.orange_server.orangeserver.OrangeServer;

/**
 * MySQLReconnect (MySQLReconnect.java)
 * @author syam(syamn)
 */
public class MySQLReconnect implements Runnable{
    private final OrangeServer plugin;
    private final Database db;

    public MySQLReconnect(final OrangeServer plugin, final Database db){
        this.plugin = plugin;
        this.db = db;
    }

    @Override
    public void run(){
        if (!db.isConnected()){
            db.connect();
            if (db.isConnected()){
                // TODO stuff
            }
        }
    }
}