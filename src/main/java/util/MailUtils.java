package util;

import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 发邮件工具类
 */
public final class MailUtils {

    public static void saveProps(Properties props){
        try {
            props.put("mail.smtp.auth","true");
            props.put("mail.pop3.auth","true");
            FileOutputStream fos = new FileOutputStream("conf.properties");
            props.store(fos,null);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param to 收件人邮箱
     * @param text 邮件正文
     * @param title 标题
     * @param attach 附件
     */
    /* 发送验证信息的邮件 */
    public static boolean sendMail(String to, String text, String title, String attach){
        try {
            Properties props = getProps();

            Session mailSession = getSession(props);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            message.setFrom(new InternetAddress(props.getProperty("mail.user")));
            // 设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 设置邮件标题
            message.setSubject(title);
            //创建用于封装邮件的Multipart对象
            Multipart mp = new MimeMultipart();
            //处理邮件正文
            MimeBodyPart mbpText = new MimeBodyPart();
            mbpText.setText(text);
            mp.addBodyPart(mbpText);
            // 处理邮件附件
            BASE64Encoder enco = new BASE64Encoder();
            if(!"".equals(attach)){
                String[] attatchFiles = attach.split(",");
                for (String attatchFile : attatchFiles) {
                    MimeBodyPart mbpAttatch = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(attatchFile);
                    mbpAttatch.setDataHandler(new DataHandler(fds));
                    //将文件名进行BASE64编码
                    String sendFileName = "=?GB2312?B?" + enco.encode(new String(
                            fds.getName().getBytes(), "gb2312").getBytes("gb2312")) + "?=";
                    mbpAttatch.setFileName(sendFileName);
                    mp.addBodyPart(mbpAttatch);
                }
            }

            //封装并保存邮件信息
            message.setContent(mp);
            message.saveChanges();

            // 发送邮件
            Transport.send(message,message.getAllRecipients());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Folder receiveMail(){
        try {
            Properties props = getProps();
            Session session = getSession(props);
            Store store = session.getStore("pop3");
            store.connect((String)props.get("mail.user"),(String)props.get("mail.password"));
            //取得store中的Folder邮件夹
            return store.getFolder("Inbox");
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Session getSession(Properties props){
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        return Session.getInstance(props, authenticator);
    }

    public static Properties getProps() throws IOException {
        Properties props = new Properties();

        InputStream fis = new FileInputStream("conf.properties");

        props.load(fis);
        fis.close();
        return props;
    }
}
