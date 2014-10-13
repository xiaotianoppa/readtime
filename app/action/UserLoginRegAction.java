package action;


import models.ALSession;
import models.User;
import models.UserLoginLog;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import result.ALResult;
import utils.PlayUtil;

public class UserLoginRegAction {

    private static final Logger log = LoggerFactory.getLogger(UserLoginRegAction.class);
 
    private final static String USER_ARG_KEY = "_user_";
    
    public static ALResult<ALSession> userRegister(String userName, String password, String email, String ip) {
        
        boolean isRegister = true;
        
        email = PlayUtil.trimValue(email);
        userName = PlayUtil.trimValue(userName);
        password = PlayUtil.trimValue(password);

        if (StringUtils.isEmpty(email)) {
            return afterLoginFail("请先输入注册邮箱！", isRegister);
        }
        if (StringUtils.isEmpty(userName)) {
            return afterLoginFail("请先输入用户名！", isRegister);
        }
        if (StringUtils.isEmpty(password)) {
            return afterLoginFail("请先输入密码！", isRegister);
        }
        
        if (User.findByName(userName)!=null) {
            log.info("[exist userName:]" + userName);
            return afterLoginFail("该用户名已被注册！", isRegister);
        }

        if (userName.length() < 2) {
            return afterLoginFail("用户名太短了,只有[" + userName.length() + "]个字符,至少需要2个字符！", isRegister);
        }
        if (userName.length() > 20) {
            return afterLoginFail("用户名太长了,都[" + userName.length() + "]个字符了,最多只能20个字符！", isRegister);
        }

        ALResult passwordRes = checkNewPassword(password);
        if (passwordRes.isOk() == false) {
            return afterLoginFail(passwordRes.getMsg(), isRegister);
        }

        User user = User.saveUser(email, password, userName);  
        return afterLoginSuccess(user.id, ip, isRegister);
    }
    
    protected static ALResult checkNewPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return new ALResult(false, "请先输入密码！", null);
        }
        
        if (password.length() < 4) {
            return new ALResult(false, "密码太短了,只有[" + password.length() + "]个字符,至少需要4个字符！", null);
        }
        if (password.length() > 64) {
            return new ALResult(false, "密码太长了,都[" + password.length() + "]个字符了,最多只能64个字符！", null);
        }
        
        return new ALResult(true, "", null);
        
    }
    
    
    public static ALResult<ALSession> userLogin(String userName, String password,
            String ip) {
        
        userName = PlayUtil.trimValue(userName);
        password = PlayUtil.trimValue(password);
        
        boolean isRegister = false;
        
        if (StringUtils.isEmpty(userName)) {
            return afterLoginFail("请先输入用户名！", isRegister);
        }
        if (StringUtils.isEmpty(password)) {
            return afterLoginFail("请先输入密码！", isRegister);
        }
      
        if(User.findByName(userName)==null){
            return afterLoginFail("用户不存在！", isRegister);
        }
        
        if(User.login(userName, password) == null){
            return afterLoginFail("用户名或密码错误！", isRegister); 
        }        
        
        User user = User.login(userName, password);
        return afterLoginSuccess(user.id, ip, isRegister);
    }
    
    
    // 登录的时候，也可能出现User不存在的情况
    private static ALResult<ALSession> afterLoginSuccess(Long userId, String ip, boolean isRegister) {

        //存储登录log
        boolean isSuccess = UserLoginLog.addUserLoginLog(userId, ip, isRegister);
        if (isSuccess == false) {
            return new ALResult<ALSession>(false, "系统出现异常，请重试或联系我们！");
        }
        
        //创建ALSession
        ALSession alSession = ALSession.createOne(userId);
        if (alSession == null) {
            return new ALResult<ALSession>(false, "系统出现异常，请重试或联系我们！");
        }
        ALResult<ALSession> sessionRes = new ALResult<ALSession>(alSession);
        
        return sessionRes;
    }
    
    
    private static ALResult<ALSession> afterLoginFail(String message, boolean isRegister) {
        log.error("fail to register or login: " + message + "------------------------");
        return new ALResult<ALSession>(false, message);
    }
    
}
