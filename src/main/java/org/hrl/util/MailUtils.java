/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/25
 */

package org.hrl.util;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {

    public static void main(String[] args) {
        MailUtils.sendMail("hello");
    }

    private static String to = "827161957@qq.com";
    private static String from = "heruil@126.com";
    private static String password="heruilong1990";
    static String host = "smtp.126.com";

    static Session session;


    static {
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.user","heruil@126.com");
        properties.setProperty("mail.password", "heruilong1990");

        // 获取默认session对象
        session = Session.getDefaultInstance(properties);

    }


    public static void sendMail(String content) {
        System.out.println("send msg");
        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("This is the Subject Line!");

            // 设置消息体
            message.setText("This is actual message");

            // 发送消息
            SMTPTransport t = (SMTPTransport) session.getTransport();
            t.connect(host, from, password);
            t.sendMessage(message, message.getAllRecipients());

            //Transport.send(message,from,password);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
