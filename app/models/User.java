package models;

import java.io.File;

import javax.persistence.Entity;


import org.apache.commons.lang3.StringUtils;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
    @Email
    @Required
    public String email;
    @Required
    public String password;
    @Required
    public String username;
    
    public static class LoginUserRole {
        public static final int Normal = 1;
        public static final int QTMember = 2;//会员
        public static final int Admin = 4;//管理员
        
        public static final int SuperAdmin = 8;//超级管理员
    }
    
    public int userRole;
    
    public boolean isAdmin;
       
    public String headerImage;
    
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

    public User(String email, String password, String username) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    
    public User(String email, String password, String username, String headerImage) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.headerImage = headerImage;
    }
    
    public static User connect(String email ,String password){
        return find("byEmailAndPassword",email,password).first();
    } 
    
    public static User login(String username ,String password){
        return find("byusernameAndPassword",username,password).first();
    }
    
    public static User findByName(String username){
        return find("byusername",username).first();
    }
    
    public  static User saveUser(String email, String password, String username){
        User user = new User(email, password, username);
        user.save();
        return user;
    }
    
    public  static User saveUser(User user, String headerImage){
        String path = user.headerImage;
        if(path ==null || StringUtils.isEmpty(path.trim())){
          
        }else{
            File file = new File(path);
            if(file.exists()){
             file.delete();
            }
        }    
        user.headerImage = headerImage;
        user.save();
        return user;
    }
    
    public static User findByUserId(Long id){
        return find("byId",id).first();
    }
}
