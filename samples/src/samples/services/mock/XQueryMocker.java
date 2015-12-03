/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.mock;

import org.adroitlogic.ultraesb.api.*;
import org.adroitlogic.ultraesb.api.helper.XQuerySupport;
import org.adroitlogic.ultraesb.api.transport.http.HttpConstants;
import org.adroitlogic.ultraesb.core.ConfigurationImpl;
import org.adroitlogic.ultraesb.core.format.DOMMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author asankha
 */
public class XQueryMocker implements JavaClassSequence {

    private FileCache fileCache;

    private final ParameterizedRowMapper<Subscription> mapper = new ParameterizedRowMapper<Subscription>() {
        public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            Subscription sub = new Subscription();
            sub.id = rs.getString("id");
            sub.startdate = rs.getTimestamp("startdate");
            sub.enddate = rs.getTimestamp("enddate");
            sub.category = rs.getInt("category");
            sub.username = rs.getString("username");
            sub.etag = rs.getString("etag");
            return sub;
        }
    };

    public void init(Configuration config) {
        fileCache = ((ConfigurationImpl) config).getRootDeploymentUnit().getDefaultFileCache();
    }

    public void destroy() {
        // any completion stuff
    }

    public void execute(Message msg, Mediation mediation) throws Exception {

        Message res = msg.createDefaultResponseMessage();
        String method = msg.getStringMessageProperty(HttpConstants.MessageProperties.METHOD);

        if ("GET".equals(method)) {

            // get etag from request, and query parameters
            String clientETag = msg.getFirstTransportHeader("If-None-Match");
            @SuppressWarnings("unchecked")
            String id = mediation.getHTTPSupport().getFirstRequestParameter(msg, "id");

            if (clientETag != null) {
                JdbcTemplate t = new JdbcTemplate(mediation.getDataSource("dataSource"));
                String serverETag = t.queryForObject("SELECT ETAG FROM SUBSCRIPTION WHERE ID = ?", String.class, id);

                if (clientETag.equals(serverETag)) {
                    mediation.sendResponse(msg, 304);
                    return;
                }
            }

            // if client did not send a if-none-match header, or if the etag sent did not match the latest on DB
            JdbcTemplate t = new JdbcTemplate(mediation.getDataSource("dataSource"));
            Subscription sub = t.queryForObject("SELECT * FROM SUBSCRIPTION WHERE ID = ?", mapper, id);
            Map<String, Object> vars = new HashMap<String, Object>();
            if (sub != null) {
                vars.put("id", sub.id);
                vars.put("startdate", sub.startdate.toString());
                vars.put("enddate", sub.enddate.toString());
                vars.put("category", sub.category);
                vars.put("username", sub.username);
            }

            XQuerySupport xq = mediation.getSpringBean("xq", XQuerySupport.class);
            xq.transformMessage(res, "samples/resources/sample1.xq", vars);
            if (sub != null) {
                res.addTransportHeader("ETag", sub.etag);
            }
            mediation.sendResponse(res, 200);

        } else if ("POST".equals(method)) {

            XQuerySupport xq = mediation.getSpringBean("xq", XQuerySupport.class);

            Map<String, Object> vars = new HashMap<String, Object>();

            if (msg.getCurrentPayload() instanceof DOMMessage) {
                vars.put("payload", (((DOMMessage) msg.getCurrentPayload()).getDocument()));
            } else {
                DOMMessage domMessage = new DOMMessage(msg.getCurrentPayload(), fileCache);
                vars.put("payload", domMessage.getDocument());
            }

            String paid = mediation.getXMLSupport().extractAsStringUsingXPath(msg,
                    "//m0:updateSubscription/m0:amountPaid", new String[] {"m0", "http://mock.samples/"});
            vars.put("balance", Double.valueOf(paid) + 1550.33);

            xq.transformMessage(res, "samples/resources/sample2.xq", vars);
            mediation.sendResponse(res, 200);
        }
    }

    static class Subscription {
        String id;
        Date startdate;
        Date enddate;
        int category;
        String username;
        String etag;
    }
}
