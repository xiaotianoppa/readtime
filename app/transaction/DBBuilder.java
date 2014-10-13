
package transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.api.domain.JdpUser;

import play.Play;

public class DBBuilder {

    public static final int HASH_NUM = 32;

    public static final long HASH_NUM_LONG = 32;

    private static final Logger log = LoggerFactory.getLogger(DBBuilder.class);

    public static final String TAG = "DBBuilder";

    static {
        log.info("Init jdbc pool");
        DBBuilder.initPool();
    }

    public enum DataSrc {
        BASIC, QUOTA, RDS, JDP;

        private String username;

        private String password;

        private String url;

        private String driver;

        private DataSrc() {
        }

        private DataSrc(String username, String password, String url) {
            this.username = username;
            this.password = password;
            this.url = url;
        }

        public void setAll(String username, String password, String url, String dirver) {
            this.username = username;
            this.password = password;
            this.url = url;
            this.driver = dirver;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

    }

    private static BasicDataSource baseDataSrc;

    private static BasicDataSource quotaDataSrc;

    private static BasicDataSource rdsDataSrc;

    private static BasicDataSource jdpDataSrc;

    public static Connection getConn() {
        return getConn(DataSrc.BASIC);
    }

    static ThreadLocal<Connection> threadConn = new ThreadLocal<Connection>();

    public static ThreadLocal<Connection> getThreadConn() {
        return threadConn;
    }

    /**
     * 判断当前的数据库连接，是否是通过JDBCTransactionSecurity创建的
     * @return
     */
    public static boolean isConnFromTransactionSecurity() {
        Connection conn = threadConn.get();
        if (conn != null) {
            return true;
        } else {
            return false;
        }
    }

