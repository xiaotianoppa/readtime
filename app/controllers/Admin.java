package controllers;

import models.ALSession;
import action.UserLoginRegAction;
import play.mvc.With;
import result.ALResult;


public class Admin extends CheckUserLogin{
         
    public static void index() {
        
        if(checkHasRight()){
            render("/admin/index.html");
        }else{
            ControllerUtils.redirectToQTNoAuth();
        }
       
     }
    
}
