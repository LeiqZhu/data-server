package util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JdbcUtil {

    public static JdbcUtil jdbcUtil = new JdbcUtil();

    private static volatile DataSource dataSource;

    static{
        dataSource = initDataSource();
    }

    public static DataSource initDataSource(){
        BasicDataSource dataSource = new BasicDataSource();

        Properties properties = PropertyUtil.getInstance("db.properties");

        dataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));

        dataSource.setInitialSize(Integer.parseInt(properties.getProperty("jdbc.initialSize")));
        dataSource.setMaxTotal(Integer.parseInt(properties.getProperty("jdbc.maxTotal")));
        dataSource.setMinIdle(Integer.parseInt(properties.getProperty("jdbc.minIdle")));
        dataSource.setMaxIdle(Integer.parseInt(properties.getProperty("jdbc.maxIdle")));
        dataSource.setMaxWaitMillis(Long.parseLong(properties.getProperty("jdbc.maxWait")));
        if (properties.containsKey("jdbc.maxConnLifetime")) {
            dataSource.setMaxConnLifetimeMillis(Long.parseLong(properties.getProperty("jdbc.maxConnLifetime")));
        }
        if (properties.containsKey("jdbc.connectionProperties")) {
            dataSource.setConnectionProperties(properties.getProperty("jdbc.connectionProperties"));
        }
        return dataSource;
    }

    private DataSource getDataSource() {
        return dataSource;
    }

    public synchronized Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public int insert(String sql, Object[] params) throws SQLException {

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int id = 0;
        try {
            conn = getConnection();
            conn.setReadOnly(false);

            pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstm.setObject(i + 1, params[i]);
                }
            }
            pstm.executeUpdate();
            rs = pstm.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                DbUtils.closeQuietly(conn);
            }
        }
        return id;
    }

    public Map<String, Object> queryOne(String sql, Object[] params) throws SQLException {

        Connection conn = null;
        QueryRunner qr = new QueryRunner();
        Map<String, Object> results = null;
        try {
            conn = getConnection();
            conn.setReadOnly(true);

            results = qr.query(conn, sql, new MapHandler(), params);
        }
        finally {
            if (conn != null) {
                DbUtils.closeQuietly(conn);
            }
        }
        return results;

    }

    public List<Map<String, Object>> getAllMapList(String sql) throws Exception {

        Connection conn = null;
        QueryRunner qr = new QueryRunner();
        List<Map<String, Object>> results = null;
        try {
            conn = getConnection();

            results = qr.query(conn, sql, new MapListHandler());

        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            try {
                DbUtils.closeQuietly(conn);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;

    }

    public static void main(String[] args) throws SQLException {
        String sql = "INSERT IGNORE INTO fans_status(stat,speed,shake,timer_on,timer_off,time) VALUES(?,?,?,?,?,?)";
        Object[] param = {1,1,1,0,0,"2018-04-28 12:12:38"};
        JdbcUtil.jdbcUtil.insert(sql,param);
    }
}
