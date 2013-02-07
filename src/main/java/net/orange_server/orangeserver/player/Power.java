/**
 * OrangeServer - Package: net.orange_server.orangeserver.player
 * Created: 2013/01/06 8:40:41
 */
package net.orange_server.orangeserver.player;

import java.util.Locale;

/**
 * Power (Power.java)
 * @author syam(syamn)
 */
public enum Power {
    FLYMODE,
    ;
    
    @Override
    public String toString(){
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
