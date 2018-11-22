package com.Util.IOUtil;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

public class RemoteLogin {
    private static Logger logger = Logger.getLogger(RemoteLogin.class);

    public static Session sshSftp(String ip, String user, String psw, int port) {
        logger.info("通过用户名-密码方式SSH登陆");

        Session session = null;
        JSch jsch = new JSch();
        try {
            if (port <= 0) {
                // 连接服务器，采用默认端口
                session = jsch.getSession(user, ip);
            } else {
                // 采用指定的端口连接服务器
                session = jsch.getSession(user, ip, port);
            }

            // 如果服务器连接不上，则抛出异常
            if (session == null) {
                throw new Exception("session is null");
            }

            // 设置登陆主机的密码
            session.setPassword(psw);// 设置密码
            // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            // 设置登陆超时时间
            session.connect(300000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("登陆成功");
        return session;
    }

    /**
     * 密匙方式登录
     *
     * @param ip
     * @param user
     * @param port
     * @param privateKey
     * @param passphrase
     */
    public static Session sshSftp2(String ip, String user, int port, String privateKey, String passphrase) {
        logger.info("通过密锁方式SSH登陆");
        Session session = null;
        JSch jsch = new JSch();
        try {
            // 设置密钥和密码
            // 支持密钥的方式登陆，只需在jsch.getSession之前设置一下密钥的相关信息就可以了
            if (privateKey != null && !"".equals(privateKey)) {
                if (passphrase != null && "".equals(passphrase)) {
                    // 设置带口令的密钥
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    // 设置不带口令的密钥
                    jsch.addIdentity(privateKey);
                }
            }
            if (port <= 0) {
                // 连接服务器，采用默认端口
                session = jsch.getSession(user, ip);
            } else {
                // 采用指定的端口连接服务器
                session = jsch.getSession(user, ip, port);
            }
            // 如果服务器连接不上，则抛出异常
            if (session == null) {
                throw new Exception("session is null");
            }
            // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            // 设置登陆超时时间
            session.connect(300000);
            logger.info("登陆成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session;
    }

}
