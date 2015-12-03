/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services;

import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Utility class that provides some commonly used jetty specific preparations to start a sample server using embedded
 * jetty
 *
 * @author Ruwan
 * @since 2.1.0
 */
public final class JettyUtils {

    /**
     * This class is is a utility class which is not supposed to be extended nor instantiated
     */
    private JettyUtils() {}

    /**
     * Creates a sample web application context for jetty from the samples/webapp path relative to the UltraESB home
     * directory
     *
     * @return the sample web application context
     */
    public static WebAppContext sampleWebAppContext() {
        WebAppContext webAppContext = new WebAppContext("samples/webapp", "/");
        webAppContext.setConfigurationClasses(new String[]{
                "org.eclipse.jetty.webapp.WebInfConfiguration",
                "org.eclipse.jetty.webapp.WebXmlConfiguration"});
        return webAppContext;
    }

    /**
     * Creates a SSL connector for a jetty server over the default 9002 port taking the following SSL context parameters
     * <ul>
     *     <li>key store - conf/keys/identity.jks</li>
     *     <li>key store password - password</li>
     *     <li>key password - password</li>
     *     <li>trust store - samples/conf/keys/trust.jks</li>
     *     <li>trust store password - password</li>
     * </ul>
     * All paths above are relative to the UltraESB installation home directory
     *
     * @return a SSL connector for a jetty server listening over the port 9002
     */
    public static SslConnector sampleSslConnector() {
        return sampleSslConnector(9002);
    }

    /**
     * Creates a SSL connector for a jetty server over the specified port taking the following SSL context parameters
     * <ul>
     *     <li>key store - conf/keys/identity.jks</li>
     *     <li>key store password - password</li>
     *     <li>key password - password</li>
     *     <li>trust store - samples/conf/keys/trust.jks</li>
     *     <li>trust store password - password</li>
     * </ul>
     * All paths above are relative to the UltraESB installation home directory
     *
     * @param port the port on which the SSL connector will be listening for messages
     * @return a SSL connector for a jetty server listening over the specified port
     */
    public static SslConnector sampleSslConnector(int port) {
        final SslContextFactory sslContextFactory = new SslContextFactory("conf/keys/identity.jks");
        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");
        sslContextFactory.setTrustStore("samples/conf/keys/trust.jks");
        sslContextFactory.setTrustStorePassword("password");
        final SslSocketConnector sslConnector = new SslSocketConnector(sslContextFactory);
        sslConnector.setPort(port);
        sslConnector.setMaxIdleTime(30000);
        return sslConnector;
    }
}
