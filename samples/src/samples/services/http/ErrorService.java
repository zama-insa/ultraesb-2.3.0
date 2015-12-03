/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.http;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ErrorService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ErrorService.class);

    private volatile boolean stop = false;
    private ServerSocket serverSocket = null;

    public void stop() {
        if (serverSocket != null) {
            this.stop = true;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        stop = false;
        new Thread(this).start();
    }

    public static void main(String[] args) throws Exception {
        ErrorService es = new ErrorService();
        es.start();
        Thread.sleep(3000);
        es.stop();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(9000);
            while (!stop) {
                Socket socket = serverSocket.accept();
                new Thread(new ErrorService.Worker(socket)).start();
            }
            logger.info("Stopped..");
        } catch (SocketException ignore) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Worker implements Runnable {
        private static final String ECHO_RESPONSE_PREFIX = "Echo response follows - ";
        final Socket socket;

        Worker(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                socket.setTcpNoDelay(false);
                //socket.setPerformancePreferences(10, 100, 10);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            int returnCode = -1, sleepRead = -1, sleepWrite = -1;
            boolean closeReading = false, closeWriting = false;

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String str;
                int contentLength = 0;

                while ((str = br.readLine()) != null) {
                    if (str.trim().length() == 0) {
                        break;
                    } else if (str.indexOf(":") != -1) {
                        String header = str.substring(0, str.indexOf(":"));
                        if ("Content-Length".equalsIgnoreCase(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            contentLength = Integer.parseInt(value.trim());
                        } else if ("return-code".equals(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            returnCode = Integer.parseInt(value.trim());
                        } else if ("sleep-read".equals(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            sleepRead = Integer.parseInt(value.trim());
                        } else if ("sleep-write".equals(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            sleepWrite = Integer.parseInt(value.trim());
                        } else if ("close-read".equals(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            closeReading = Boolean.parseBoolean(value.trim());
                        } else if ("close-write".equals(header)) {
                            String value = str.substring(str.indexOf(":") + 1);
                            closeWriting = Boolean.parseBoolean(value.trim());
                        }
                    }
                }

                //---------- reading request -----------------
                boolean slept = false;

                ByteBuffer body = ByteBuffer.allocate(4096);
                if (contentLength > 0) {
                    for (int i = 0; i < contentLength; i++) {
                        body.put((byte) br.read());

                        // sleep or close after we start reading
                        if (!slept && sleepRead > 0) {
                            logger.info("Sleeping for : " + sleepRead + " while reading request");
                            Thread.sleep(sleepRead);
                            slept = true;
                            logger.info("Woke up...");
                        }
                        if (closeReading) {
                            logger.info("Closing socket while reading");
                            socket.close();
                            return;
                        }
                    }

                } else {
                    while (true) {
                        String s = br.readLine();
                        if (s == null || s.trim().length() == 0) {
                            continue;
                        }
                        int chunkSize = Integer.parseInt(s, 16);
                        if (chunkSize == 0) {
                            break;
                        }
                        for (int i = 0; i < chunkSize; i++) {
                            body.put((byte) br.read());
                        }
                        //break;
                    }
                }

                body.flip();
                byte[] bytes = new byte[body.limit()];
                body.get(bytes, 0, body.limit());

                // ------------ writing response ------------

                if (returnCode > 0) {
                    bw.append("HTTP/1.1 " + returnCode + " Message\r\n");
                } else {
                    bw.append("HTTP/1.1 200 OK\r\n");
                }
                bw.append("Content-Type: text/xml\r\n");
                bw.append("Content-Length: " + (body.limit() + ECHO_RESPONSE_PREFIX.length()) + "\r\n");
                bw.append("Connection: close\r\n\r\n");
                //bw.flush();

                slept = false;
                char[] payload = new String(bytes).toCharArray();

                bw.append(ECHO_RESPONSE_PREFIX);
                bw.flush();

                for (int i=0; i<payload.length; i++) {
                    bw.append(payload[i]);

                    if (!slept && sleepWrite > 0) {
                        bw.flush();
                        logger.info("Sleeping for : " + sleepWrite + " while writing response");
                        Thread.sleep(sleepWrite);
                        logger.info("Woke up...");
                        slept = true;
                    }
                    if (closeWriting) {
                        bw.flush();
                        logger.info("Closing socket while writing");
                        socket.close();
                        return;
                    }
                }

                try {
                    bw.flush();
                } catch (Exception ignore) {}

                try {
                    socket.close();
                } catch (Exception ignore) {}

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}