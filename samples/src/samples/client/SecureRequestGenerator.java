/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.client;

import org.adroitlogic.soapbox.CryptoSupport;
import org.adroitlogic.soapbox.api.WSSecurityManager;
import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.format.MessageFormat;
import org.adroitlogic.ultraesb.core.ConfigurationImpl;
import org.adroitlogic.ultraesb.core.MediationImpl;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.adroitlogic.ultraesb.core.PooledMessageFileCache;
import org.adroitlogic.ultraesb.core.format.DOMMessage;
import org.adroitlogic.ultraesb.core.format.RawFileMessage;
import org.adroitlogic.ultraesb.core.helper.XMLFeatures;

import java.io.*;
import java.util.*;

/**
 * @author sampath
 * @since 1.5.0
 */
public class SecureRequestGenerator {

    private static Properties properties = new Properties();
    private static final String KEYSTORE_USER__PROP_NAME_PREFIX = "keystore.user.";

    public static void main(String[] args) {

        if (args.length > 0) {
            try {
                properties.load(new FileReader(args[0]));
            } catch (IOException e) {
                System.out.println("The specified recreation properties file (" + args[0] + ") is invalid");
                System.exit(1);
            }
        }

        String requestDir = getProperty("req.dir", "samples/resources/requests/");
        if (!requestDir.endsWith("/")) {
            requestDir += "/";
        }

        MediationImpl.initialize(new ConfigurationImpl());
        XMLFeatures.initializeInstance(1, 1, 1, 1, false, false);
        String user1 = getProperty(KEYSTORE_USER__PROP_NAME_PREFIX + "1", "bob");
        String user2 = getProperty(KEYSTORE_USER__PROP_NAME_PREFIX + "2", "alice");
        Map<String, String> passwdMap = new HashMap<String, String>();
        passwdMap.put(user1, getProperty(KEYSTORE_USER__PROP_NAME_PREFIX + "1.password", "password"));
        passwdMap.put(user2, getProperty(KEYSTORE_USER__PROP_NAME_PREFIX + "2.password", "password"));
        WSSecurityManager wssecMgr = new org.adroitlogic.soapbox.WSSecurityManager(
            getProperty("keystore.path", "samples/resources/perf/store.jks"),
            getProperty("keystore.password", "password"), passwdMap);
        CryptoSupport.initializeInstance(1,1,1);

        Collection<String> requestSizes = Arrays.asList(getProperty("request.sizes", "500B,1K,5K,10K,100K").split(","));
        for (String requestSize : requestSizes) {
            System.out.println("Recreating the secure request " + requestSize
                + "_buyStocks_secure.xml from the plain request " + requestSize + "_buyStocks.xml");
            Message msg = new MessageImpl(true, null, "http");
            File requestFile = new File(requestDir + requestSize + "_buyStocks.xml");
            MessageFormat format = new RawFileMessage(requestFile, requestFile.length());
            msg.setCurrentPayload(new DOMMessage(format, new PooledMessageFileCache(10)));
            wssecMgr.timestampSignAndEncryptMessage(msg, user1, user2, 30*24*60*60*1000L, null);
            try {
                File output = new File(requestDir + requestSize + "_buyStocks_secure.xml");
                FileOutputStream resultStream = new FileOutputStream(output);
                msg.getCurrentPayload().writeTo(resultStream);
                System.out.println("Recreated secure request written to : " + output.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getProperty(String key, String def) {
        return properties.containsKey(key) ? properties.get(key).toString() : def;
    }
}
