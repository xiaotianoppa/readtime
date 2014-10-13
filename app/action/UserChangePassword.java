package action;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.User;
import result.ALResult;

public class UserChangePassword {

    private static final String Domain = "http://115.29.77.52/";

    private static final Logger log = LoggerFactory
            .getLogger(UserChangePassword.class);

    public static ALResult sendEmailVerification(User user) {

        if (user == null) {
            return new ALResult("没有找到用户信息");
        }

        final String email = user.email;

        int index = email.indexOf('@');
        String domainName = email.substring(index + 1);

        // 发送邮箱验证信息
        boolean isSuccess = doBuildVerification(user);
        if (isSuccess == false) {
            return new ALResult("邮箱验证发送失败，请重稍后新发送邮箱验证");
        } else {
            return new ALResult(true, "已发送至您的邮箱 ", domainName);
        }

    }

    private static boolean doBuildVerification(User user) {
        final String userName = user.username;
        final String email = user.email;

        final String un = "high_music@163.com";
        final String pw = "abc123456abc";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.auth", true);
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(un, pw);
            }
        });
        // session.setDebug(true);
        Message message = new MimeMessage(session);
        try {
            message.setSubject("找回您的嗨音乐密码");
            message.setHeader("Header", "找回您的嗨音乐密码");
            Address addressFrom = new InternetAddress("high_music@163.com");
            message.setFrom(addressFrom);
            Address addressTo = new InternetAddress(email);
            message.addRecipient(Message.RecipientType.TO, addressTo);
            String tmpl = FileUtils.readFileToString(new File("conf",
                    "sutui.forgetpassword.html"));
            String url = genModifyPwdUrl(user);
            if (StringUtils.isEmpty(url)) {
                return false;
            }
            String msg = String.format(tmpl, userName, userName, url, url, url);
            message.setContent(msg, "text/html; charset=utf-8");
            message.saveChanges();
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            log.warn(e.getMessage(), e);
            return false;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return false;
        }

    }

    public static String genModifyPwdUrl(User user) {

        try {
            String encodeUserName = URLEncoder.encode(user.username, "utf-8");
            // String url = Domain + "ALLogin/findPW?code=" +
            // findPWLog.getCode() + "&userName=" + encodeUserName;
            String url = Domain;
            return url;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "";
        }

    }

}
