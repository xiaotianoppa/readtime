package action;

import models.ALSession;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.cache.Cache;

public class LoginUserGetAction {

    private static final Logger log = LoggerFactory.getLogger(LoginUserGetAction.class);
    
    public static User fetchUserBySid(String sid, String ip) {
        /**
         * 添加cache
         */
        if (StringUtils.isEmpty(sid)) {
            return null;
        }
        
        ALSession session = ALSession.checkIsOutDate(sid);
        
        if (session == null) {
            return null;
        }
        
        User user = User.findByUserId(session.getUserId());
        
        return user;
    }
    
    public static User fetchByUserId(Long userId) {
        
        User user = User.findByUserId(userId);
        
        return user;
        
    }
    
    public static User fetchByUserName(String userName) {
        
        User user = User.findByName(userName);
        
        return user;
        
    }
    
    
    public static class UserSessionIdCache {
        
        private static final String Prefix = "_User_Session_Cache_";
        
        private static String genCacheKey(String sid) {
            return Prefix + sid;
        }
        
        public static void putToCache(String sid, User user) {
            if (user == null) {
                return;
            }
            
            String key = genCacheKey(sid);
            
            try {
                Cache.set(key, user, "12h");
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return;
            }
            
        }
        
        public static User getFromCache(String sid) {
            if (StringUtils.isEmpty(sid)) {
                return null;
            }
            
            String key = genCacheKey(sid);
            
            try {
                User user = (User) Cache.get(key);
                return user;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
            
        }
        
    }
}