    public static synchronized Connection getConn(DataSrc src) {
        Connection conn = threadConn.get();
        if (conn != null) {
            return conn;
        }

        try {
            switch (src) {
                case BASIC:
                    return baseDataSrc.getConnection();
                case QUOTA:
                    return quotaDataSrc.getConnection();
                case RDS:
                    return rdsDataSrc.getConnection();
                case JDP:
                    return jdpDataSrc.getConnection();
                default:
                    return baseDataSrc.getConnection();
            }
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    private static synchronized void initPool() {
        Properties prop = Play.configuration;
        baseDataSrc = new BasicDataSource();
        rdsDataSrc = new BasicDataSource();
        quotaDataSrc = new BasicDataSource();
        jdpDataSrc = new BasicDataSource();

        int maxMainConnSize = Integer.parseInt(prop.getProperty("db.pool.maxSize", "32"));
        int minPoolSize = Integer.parseInt(prop.getProperty("db.pool.minSize", "16"));

        String defaultDriver = "com.mysql.jdbc.Driver";

        defaultDriver = prop.getProperty("base.db.driver", defaultDriver);
        String defaultUrl = prop.getProperty("base.db.url");
        log.info("[db url:]" + defaultUrl);

        if (StringUtils.isEmpty(defaultUrl)) {
            defaultUrl = prop.getProperty("db.url");
        }

        log.info("[db url:]" + defaultUrl);

        String defaultUsername = prop.getProperty("base.db.user");
        if (StringUtils.isEmpty(defaultUsername)) {
            defaultUsername = prop.getProperty("db.user");
        }

        String defaultPassword = prop.getProperty("base.db.pass");
        if (StringUtils.isEmpty(defaultPassword)) {
            defaultPassword = prop.getProperty("db.pass");
        }

        baseDataSrc.setDriverClassName(defaultDriver);
        baseDataSrc.setUrl(defaultUrl);
        baseDataSrc.setUsername(defaultUsername);
        baseDataSrc.setPassword(defaultPassword);
        baseDataSrc.setMaxActive(maxMainConnSize);
        DataSrc.BASIC.setAll(defaultUsername, defaultPassword, defaultUrl, defaultDriver);

        String quotaUrl = prop.getProperty("quota.db.url", defaultUrl);
        String quotaUserName = prop.getProperty("quota.db.user", defaultUsername);
        String quotaPassword = prop.getProperty("quota.db.pass", defaultPassword);
        log.error("quota url :" + quotaUrl);
        quotaDataSrc.setUrl(quotaUrl);
        quotaDataSrc.setUsername(quotaUserName);
        quotaDataSrc.setPassword(quotaPassword);
        quotaDataSrc.setDriverClassName(prop.getProperty("quota.db.driver", defaultDriver));
        quotaDataSrc.setMaxActive(maxMainConnSize);
        DataSrc.QUOTA.setAll(quotaUserName, quotaPassword, quotaUrl, defaultDriver);

        String rdsUrl = prop.getProperty("rds.db.url", defaultUrl);
        String rdsUserName = prop.getProperty("rds.db.user", defaultUsername);
        String rdsPassword = prop.getProperty("rds.db.pass", defaultPassword);
        log.error("rds url :" + rdsUrl);
        rdsDataSrc.setUrl(rdsUrl);
        rdsDataSrc.setUsername(rdsUserName);
        rdsDataSrc.setPassword(rdsPassword);
        rdsDataSrc.setDriverClassName(defaultDriver);
        rdsDataSrc.setMaxActive(maxMainConnSize);
        DataSrc.RDS.setAll(rdsUserName, rdsPassword, rdsUrl, defaultDriver);

        DataSrc.JDP.setAll(prop.getProperty("jdp.db.user", defaultUsername),
                prop.getProperty("jdp.db.pass", defaultPassword),
                prop.getProperty("jdp.db.url", defaultUrl), defaultDriver);

        jdpDataSrc.setUrl(DataSrc.JDP.getUrl());
        jdpDataSrc.setUsername(DataSrc.JDP.getUsername());
        jdpDataSrc.setPassword(DataSrc.JDP.getPassword());
        jdpDataSrc.setDriverClassName(defaultDriver);
        jdpDataSrc.setMaxActive(maxMainConnSize);
        log.error(" jdp url:" + DataSrc.JDP.getUrl());

        BasicDataSource[] srcs = new BasicDataSource[] {
                baseDataSrc, quotaDataSrc, rdsDataSrc, jdpDataSrc
        };

        for (BasicDataSource src : srcs) {
            src.setMaxActive(maxMainConnSize);
            src.setInitialSize(minPoolSize);
            src.setMaxIdle(20);
            src.setMaxWait(5000L);
            src.setTestOnBorrow(true);
        }
    }

    public static Map<Long, String> offsetCache = new HashMap<Long, String>(512);

    public static String genVisitLogHashKey(Long seed, Long ts) {
        long key = seed.longValue() + ts.longValue();
        if (offsetCache.containsKey(key)) {
            return offsetCache.get(key);
        }

        int dayOffset = 0;
        int monthOffset = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.longValue());
        int month = cal.get(Calendar.MONTH) + 1;
        int monthDay = cal.get(Calendar.DAY_OF_MONTH);
        if (monthDay <= 7) {
            dayOffset = 0;
        } else if (monthDay <= 15) {
            dayOffset = 1;
        } else if (monthDay <= 22) {
            dayOffset = 2;
        } else {
            dayOffset = 3;
        }

        monthOffset = month % 4;

        // Totally 512 tables;
        int hash = (int) (seed.longValue() % 32L) + monthOffset * 128 + dayOffset * 32;
        String res = Integer.toString(hash);
        offsetCache.put(key, res);

        return res;
    }

    public static long genUserIdHashKey(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userId % HASH_NUM;
    }
    
    public static String genTaskIdHashKey(String masterTaskId){
    	return masterTaskId.substring(0, 1).toLowerCase();
    }
    
    public static long genUserNickHashKey(String userNick) {
        if (StringUtils.isEmpty(userNick)) {
            return 0;
        }
        return userNick.hashCode() % HASH_NUM;
    }

    public static String getStatus() {

        StringBuilder sb = new StringBuilder();
        sb.append(" [Base Data Src] active num:" + baseDataSrc.getNumActive() + " with max :"
                + baseDataSrc.getMaxActive());

        return sb.toString();
    }
}
