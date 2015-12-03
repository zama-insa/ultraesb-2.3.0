/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.json;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.api.*;
import org.adroitlogic.ultraesb.api.transport.http.HttpConstants;
import org.adroitlogic.ultraesb.core.helper.JSONUtils;
import org.adroitlogic.ultraesb.json.Employee;
import org.apache.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * A sample sequence supporting CRUD operations
 * 
 * @author asankha
 */
public class JsonCRUDSequence implements JavaClassSequence {

    private static final Logger logger = LoggerFactory.getLogger("sequence");

    public void execute(Message msg, Mediation mediation) throws Exception {

        final DataSource dataSource = mediation.getDataSource("dataSource");

        String method = msg.getStringMessageProperty(HttpConstants.MessageProperties.METHOD);
        String idStr = msg.getDestinationURL().substring("/employees".length());
        if (idStr.startsWith("/")) {
            idStr = idStr.substring(1);
        }
        int qPos = idStr.indexOf('?');
        if (qPos != -1) {
            idStr = idStr.substring(0, qPos);
        }
        final int id = (idStr.length() > 0) ? Integer.parseInt(idStr) : -1;

        if ("GET".equals(method)) {
            if (id > 0) {
                // return requested entry
                JSONUtils.getRowAsJson(
                    "select * from EMPLOYEE where employeeId = ?", dataSource, null, new Object[]{id}, msg);
            } else {
                Map<String, String> params = (Map<String, String>) msg.getMessageProperty(HttpConstants.MessageProperties.QUERY_PARAM_MAP);
                if (params.isEmpty()) {
                    // return all
                    JSONUtils.getRowsAsJson(
                        "select * from EMPLOYEE", dataSource, null, null, msg);
                } else if (params.containsKey("salary")) {
                    JSONUtils.getRowsAsJson(
                        "select * from EMPLOYEE where salary > ?", dataSource, null,
                        new Object[] { Double.parseDouble(params.get("salary"))}, msg);
                }
            }
            msg.getTransportHeaders().clear();
            mediation.sendResponse(msg, msg.getCurrentPayload() == null ? HttpStatus.SC_NOT_FOUND : HttpStatus.SC_OK);

        } else if ("POST".equals(method) || "PUT".equals(method)) {

            final Employee emp = mediation.getJSONSupport().convertToTypedJSON(msg, Employee.class);
            JdbcTemplate t = new JdbcTemplate(dataSource);

            if (id > 0) {
                // update
                int i = t.update("UPDATE EMPLOYEE set " +
                        "employeeName = ?, dateOfBirth = ?, salary = ?, address = ?, permanent = ? WHERE employeeId = ?",
                        new PreparedStatementSetter() {
                            public void setValues(PreparedStatement ps) throws SQLException {
                                ps.setString(1, emp.getEmployeeName());
                                ps.setDate(2, new java.sql.Date(emp.getDateOfBirth().getTime()));
                                ps.setDouble(3, emp.getSalary());
                                ps.setString(4, emp.getAddress());
                                ps.setBoolean(5, emp.isPermanent());
                                ps.setInt(6, id);
                            }
                        });
                msg.getTransportHeaders().clear();
                msg.setCurrentPayload(null);
                mediation.sendResponse(msg, HttpStatus.SC_NO_CONTENT);

            } else {
                // add an Employee
                KeyHolder keyHolder = new GeneratedKeyHolder();
                t.update(new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(
                                "INSERT INTO EMPLOYEE(employeeName, dateOfBirth, salary, address, permanent) VALUES (?,?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, emp.getEmployeeName());
                        ps.setDate(2, new java.sql.Date(emp.getDateOfBirth().getTime()));
                        ps.setDouble(3, emp.getSalary());
                        ps.setString(4, emp.getAddress());
                        ps.setBoolean(5, emp.isPermanent());
                        return ps;
                    }
                }, keyHolder);

                msg.getTransportHeaders().clear();
                msg.addTransportHeader("Location", "http://localhost:8280/employees/" + keyHolder.getKey().intValue());
                msg.setCurrentPayload(null);
                mediation.sendResponse(msg, HttpStatus.SC_CREATED);
            }

        } else if ("DELETE".equals(method)) {

            JdbcTemplate t = new JdbcTemplate(dataSource);
            t.update("DELETE FROM EMPLOYEE WHERE employeeId = ?", new Object[]{id});
            msg.getTransportHeaders().clear();
            msg.setCurrentPayload(null);
            mediation.sendResponse(msg, HttpStatus.SC_NO_CONTENT);
        }
    }

    public void init(Configuration config) {
        logger.info("Started the JSON Employee management sequence");   // not used
    }

    public void destroy() {
        logger.info("Stopped the JSON Employee management sequence");   // not used
    }
}
