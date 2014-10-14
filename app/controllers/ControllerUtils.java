
package controllers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciaosir.client.utils.JsonUtil;
import com.ciaosir.client.utils.NetworkUtil;

import play.Play;
import play.mvc.Controller;
import result.ALResult;

public class ControllerUtils extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ControllerUtils.class);

    public static final String TAG = "ControllerUtils";
 
    static void renderLoginFail(boolean isAdmin, String message) {

        if (isAdmin == false) {
            redirectToLogin(request.url);
        }else{
            toAdminLogin(request.url);
        }
        
        ALResult res = new ALResult(false, message, null);
        
        res.setNeedWarnLogin(true);
        
        renderJSON(JsonUtil.getJson(res));
       
        
    }
   
    static void redirectToLogin(String redirectURL) {
        ALLogin.index(redirectURL);
    }
    
    static void toAdminLogin(String redirectURL) {
        ALLogin.admin(redirectURL);
    }
    
    static void redirectToQTNoAuth() {
        toAdminLogin(request.url);
    }
    
    static void renderAjaxQTNoAuth() {
        renderError("您当前权限不够，不能进行此操作！");
    }
     
    static void renderError(String message) {
        ALResult res = new ALResult(false, message, null);
        
        renderJSON(JsonUtil.getJson(res));
    }
    
    static void renderSuccess(String message) {
        ALResult res = new ALResult(true, message, null);
        
        renderJSON(JsonUtil.getJson(res));
    }
    
    static void renderResultJson(Object obj) {
        ALResult res = new ALResult(true, "", obj);
        
        renderJSON(JsonUtil.getJson(res));
    }

    static void renderALResult(ALResult res) {
        //log.info("[res:]" + res);
        renderJSON(JsonUtil.getJson(res));
    }

    static void renderMockFileInJsonIfDev(String filename) throws IOException {
        if (Play.mode.isProd()) {
            return;
        }

        File mockDir = new File(Play.applicationPath, "conf/mock");
        if (!mockDir.exists()) {
            mockDir.mkdirs();
        }

        String str = FileUtils.readFileToString(new File(mockDir, filename));
        renderJSON(str);
    }
    
    
    static String getRemoteIp() {
        String ip = NetworkUtil.getRemoteIPForNginx(request);
        return ip;
    }

}
