package models;

import javax.persistence.Entity;
import javax.persistence.Transient;

import models.User.LoginUserRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.db.jpa.Model;
import transaction.CodeGenerator.DBDispatcher;
import transaction.CodeGenerator.PolicySQLGenerator;
import transaction.DBBuilder.DataSrc;

@Entity(name = LoginUser.TABLE_NAME)
public class LoginUser extends Model implements PolicySQLGenerator<Long> {

    private static final Logger log = LoggerFactory.getLogger(LoginUser.class);

    public static final String TABLE_NAME = "login_user";

    @Transient
    public static LoginUser Empty = new LoginUser();

    @Transient
    public static DBDispatcher dp = new DBDispatcher(DataSrc.BASIC, Empty);
    
    private String username;
    
    private String email;

    private String password;
    
    public static class LoginUserRole {
        public static final int Normal = 1;
        public static final int QTMember = 2;//会员
        public static final int Admin = 4;//管理员
        
        public static final int SuperAdmin = 8;//超级管理员
    }
    
    private int userRole;
       
    private String headerImage;//头像地址
    
    private long createTs;
    
    private long updateTs;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }
    
    public boolean isSuperAdminRole() {
        return LoginUserRole.SuperAdmin ==  userRole;
    }

    public boolean isAdminRole() {
        return LoginUserRole.Admin ==  userRole;
    }

    public boolean isMemberRole() {
        return LoginUserRole.QTMember ==  userRole;
    }
    
    public boolean isNormalrRole() {
        return LoginUserRole.Normal ==  userRole;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public long getCreateTs() {
        return createTs;
    }

    public void setCreateTs(long createTs) {
        this.createTs = createTs;
    }

    public long getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(long updateTs) {
        this.updateTs = updateTs;
    }

    public LoginUser() {
        super();
    }
    
    public LoginUser(String email, String password, String username) {
        this.email = email;
        this.username = username;
        this.password = password;
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
    
    public static long findExistId(long codeId) {

        String query = " select id from " + TABLE_NAME + " where id = ? ";

        return dp.singleLongQuery(query, codeId);
    }

    @Override
    public boolean jdbcSave() {
        long existId = findExistId(this.getId());

        if (existId <= 0L) {
            return rawInsert();
        } else {
            this.setId(existId);
            return rawUpdate();
        }
    }

    public boolean rawInsert() {

        String insertSQL = "insert into `" + TABLE_NAME + "`(`username`," +
                "`password`,`email`,`userRole`," +
                "`headerImage`,`createTs`,`updateTs`) " +
                " values(?,?,?,?,?,?,?)";

        this.createTs = System.currentTimeMillis();
        this.updateTs = this.createTs;
        
        long id = dp.insert(insertSQL,this.username, 
                this.password, this.email,this.userRole,
                this.headerImage, this.createTs, this.updateTs);

        if (id > 0L) {
            this.setId(id);
            return true;
        } else {
            log.error("Insert Fails....." + "[id : ]" + this.getId());
            return false;
        }

    }

    public boolean rawUpdate() {

        String updateSQL = "update `" + TABLE_NAME + "` set `username` = ?," +
                "`password` = ?, `email` = ?, `headerImage` = ?, " +
                "`userRole` = ?  where `id` = ? ";

        this.updateTs = System.currentTimeMillis();
        
        long updateNum = dp.update(updateSQL, this.username, 
                this.password, this.email, this.headerImage,
                this.userRole,  this.getId());

        if (updateNum == 1) {
            return true;
        } else {
            log.error("update failed...for :" + this.getId() );
            return false;
        }
    }
    

    
}
