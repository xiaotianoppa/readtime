package models;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.jpa.Model;
import transaction.CodeGenerator.DBDispatcher;
import transaction.CodeGenerator.PolicySQLGenerator;
import transaction.DBBuilder.DataSrc;

@Entity(name = UserLoginLog.TABLE_NAME)
public class UserLoginLog extends Model implements PolicySQLGenerator<Long> {

    private static final Logger log = LoggerFactory.getLogger(UserLoginLog.class);

    public static final String TABLE_NAME = "user_login_log";

    @Transient
    public static UserLoginLog Empty = new UserLoginLog();

    @Transient
    public static DBDispatcher dp = new DBDispatcher(DataSrc.BASIC, Empty);
    
    private Long userId;

    private boolean isRegister;
    
    private String ip;
    
    private long loginTs;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLoginTs() {
        return loginTs;
    }

    public void setLoginTs(long loginTs) {
        this.loginTs = loginTs;
    }

    public UserLoginLog() {
        super();
    }

    public UserLoginLog(Long userId, String ip, boolean isRegister) {
        super();
        this.userId = userId;
        this.ip = ip;
        this.isRegister = isRegister;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getTableHashKey(Long t) {
        return null;
    }

    @Override
    public String getIdColumn() {
        return "id";
    }

    @Override
    public String getIdName() {
        return "id";
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean jdbcSave() {
        return rawInsert();
    }

    public boolean rawInsert() {
        
        try {
            String insertSQL = "insert into `" + TABLE_NAME + "`(`userId`,`ip`," +
                    "`isRegister`,`loginTs`) " +
                    " values(?,?,?,?)";
            
            this.loginTs = System.currentTimeMillis();

            long id = dp.insert(insertSQL, this.userId, this.ip,
                    this.isRegister, this.loginTs);

            if (id > 0L) {
                return true;
            } else {
                log.error("Insert Fails....." + "[userId : ]" + this.userId);
                return false;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }

    }
    
    
    public static boolean addUserLoginLog(Long userId, String ip, boolean isRegister) {
        UserLoginLog loginLog = new UserLoginLog(userId, ip, isRegister);
        
        return loginLog.rawInsert();
    }
    
}
