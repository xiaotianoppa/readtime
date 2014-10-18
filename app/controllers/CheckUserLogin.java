
package controllers;

import models.User;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import action.LoginUserGetAction;

import com.ciaosir.client.utils.JsonUtil;

import play.Play;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;
import result.ALResult;
import utils.PlayUtil;

public class CheckUserLogin extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CheckUserLogin.class);
    
    private final static String USER_ARG_KEY = "_user_";
   
    @Before(only = {"UserAdmin.index"})
    static void checkForNormal(){
        checkIsLogin(false);
    }
   
   //@Before(only = {"Admin.index"})
   static void checkForAdmin(){
       checkIsLogin(true);
   }
    
    static void checkIsLogin( boolean isAdmin) {

         User user = (User) request.args.get(USER_ARG_KEY);
        
        if (user != null) {
           return;
        }
         
        String sid = ALLogin.getSidFromCookie();
        
        if (StringUtils.isEmpty(sid)) {
            ControllerUtils.renderLoginFail( isAdmin, "您尚未登录，请先登录！");
        }
        
        String ip = ControllerUtils.getRemoteIp();
        user = LoginUserGetAction.fetchUserBySid(sid, ip);
        
        if (user == null) {
            ControllerUtils.renderLoginFail( isAdmin, "您的登录已过期，请先登录！");
        }
        
        ALLogin.putSidToCookie(sid);     
        request.args.put(USER_ARG_KEY, user);
    
    }
 
    static User checkLogin(){
        User user = (User) request.args.get(USER_ARG_KEY);     
        return user;
    }
    
    static User login() {
        
        User user = (User) request.args.get(USER_ARG_KEY);
        
        if (user == null) {
            ControllerUtils.renderLoginFail( false, "找不到用户，您尚未登录，请先登录！");
        }
        
        return user;
    }  
    
    static User connect(){
        User user = (User) request.args.get(USER_ARG_KEY);
        
        if (user == null) {
            ControllerUtils.renderLoginFail( true, "找不到用户，您尚未登录，请先登录！");
        }
        
        return user;
    }
    
    static boolean checkHasRight(){
        return (isSuperAdminRole() ||  isAdminRole());
    }
    
    static boolean isSuperAdminRole() {
        User user = connect();
        return user.isSuperAdminRole();
    }
    
    static boolean isAdminRole() {
        User user = connect();
        return user.isAdminRole();
    }
    
    static boolean isMemberRole() {
        User user = connect();
        return user.isMemberRole();
    }
    
    static boolean isNormalrRole() {
        User user = connect();
        return user.isNormalrRole();
    }
    

}
