package com.Util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import static com.Util.IOUtil.RemoteLogin.sshSftp;

/**
 * https://blog.csdn.net/hezhihuahzh/article/details/79056544
 * Java利用ssh协议实现本地文件到远程Linux服务器的上传
 */
public class RemoteFileHelper {
    private static Logger logger = Logger.getLogger(RemoteFileHelper.class);

    public static void upLoadFile(String ip, String user, String psw, int port, String sPath, String dPath) {
        Session session = sshSftp(ip, user, psw, port);

        Channel channel = null;
        try {
            channel = (Channel) session.openChannel("sftp");
            channel.connect(10000000);
            ChannelSftp sftp = (ChannelSftp) channel;
            try {
                //上传
                sftp.cd(dPath);
                //return;
                /*
                Scanner scanner = new Scanner(System.in);
                System.out.println(dPath + ":此目录已存在,文件可能会被覆盖!是否继续y/n?");
                String next = scanner.next();
                if (!next.toLowerCase().equals("y")) {
                    return;
                }
                */
            } catch (SftpException e) {
                sftp.mkdir(dPath);
                sftp.cd(dPath);
            }
            File file = new File(sPath);
            copyFile(sftp, file, sftp.pwd());
            logger.info("上传文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            channel.disconnect();
        }
    }

    public static void copyFile(ChannelSftp sftp, File file, String pwd) {

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            try {
                try {
                    String fileName = file.getName();
                    sftp.cd(pwd);
                    logger.info("正在创建目录:" + sftp.pwd() + "/" + fileName);
                    sftp.mkdir(fileName);
                    logger.info("目录创建成功:" + sftp.pwd() + "/" + fileName);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                pwd = pwd + "/" + file.getName();
                try {

                    sftp.cd(file.getName());
                } catch (SftpException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (int i = 0; i < list.length; i++) {
                copyFile(sftp, list[i], pwd);
            }
        } else {

            try {
                sftp.cd(pwd);

            } catch (SftpException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            logger.info("正在复制文件:" + file.getAbsolutePath());
            InputStream instream = null;
            OutputStream outstream = null;
            try {
                outstream = sftp.put(file.getName());
                instream = new FileInputStream(file);

                byte b[] = new byte[1024];
                int n;
                try {
                    while ((n = instream.read(b)) != -1) {
                        outstream.write(b, 0, n);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (SftpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    outstream.flush();
                    outstream.close();
                    instream.close();

                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:\\DTAinHadoop\\src\\log4j.properties");
        upLoadFile("192.168.0.2", "cloud", "1234", 22, "D:\\test100_100.txt", "/home/cloud/cloud/haninput");
    }
}