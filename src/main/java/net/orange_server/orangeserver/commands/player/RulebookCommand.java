/**
 * OrangeServer - Package: net.orange_server.orangeserver.commands.player
 * Created: 2013/02/22 6:27:33
 */
package net.orange_server.orangeserver.commands.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.commands.BaseCommand;
import net.orange_server.orangeserver.manager.RuleBook;
import net.orange_server.orangeserver.permission.Perms;
import net.orange_server.orangeserver.player.OrangePlayer;
import net.orange_server.orangeserver.player.PlayerManager;
import net.orange_server.orangeserver.utils.plugin.OrangeServerUtil;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.economy.EconomyUtil;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permissible;

/**
 * RulebookCommand (RulebookCommand.java)
 * @author syam(syamn)
 */
public class RulebookCommand extends BaseCommand{
    public RulebookCommand(){
        bePlayer = true;
        name = "rulebook";
        perm = Perms.RULEBOOK;
        argLength = 0;
        usage = "<- rulebook commands";
    }

    public void execute() throws CommandException{
        //final OrangePlayer sp = PlayerManager.getPlayer(player);
        final String action = (args.size() > 0) ? args.remove(0).trim().toLowerCase(Locale.ENGLISH) : "";
        
        if (action.equals("list")){
            checkIsAdmin();
            listCommand();
            return;
        }
        else if (action.equals("new")){
            checkIsAdmin();
            newCommand();
            return;
        }
        else if (action.equals("delete")){
            checkIsAdmin();
            deleteCommand();
            return;
        }
        else if (action.equals("cost")){
            checkIsAdmin();
            costCommand();
            return;
        }
        
        Map<String, RuleBook> books = getBuyableBooks();
        if (books.isEmpty()){
            throw new CommandException("&c現在購入できるルールブックはありません！");
        }
        
        if (books.containsKey(action)){
            buyBook(books.get(action));
            return;
        }
        
        Util.message(sender, "&a ===== &b購入可能ルールブックリスト(" + books.size() + ") &a======");
        for (final RuleBook book : books.values()){
            Util.message(sender, " &6" + book.getName() + "&7 (Cost: " + book.getCost() +" Gold)");
        }
    }
    
    private void checkIsAdmin() throws CommandException{
        if (!Perms.RULEBOOK_ADMIN.has(sender)){
            throw new CommandException("&c権限がありません");
        }
    }
    
    private void listCommand() throws CommandException{
        Map<String, RuleBook> books = RuleBook.getBooks();
        if (books.size() <= 0){
            throw new CommandException("&c現在ルールブックは1冊も設定されていません！");
        }
        Util.message(sender, "&a ========== &bRuleBooks(" + books.size() + ") &a==========");
        for (final RuleBook book : books.values()){
            Util.message(sender, " &6" + book.getName() + "&7 (Cost: " + book.getCost() +" Gold)");
        }
    }
    
    private void newCommand() throws CommandException{
        if (args.size() <= 0){
            throw new CommandException("&cルールブック名を指定してください！");
        }
        final String name = args.get(0).trim();
        
        if (RuleBook.isExist(name)){
            throw new CommandException("&cこの名前のルールブックは既に存在します！");
        }
        
        final ItemStack is = player.getItemInHand();
        if (is == null || is.getType() != Material.WRITTEN_BOOK){
            throw new CommandException("&c手に署名済みの本を持っていません！");
        }
        
        RuleBook.newBook(name, is);
        Util.message(sender, "&aルールブック " + name + " を保存しました！");
    }
    
    private void deleteCommand() throws CommandException{
        if (!Perms.RULEBOOK_ADMIN.has(sender)){
            throw new CommandException("&c権限がありません");
        }
        
        if (args.size() <= 0){
            throw new CommandException("&cルールブック名を指定してください！");
        }
        final String name = args.get(0).trim();
        
        if (!RuleBook.isExist(name)){
            throw new CommandException("&cその名前のルールブックは存在しません！");
        }
        
        RuleBook.getBook(name).delete();
        Util.message(sender, "&aルールブック " + name + " を削除しました！");
    }
    
    private void costCommand() throws CommandException{
        if (!Perms.RULEBOOK_ADMIN.has(sender)){
            throw new CommandException("&c権限がありません");
        }
        
        if (args.size() <= 1){
            throw new CommandException("&cルールブック名と価格を指定してください！");
        }
        final String name = args.get(0).trim();
        if (!StrUtil.isDouble(args.get(1))){
            throw new CommandException("&c価格が数値ではありません: " + args.get(1));
        }
        final double cost = Double.parseDouble(args.get(1));
        if (cost < 0){
            throw new CommandException("&c負数は指定できません！");
        }
        
        if (!RuleBook.isExist(name)){
            throw new CommandException("&cこの名前のルールブックは存在しません！");
        }
        RuleBook.getBook(name).setCost(cost);
        
        Util.message(sender, "&aルールブック " + name + " の価格を " + cost + "Gold に変更しました！");
    }

    private void buyBook(final RuleBook book) throws CommandException{
        // check inventory
        PlayerInventory inv = player.getInventory();
        Iterator<ItemStack> iter = inv.iterator();
        boolean hasEmptySlot = false;
        while (iter.hasNext()){
            if (iter.next() == null){
                hasEmptySlot = true;
                break;
            }
        }
        if (!hasEmptySlot){
            throw new CommandException("&cインベントリに空きがありません！");
        }
        
        // pay cost
        double cost = book.getCost();
        boolean paid = false;
        if (cost > 0){
            if (!OSHelper.getInstance().isEnableEcon()){
                throw new CommandException("&c経済システムが動作していないため使えません！");
            }
            paid = EconomyUtil.takeMoney(player, cost);
            if (!paid){
                throw new CommandException("&cお金が足りません！ " + cost + "Gold必要です！");
            }
            
            if (!OrangeServerUtil.addToServerEconAccount(cost)){
                LogUtil.warning("Could not add money to server econ account!");
            }
        }
        
        inv.addItem(book.getItem());
        Util.message(sender, "&aルールブック " + book.getName() + " を" + ((paid) ? "購入" : "入手") + "しました！");
        //OrangeServerUtil.sendlog(player, sp.getName() + " &aがルールブック " + books.getName() + " を購入しました");
    }
    
    private boolean isBuyableBook(final RuleBook book, final Permissible perm){
        if (book == null || perm == null) return false;
        return (Perms.RULEBOOK_BUY_HEADER.has(perm, book.getName().toLowerCase(Locale.ENGLISH).replace(' ', '_')));
    }
    
    private Map<String, RuleBook> getBuyableBooks(){
        Map<String, RuleBook> ret = new HashMap<>();
        ret.clear();
        
        for (final RuleBook book : RuleBook.getBooks().values()){
            if (isBuyableBook(book, sender)){
                ret.put(book.getName(), book);
            }
        }
        
        return ret;
    }
}