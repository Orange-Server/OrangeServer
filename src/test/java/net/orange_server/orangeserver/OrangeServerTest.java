/**
 * OrangeServer - Package: net.orange_server.orangeserver
 * Created: 2013/01/12 20:00:17
 */
package net.orange_server.orangeserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * OrangeServerTest (OrangeServerTest.java)
 * @author syam(syamn)
 */
public class OrangeServerTest {
    private transient List<Player> playerList;
    
    @Before
    public void setUp(){
        playerList = new ArrayList<Player>();
    }
    
    @Test
    public void addPlayerTest(){
        Player p = addPlayer("testPlayer1");
        log("addPlayerTest passed");
    }
    
    @After
    public void tearDown(){
        playerList.clear();
        System.gc();
    }
    
    private void log(String str){
        System.out.println(str);
    }
    
    private Player addPlayer(String name){
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        playerList.add(player);
        return player;
    }
}
