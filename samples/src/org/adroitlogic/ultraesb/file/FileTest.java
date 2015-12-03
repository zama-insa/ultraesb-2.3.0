/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.file;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.eclipse.jetty.server.Server;
import samples.services.JettyUtils;

import java.io.*;

/**
 * @author asankha
 */
public class FileTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(FileTest.class);

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    private static final String TMP_FILE_INCOMING = TMP_DIR + "file" + File.separator + "incoming";
    private static final String TMP_FILE_ERROR = TMP_DIR + "file" + File.separator + "error";
    private static final String TMP_FILE_DONE = TMP_DIR + "file" + File.separator + "done";
    private static final String TMP_FILE_SENT = TMP_DIR + "file" + File.separator + "sent";

    private static final String FTP_FILE_INCOMING = TMP_DIR + "ftp" + File.separator + "incoming";
    private static final String FTP_FILE_ERROR = TMP_DIR + "ftp" + File.separator + "error";
    private static final String FTP_FILE_DONE = TMP_DIR + "ftp" + File.separator + "done";

    private static Server server = null;
    private static int port = 9000;
    private static FTPClient ftp = null;
    private static String ftpUsername = "asankha";
    private static String ftpPassword = "xxxxxx";

    public static Test suite() {
        return new TestSetup(new TestSuite(FileTest.class)) {

            protected void setUp() throws Exception {
                if ("\\".equals(File.separator)) {
                    logger.info("File transport should not be used under Windows");
                    return;
                }

                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-401.xml"});

                createCleanDirectory(TMP_FILE_INCOMING);
                createCleanDirectory(TMP_FILE_ERROR);
                createCleanDirectory(TMP_FILE_DONE);
                createCleanDirectory(TMP_FILE_SENT);

                createCleanDirectory(FTP_FILE_INCOMING);
                createCleanDirectory(FTP_FILE_ERROR);
                createCleanDirectory(FTP_FILE_DONE);

                server = new Server(port);
                server.setHandler(JettyUtils.sampleWebAppContext());
                server.start();

                // test stopping and starting a file proxy
                while (!ServerManager.getInstance().isStarted()) {
                    logger.debug("Waiting till the ESB starts..");
                    Thread.sleep(100);
                }
                ServerManager.getInstance().getConfig().getRootDeploymentUnit().getProxyService("file-proxy1").stop();
                logger.debug("Stopped file-proxy1");

                while(ServerManager.getInstance().getConfig().getRootDeploymentUnit().getProxyService("file-proxy1").isStarted()) {
                    logger.debug("Waiting till the file-proxy1 stops..");
                    Thread.sleep(100);
                }
                ServerManager.getInstance().getConfig().getRootDeploymentUnit().getProxyService("file-proxy1").start();
                logger.debug("Started file-proxy1");
            }

            protected void tearDown() throws Exception {
                if ("\\".equals(File.separator)) {
                    logger.info("File transport should not be used under Windows");
                    return;
                }
                ServerManager.getInstance().shutdown(3000);
                if (server != null) {
                    server.stop();
                }
            }
        };
    }

    /*public void testLocalFileList() throws Exception {
        createFile(TMP_FILE_INCOMING + File.separator + "file1.txt", "file1");
        createFile(TMP_FILE_INCOMING + File.separator + "file2.txt", "file2");
        createFile(TMP_FILE_INCOMING + File.separator + "error.txt", "error");
        createFile(TMP_FILE_INCOMING + File.separator + "file3.txt", "file3");
        createFile(TMP_FILE_INCOMING + File.separator + "file4.txt", "file4");

        waitForCompletion(TMP_FILE_DONE, "file1.txt", "file1", 5);
        waitForCompletion(TMP_FILE_DONE, "file2.txt", "file2", 5);
        waitForCompletion(TMP_FILE_DONE, "file3.txt", "file3", 5);
        waitForCompletion(TMP_FILE_ERROR, "error.txt", "error", 5);
        waitForCompletion(TMP_FILE_DONE, "file4.txt", "file4", 5);

        createFile(TMP_FILE_INCOMING + File.separator + "file5.txt", "file5");
        createFile(TMP_FILE_INCOMING + File.separator + "file6.txt", "file6");
        createFile(TMP_FILE_INCOMING + File.separator + "error.txt", "error");

        waitForCompletion(TMP_FILE_DONE, "file5.txt", "file5", 5);
        waitForCompletion(TMP_FILE_DONE, "file6.txt", "file6", 5);
        waitForCompletion(TMP_FILE_ERROR, "error.txt", "error", 5);
    }

    public void testLocalFileSingle() throws Exception {
        createFile(TMP_FILE_INCOMING + File.separator + "special.file", "special");
        waitForCompletion(TMP_FILE_DONE, "special.file", "special", 5);

        createFile(TMP_FILE_INCOMING + File.separator + "special.file", "error");
        waitForCompletion(TMP_FILE_ERROR, "special.file", "error", 5);

        createFile(TMP_FILE_INCOMING + File.separator + "special.file", "special");
        waitForCompletion(TMP_FILE_DONE, "special.file", "special", 5);

        createFile(TMP_FILE_INCOMING + File.separator + "special.file", "error");
        waitForCompletion(TMP_FILE_ERROR, "special.file", "error", 5);
    }

    public void testLocalFileWaitForResponse() throws Exception {
        createFile(TMP_FILE_INCOMING + File.separator + "response.test", "response");
        waitForCompletion(TMP_FILE_DONE, "response.test", "response", 5);

        createFile(TMP_FILE_INCOMING + File.separator + "response.test", "error");
        waitForCompletion(TMP_FILE_ERROR, "response.test", "error", 5);
    }*/

    public void testLocalFileSending() throws Exception {
        if ("\\".equals(File.separator)) {
            logger.info("File transport should not be used under Windows");
            return;
        }
        createFile(TMP_FILE_INCOMING + File.separator + "send.msg", "send");
        waitForCompletion(TMP_FILE_SENT, "send.msg", "send", 10);
    }


    /*public void testFTPSFileList() throws Exception {

        new File(FTP_FILE_INCOMING).mkdirs();
        connectToFTPS();

        uploadFile(FTP_FILE_INCOMING + File.separator + "file1.txt", "file1");
        uploadFile(FTP_FILE_INCOMING + File.separator + "file2.txt", "file2");
        uploadFile(FTP_FILE_INCOMING + File.separator + "error.txt", "error");
        uploadFile(FTP_FILE_INCOMING + File.separator + "file3.txt", "file3");
        uploadFile(FTP_FILE_INCOMING + File.separator + "file4.txt", "file4");

        waitForCompletion(FTP_FILE_DONE, "file1.txt", "file1", 5);
        waitForCompletion(FTP_FILE_DONE, "file2.txt", "file2", 5);
        waitForCompletion(FTP_FILE_DONE, "file3.txt", "file3", 5);
        waitForCompletion(FTP_FILE_ERROR, "error.txt", "error", 5);
        waitForCompletion(FTP_FILE_DONE, "file4.txt", "file4", 5);

        uploadFile(FTP_FILE_INCOMING + File.separator + "file5.txt", "file5");
        uploadFile(FTP_FILE_INCOMING + File.separator + "file6.txt", "file6");
        uploadFile(FTP_FILE_INCOMING + File.separator + "error.txt", "error");

        waitForCompletion(FTP_FILE_DONE, "file5.txt", "file5", 5);
        waitForCompletion(FTP_FILE_DONE, "file6.txt", "file6", 5);
        waitForCompletion(FTP_FILE_ERROR, "error.txt", "error", 5);

        disconnectFromFTP();
    }*/

    private void waitForCompletion(String dir, String name, String content, int seconds) throws Exception {
        boolean processed = false;
        for (int i = 0; i < seconds; i++) {
            processed = doesFileExistWithContent(dir, name, content);
            if (processed) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertTrue(processed);
    }

    private static boolean doesFileExistWithContent(String dir, String name, String content) throws Exception {
        for (String file : new File(dir).list()) {
            if (file.endsWith(name)) {
                return content.equals(readFileAsString(dir + File.separator + file));
            }
        }
        return false;
    }

    private static String readFileAsString(String path) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str;
        StringBuilder sb = new StringBuilder(20);
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return sb.toString();
    }

    private static void createCleanDirectory(String path) {
        File f = new File(path);
        if (f.exists()) {
            File[] children = f.listFiles();
            for (File c : children) {
                c.delete();
            }
        } else {
            f.mkdirs();
        }
    }

    private static void createFile(String path, String content) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        out.write(content);
        out.close();
    }

    private static void connectToFTP() throws Exception {

        ftp = new FTPClient();
        //ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect("localhost");

        // After connection attempt, you should check the reply code to verify
        // success.
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            fail("FTP server refused connection for : localhost");
        }

        if (!ftp.login(ftpUsername, ftpPassword)) {
            ftp.logout();
            fail("Login failed for FTP server");
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    private static void connectToFTPS() throws Exception {

        ftp = new FTPSClient("SSL");
        //ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect("localhost");
        ((FTPSClient)ftp).execPROT("P"); // encrypt data channel

        // After connection attempt, you should check the reply code to verify
        // success.
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            fail("FTP server refused connection for : localhost");
        }

        if (!ftp.login(ftpUsername, ftpPassword)) {
            ftp.logout();
            fail("Login failed for FTP server");
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    private static void uploadFile(String path, String content) throws Exception {
        ftp.storeFile(path, new StringBufferInputStream(content));
    }

    private static void disconnectFromFTP() throws Exception {
        ftp.disconnect();
    }
}
