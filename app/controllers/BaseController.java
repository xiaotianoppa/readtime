package controllers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

public class BaseController extends Controller {
    
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    //
    @Before
    public static void startTime() {
        log.info("Request For " + request.url + ":" + request.action + " Starts, ip: " + ControllerUtils.getRemoteIp());
        request.args.put("_ts", System.currentTimeMillis());
        
        
    }
    
    @After
    public static void endTime() {
        log.info("Action [" + request.url + "] took "
                + (System.currentTimeMillis() - (Long) request.current().args.get("_ts")) + " ms");
    }
    
}
