/**
 * OrangeServer - Package: net.orange_server.orangeserver.manager
 * Created: 2013/01/11 0:26:26
 */
package net.orange_server.orangeserver.manager;

/**
 * Worlds (Worlds.java)
 * @author syam(syamn)
 */
public class Worlds {
    public static String main_world = "world";
    public static String main_nether = "world_nether";
    public static String main_end = "world_the_end";
        
    public static boolean isResource(final String worldName){
        return worldName.contains("shigen");
    }
}
