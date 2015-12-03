/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services;

import org.apache.commons.cli.*;
import samples.services.multiplicationstresstest.ArrayMultiplicationTest;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StressTest {

    private int threads = 10;
    private int iterations = 1000;
    private int delay = 5000;
    private String surl = null;
    private int arraySize = 1000;
    private String header = null;
    private String headerValue = null;

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public void setHeaderName(String header) {
        this.header = header;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public static void main(String args[]) throws InterruptedException, ParseException {

        Random random = new Random(System.currentTimeMillis());

        Options options = CommandLineUtils.getOptions();
        CommandLineParser parser = new PosixParser();

        CommandLine cmd = parser.parse(options, args);

        if (args.length == 0 || cmd.hasOption('h')) {
            CommandLineUtils.showUsage(options);
            System.exit(1);
        }
        StressTest st = new StressTest();
        CommandLineUtils.parseCommandLine(cmd, st, null);

        StressTestStats stats = new StressTestStats();

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                st.threads, st.threads, 5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "TestPool");
                    }
                });

        threadPool.prestartAllCoreThreads();

        ArrayMultiplicationTest[] st1 = new ArrayMultiplicationTest[st.threads];

        for (int i = 0; i < st.threads; i++) {
            st1[i] = new ArrayMultiplicationTest(stats, st.surl, st.arraySize, st.header, st.headerValue);
            for (int j = 0; j < st.iterations; j++) {
                threadPool.execute(st1[i]);
                Thread.sleep(random.nextInt(st.delay));
            }
        }
        threadPool.shutdown();
    }
}