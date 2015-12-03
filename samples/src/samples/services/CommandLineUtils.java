/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services;

import org.adroitlogic.ultraesb.api.ConfigurationConstants;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandLineUtils {

    static Options getOptions() {

        Option copt = new Option("c", true, "Concurrency while performing the requests");
        copt.setRequired(false);
        copt.setArgName("concurrency");

        Option iopt = new Option("i", true, "Number of iterations need to be performed");
        iopt.setRequired(false);
        iopt.setArgName("iterations");

        Option ropt = new Option("r", true, "Random repeating delay (in milliseconds)");
        ropt.setRequired(false);
        ropt.setArgName("repeat-interval");

        Option surlopt = new Option("surl", true, "SOAP URL");
        surlopt.setRequired(false);
        surlopt.setArgName("soap-url");

        Option hurlopt = new Option("hurl", true, "Hessian URL");
        hurlopt.setRequired(false);
        hurlopt.setArgName("hessian-url");

        Option hopt = new Option("h", false, "Display information");
        hopt.setRequired(false);
        hopt.setArgName("help");

        Option nopt = new Option("n", true, "Number of elements in multiplication array");
        nopt.setRequired(false);
        nopt.setArgName("array-elements");

        Option headeropt = new Option("header", true, "Header name");
        headeropt.setRequired(false);
        headeropt.setArgName("header-name");

        Option vopt = new Option("v", true, "Header value");
        vopt.setRequired(false);
        vopt.setArgName("header-value");

        Options options = new Options();

        options.addOption(copt);
        options.addOption(iopt);
        options.addOption(ropt);
        options.addOption(surlopt);
        options.addOption(hurlopt);
        options.addOption(hopt);
        options.addOption(nopt);
        options.addOption(headeropt);
        options.addOption(vopt);

        return options;
    }

    static void parseCommandLine(CommandLine cmd, StressTest st, StressTestHessian sth) {

        if (cmd.hasOption('c')) {
            String s = cmd.getOptionValue('c');
            try {
                if (sth == null) {
                    st.setThreads(Integer.parseInt(s));
                } else {
                    sth.setThreads(Integer.parseInt(s));
                }
            } catch (NumberFormatException ex) {
                printError("Invalid number for concurrency: " + s);
            }
        }

        if (cmd.hasOption('i')) {
            String s = cmd.getOptionValue('i');
            try {
                if (sth == null) {
                    st.setIterations(Integer.parseInt(s));
                } else {
                    sth.setIterations(Integer.parseInt(s));
                }
            } catch (NumberFormatException ex) {
                printError("Invalid number of iterations: " + s);
            }
        }

        if (cmd.hasOption('r')) {
            String s = cmd.getOptionValue('r');
            try {
                if (sth == null) {
                    st.setDelay(Integer.parseInt(s));
                } else {
                    sth.setDelay(Integer.parseInt(s));
                }
            } catch (NumberFormatException ex) {
                printError("Invalid number for repeating delay: " + s);
            }
        }

        if (cmd.hasOption("surl")) {
            String s = cmd.getOptionValue("surl");
            try {
                st.setSurl(s);
            } catch (NumberFormatException ex) {
                printError("Invalid SOAP URL: " + s);
            }
        }

        if (cmd.hasOption("hurl")) {
            String s = cmd.getOptionValue("hurl");
            try {
                sth.setHurl(s);
            } catch (Exception ex) {
                printError("Invalid Hessian URL: " + s);
            }
        }

        if (cmd.hasOption('n')) {
            String s = cmd.getOptionValue('n');
            try {
                if (sth == null) {
                    st.setArraySize(Integer.parseInt(s));
                } else {
                    sth.setArraySize(Integer.parseInt(s));
                }
            } catch (NumberFormatException ex) {
                printError("Invalid number for array size: " + s);
            }
        }

        if (cmd.hasOption("header")) {
            String s = cmd.getOptionValue("header");
            try {
                if (sth == null) {
                    st.setHeaderName(s);
                } else {
                    sth.setHeaderName(s);
                }
            } catch (Exception ex) {
                printError("Invalid header: " + s);
            }
        }

        if (cmd.hasOption("v")) {
            String s = cmd.getOptionValue("v");
            try {
                if (sth == null) {
                    st.setHeaderValue(s);
                } else {
                    sth.setHeaderValue(s);
                }
            } catch (Exception ex) {
                printError("Invalid header value: " + s);
            }
        }
    }

    static void showUsage(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(ConfigurationConstants.Product.COPYRIGHT
                + "\n ArrayMultiplication [options] http[s]://hostname[:port]/path", options);
    }

    static void printError(String msg) {
        System.err.println(msg);
        showUsage(getOptions());
        System.exit(-1);
    }

}
