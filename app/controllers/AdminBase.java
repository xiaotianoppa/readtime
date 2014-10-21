package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.After;
import play.mvc.Before;

public class AdminBase extends CRUD{

    private static final Logger log = LoggerFactory.getLogger(AdminBase.class);
            
    @Before
    public static void startTime() {
        log.info("Request For " + request.url + ":" + request.action + " Starts, ip: " + ControllerUtils.getRemoteIp());
        request.args.put("_ts", System.currentTimeMillis());
        
        CheckUserLogin.checkForAdmin();
        
        if(CheckUserLogin.checkHasRight()){
          
        }else{
            ControllerUtils.redirectToQTNoAuth();
        }
       
    }
    
    @After
    public static void endTime() {
        log.info("Action [" + request.url + "] took "
                + (System.currentTimeMillis() - (Long) request.current().args.get("_ts")) + " ms");
    }
    
}
