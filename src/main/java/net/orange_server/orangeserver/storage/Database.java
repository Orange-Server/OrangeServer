/**
 * OrangeServer - Package: net.orange_server.orangeserver.storage
 * Created: 2013/01/13 1:17:40
 */
package net.orange_server.orangeserver.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.orange_server.orangeserver.OSHelper;
import net.orange_server.orangeserver.OrangeServer;
import net.syamn.utils.LogUtil;

/**
 * Database (Database.java)
 * @author syam(syamn)
 */
public class Database {
    private static Database instance = null;
    public static Database getInstance(final OrangeServer plugin){
        if (instance == null){
            synchronized (Database.class){
                if (instance == null){
                    instance = new Database(plugin);
                }
            }
        }
        return instance;
    }
    public static Database getInstance(){
        return instance;
    }
    public static void dispose(){
        if (connection != null){
            try {
                connection.close();
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        connection = null;
        instance = null;
    }
    
    private OrangeServer plugin;
    private String connectionString = null;
    
    private static long reconnectTimestamp = 0;
    private static Connection connection = null;

    /**
     * コンストラクタ
     * @param plugin プラグインインスタンス
     */
    private Database(final OrangeServer plugin){
        this.plugin = plugin;
        ConfigurationManager conf = OSHelper.getInstance().getConfig();

        // 接続情報読み込み
        connectionString = "jdbc:mysql://" + conf.getMySqlAddress() + ":" + conf.getMySqlPort() + "/" + conf.getMySqlDB() + 
                "?user=" + conf.getMySqlUser() + "&password=" + conf.getMySqlPass();
        connect(); // 接続

        // ドライバを読み込む
        try{
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.getConnection(connectionString);
        }catch (ClassNotFoundException ex1){
            LogUtil.severe(ex1.getLocalizedMessage());
        }catch (SQLException ex2){
            LogUtil.severe(ex2.getLocalizedMessage());
            printErrors(ex2);
        }
    }

    /**
     * データベースに接続する
     */
    void connect(){
        try{
            LogUtil.info("Attempting connection to MySQL..");

            Properties connectionProperties = new Properties();
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("maxReconnects", "0");
            connection = DriverManager.getConnection(connectionString, connectionProperties);

            LogUtil.info("Connected MySQL database!");
        }catch (SQLException ex){
            LogUtil.severe("Could not connect MySQL database!");
            ex.printStackTrace();
            printErrors(ex);
        }
    }

    /**
     * データベース構造を構築する
     */
    public void createStructure(){
        // for Register command
        /*
        write("CREATE TABLE IF NOT EXISTS `regist_key` (" +
              "`data_id` int (10) unsigned NOT NULL AUTO_INCREMENT," + 
              "`player_name` varchar(32) NOT NULL," +
              "`key` varchar(10) NOT NULL," +
              "`expired` int(32) unsigned NOT NULL," +
              "PRIMARY KEY (`data_id`)," +
              "UNIQUE KEY `player_name` (`player_name`)" +
              ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
        
        /*
        write("CREATE TABLE IF NOT EXISTS `" + userTable + "` (" +
                "`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +   // 割り当てられたプレイヤーID
                "`player_name` varchar(32) NOT NULL," +                     // プレイヤー名
                "`status` int(2) unsigned NOT NULL DEFAULT '0'," +          // ステータス
                "PRIMARY KEY (`player_id`)," +
                "UNIQUE KEY `player_name` (`player_name`)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");

        // 広告テーブル
        write("CREATE TABLE IF NOT EXISTS `" + dataTable + "` (" +
                "`data_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +     // 割り当てる広告ID
                "`player_id` int(10) unsigned NOT NULL," +                  // 割り当てられたプレイヤーID
                "`status` int(2) unsigned NOT NULL DEFAULT '0'," +          // ステータス
                "`registered` int(32) unsigned NOT NULL," +                 // 登録した日時
                "`expired` int(32) unsigned NOT NULL," +                    // 登録した日時
                "`text` varchar(255) NOT NULL," +                           // 広告文
                "`view_count` int(10) unsigned NOT NULL DEFAULT '0'," +     // 割り当てられたプレイヤーID
                "`view_players` int(10) unsigned NOT NULL DEFAULT '0'," +   // 割り当てられたプレイヤーID
                "PRIMARY KEY (`data_id`)" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
        */
    }

    /* ******************** */
    /**
     * 書き込みのSQLクエリーを発行する
     * @param sql 発行するSQL文
     * @return クエリ成功ならtrue、他はfalse
     */
    public boolean write(String sql){
        return write(sql, new Object[0]);
    }
    public boolean write(String sql, Object... obj){
        // 接続確認
        if (isConnected()){
            PreparedStatement statement = null;
            try{
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0){
                    for (int i = 0; i < obj.length; i++){
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                statement.executeUpdate(); // 実行
            }
            catch (SQLException ex){
                printErrors(ex);
                return false;
            }
            finally{
                // 後処理
                try {
                    if (statement != null)
                        statement.close();
                }catch (SQLException ex) { printErrors(ex); }
            }
            return true;
        }
        // 未接続
        else{
            attemptReconnect();
        }

        return false;
    }

    @Deprecated
    public boolean write(PreparedStatement statement){
        // 接続確認
        if (isConnected()){
            try{
                statement.executeUpdate(); // 実行

                // 後処理
                statement.close();

                return true;
            }catch (SQLException ex){
                printErrors(ex);

                return false;
            }
        }
        // 未接続
        else{
            attemptReconnect();
        }

        return false;
    }
    @Deprecated
    public PreparedStatement getPreparedStatement(String sql){
        // 接続確認
        if (isConnected()){
            try{
                PreparedStatement statement = connection.prepareStatement(sql);
                return statement;
            }catch (SQLException ex){
                printErrors(ex);
                return null;
            }
        }
        // 未接続
        else{
            attemptReconnect();
        }
        return null;
    }

    /* ******************** */
    /**
     * 読み出しのSQLクエリーを発行する
     * @param sql 発行するSQL文
     * @return SQLクエリで得られたデータ
     */
    public HashMap<Integer, ArrayList<String>> read(String sql){
        return read(sql, new Object[0]);
    }
    public HashMap<Integer, ArrayList<String>> read(String sql, Object... obj){
        ResultSet resultSet = null;
        HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

        // 接続確認
        if (isConnected()){
            PreparedStatement statement = null;
            try{
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0){
                    for (int i = 0; i < obj.length; i++){
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                resultSet = statement.executeQuery(); // 実行

                // 結果のレコード数だけ繰り返す
                while (resultSet.next()){
                    ArrayList<String> column = new ArrayList<String>();

                    // カラム内のデータを順にリストに追加
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++){
                        column.add(resultSet.getString(i));
                    }

                    // 返すマップにレコード番号とリストを追加
                    rows.put(resultSet.getRow(), column);
                }
            }catch (SQLException ex){
                printErrors(ex);
            }
            finally{
                // 後処理
                try {
                    if (statement != null)
                        statement.close();
                }catch (SQLException ex) { printErrors(ex); }

                try {
                    if (resultSet != null)
                        resultSet.close();
                }catch (SQLException ex) { printErrors(ex); }
            }
        }
        // 未接続
        else{
            attemptReconnect();
        }

        return rows;
    }


    /* ******************** */
    /**
     * int型の値を取得します
     * @param sql 発行するSQL文
     * @return 最初のローにある数値
     */
    public int getInt(String sql){
        return getInt(sql, new Object[0]);
    }
    public int getInt(String sql, Object... obj){
        ResultSet resultSet = null;
        int result = 0;

        // 接続確認
        if (isConnected()){
            PreparedStatement statement = null;
            try{
                statement = connection.prepareStatement(sql);
                // バインド
                if (obj != null && obj.length > 0){
                    for (int i = 0; i < obj.length; i++){
                        statement.setObject(i + 1, obj[i]);
                    }
                }
                resultSet = statement.executeQuery(); // 実行

                if (resultSet.next()){
                    result = resultSet.getInt(1);
                }else{
                    // 結果がなければ0を返す
                    result = 0;
                }
            }catch (SQLException ex){
                printErrors(ex);
            }
            finally{
                // 後処理
                try {
                    if (statement != null)
                        statement.close();
                }catch (SQLException ex) { printErrors(ex); }

                try {
                    if (resultSet != null)
                        resultSet.close();
                }catch (SQLException ex) { printErrors(ex); }
            }
        }
        // 未接続
        else{
            attemptReconnect();
        }

        return result;
    }

    /**
     * 接続状況を返す
     * @return 接続中ならtrue、タイムアウトすればfalse
     */
    public boolean isConnected(){
        if (connection == null){
            return false;
        }

        try{
            return connection.isValid(3);
        }catch (SQLException ex){
            return false;
        }
    }

    /**
     * MySQLデータベースへ再接続を試みる
     */
    public void attemptReconnect(){
        final int RECONNECT_WAIT_TICKS = 60000;
        final int RECONNECT_DELAY_TICKS = 1200;

        if (reconnectTimestamp + RECONNECT_WAIT_TICKS < System.currentTimeMillis()){
            reconnectTimestamp = System.currentTimeMillis();
            LogUtil.severe("Conection to MySQL was lost! Attempting to reconnect 60 seconds...");
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MySQLReconnect(plugin, this), RECONNECT_DELAY_TICKS);
        }
    }

    /**
     * エラーを出力する
     * @param ex
     */
    private void printErrors(SQLException ex){
        LogUtil.warning("SQLException:" +ex.getMessage());
        LogUtil.warning("SQLState:" +ex.getSQLState());
        LogUtil.warning("ErrorCode:" +ex.getErrorCode());

        LogUtil.warning("Stack Trace:");
        ex.printStackTrace();
    }
}
