
package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciaosir.client.CommonUtils;
import com.ciaosir.client.utils.DateUtil;

import play.db.jpa.GenericModel;
import play.libs.Codec;
import transaction.CodeGenerator.DBDispatcher;
import transaction.CodeGenerator.PolicySQLGenerator;
import transaction.DBBuilder.DataSrc;
import transaction.JDBCBuilder;

@Entity(name = ALSession.TABLE_NAME)
public class ALSession extends GenericModel implements PolicySQLGenerator<String> {

    public static final String TABLE_NAME = "al_session";

    private static final Logger log = LoggerFactory.getLogger(ALSession.class);

    public static final String TAG = "ALSession";
    
    @Transient
    public static ALSession Empty = new ALSession();

    @Transient
    public static DBDispatcher dp = new DBDispatcher(DataSrc.BASIC, Empty);

    /**
     * web login...
     *
     * web mission...
     */
    
    @Id
    private String id;
    
    private String src;

    private long created;

    private long updated;

    private long expiredAt;

    private Long userId;

    private String ip;
    
    public Long getUserId() {
        return userId;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIdString() {
        return id;
    }
    
    public void setIdString(String idString) {
        this.id = idString;
    }

    public ALSession() {
        super();
        if (this.id != null) {
            this.id = Codec.hexMD5(this.id) + UUID.randomUUID().toString().replaceAll("-", "");
        } else {
            this.id = Codec.hexMD5("!!!session" + System.currentTimeMillis())
                    + UUID.randomUUID().toString().replaceAll("-", "");
        }

        this.created = System.currentTimeMillis();
        this.expiredAt = this.created + DateUtil.DAY_MILLIS;
    }

    public ALSession(Long userId) {
        this();
        this.userId = userId;
    }

    public void doRefresh() {

    }

    public static ALSession checkIsOutDate(String sid) {
        ALSession session = ALSession.findById(sid);
        if (session == null) {
            return session;
        }
        long curr = System.currentTimeMillis();
        if (curr > session.expiredAt) {
            return null;
        }
  
        /**
         * TODO update later... and the ip should also be checked...
         */
        session.expiredAt = curr + DateUtil.DAY_MILLIS;
        session.jdbcSave();

        return session;
    }

    @Override
    public String toString() {
        return "ALSession [id=" + id + ",created=" + DateUtil.formDateForLog(created) + ", updated="
                + DateUtil.formDateForLog(updated) + ", expiredAt=" + DateUtil.formDateForLog(expiredAt) + ", userId="
                + userId + "]";
    }


    public static ALSession createOne(Long userId) {
        log.info("[create session for user:]" + userId);
        try {
            ALSession session = new ALSession(userId);
            session.jdbcSave();
            return session;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getTableHashKey(String t) {
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
    
 public static String findExistId(String id) {
        
        String query = " select id from " + TABLE_NAME + " where id = ? ";
        
        return dp.singleStringQuery(query, id);
    }
 
 @Override
 public boolean jdbcSave() {
     String existId = findExistId(this.id);

     if (StringUtils.isEmpty(existId)) {
         return rawInsert();
     } else {
         this.setId(existId);
         return rawUpdate();
     }
 }
 
 private boolean rawInsert() {
     
     String insertSQL = "insert into `" + TABLE_NAME + "`(`id`,`created`,`expiredAt`," +
             "`ip`,`src`,`updated`,`userId`) " +
             " values(?,?,?,?,?,?,?)";
     

     long id = dp.insert(insertSQL, this.id, this.created, this.expiredAt,
             this.ip, this.src, this.updated, this.userId);

     if (id > 0L) {
         return true;
     } else {
         log.error("Insert Fails....." + "[id : ]" + this.id);
         return false;
     }

 }

 private boolean rawUpdate() {

     String updateSQL = "update `" + TABLE_NAME + "` set `ip` = ?,`src` = ?, " +
             "`updated` = ?, `userId` = ? " +
             " where `id` = ? ";
     

     long updateNum = dp.update(updateSQL, this.ip, this.src, 
             this.updated, this.userId,
             this.getId());

     if (updateNum == 1) {
         return true;
     } else {
         log.error("update failed...for :" +  "[id : ]" + this.id);
         return false;
     }
 }
 
 public static ALSession findById(String id) {
     String query = " select " + SelectAllProperty + " from " + TABLE_NAME + " where id = ? ";
     
     return findByJDBC(query, id);
 }
 
 
 private static ALSession findByJDBC(String query, Object... params) {
     return new JDBCBuilder.JDBCExecutor<ALSession>(dp, query, params) {

         @Override
         public ALSession doWithResultSet(ResultSet rs) throws SQLException {
             if (rs.next()) {
                 return parseALSession(rs);
             } else {
                 return null;
             }
         }
         
         
     }.call();
 }
 
 private static final String SelectAllProperty = " id, created, expiredAt, " +
         "ip, src, updated, userId ";
 
 private static ALSession parseALSession(ResultSet rs) {
     try {
         
         ALSession ALSession = new ALSession();
         ALSession.setId(rs.getString(1));
         ALSession.setCreated(rs.getLong(2));
         ALSession.setExpiredAt(rs.getLong(3));
         ALSession.setIp(rs.getString(4));
         ALSession.setSrc(rs.getString(5));
         ALSession.setUpdated(rs.getLong(6));
         ALSession.setUserId(rs.getLong(7));
         return ALSession;
         
     } catch (Exception ex) {
         log.error(ex.getMessage(), ex);
         return null;
     }
 }

}
