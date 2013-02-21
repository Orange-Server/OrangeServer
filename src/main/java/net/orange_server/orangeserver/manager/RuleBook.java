/**
 * OrangeServer - Package: net.orange_server.orangeserver.manager
 * Created: 2013/02/21 4:58:31
 */
package net.orange_server.orangeserver.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import net.orange_server.orangeserver.OrangeServer;
import net.syamn.utils.LogUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * RuleBook (RuleBook.java)
 * @author syam(syamn)
 */
public class RuleBook {
    final private static String dirName = "rulebooks";
    
    private static Map<String, RuleBook> books = new HashMap<>(); 
    
    private String bookName;
    private ItemStack item;
    private double cost = 0D;
    
    public static void dispose(){
        saveBooks();
        books.clear();
    }
    
    private RuleBook(final String name, final ItemStack item){
        this.bookName = name;
        this.item = item;
    }
    private RuleBook(final File file){
        ObjectInputStream in = null;
        try {
            bookName = file.getName().replace(".dat", "");
            in = new ObjectInputStream(new FileInputStream(file));
            
            item = ItemStack.deserialize((Map<String, Object>) in.readObject());
            cost = in.readDouble();
        } catch (Exception ex) {
            LogUtil.warning("Could not load book data (" + file.getName() + "): " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { in.close(); } catch (Exception ignore) {}
        }
    }
    
    public RuleBook newBook(String name, final ItemStack item){
        name = name.trim();
        RuleBook book = new RuleBook(name, item);
        books.put(book.bookName, book);
        return book;
    }
    
    public void setCost(final double cost){
        this.cost = cost;
    }
    public double getCost(){
        return this.cost;
    }
    
    public void setItem(final ItemStack item){
        this.item = item;
    }
    public ItemStack getItem(){
        return this.item.clone();
    }
    
    public boolean save(){
        ObjectOutputStream out = null;
        try{
            if (bookName == null || item == null){
                return false;
            }
            
            File file = new File(getDataDirectory(), bookName + ".dat");
            if (!file.exists()){
                file.createNewFile();
            }
            
            out = new ObjectOutputStream(new FileOutputStream(file));
            
            out.writeObject(item.serialize());
            out.writeDouble(cost);
            
            out.flush();
            out.close();
            
            return true;
        } catch (IOException ex) {
            LogUtil.warning("Could not save " + bookName + " data: " + ex.getMessage());
            return false;
        } finally {
            try { out.close(); } catch (Exception ignore) {}
        }
    }
    
    public boolean isExist(final String name){
        return books.containsKey(name.trim());
    }
    
    public static void saveBooks(){
        for (final RuleBook book : books.values()){
            book.save();
        }
    }
    public static void loadBooks() {
        File dir = getDataDirectory();
        File[] files = dir.listFiles();

        books.clear();

        // ファイルなし
        if (files == null || files.length == 0) return;

        for (final File file : files) {
            try {
                RuleBook book = new RuleBook(file);
                books.put(book.bookName, book);
            }catch(Exception ex){
                continue;
            }
        }
    }

    public static File getDataDirectory(){
        final File dir = new File(OrangeServer.getInstance().getDataFolder(), dirName);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }
}
