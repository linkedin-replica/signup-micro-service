package com.linkedin.replica.signing.database.handlers.impl;

import com.linkedin.replica.signing.database.DatabaseConnection;
import com.linkedin.replica.signing.database.handlers.SigningHandler;
import com.linkedin.replica.signing.models.User;

import java.sql.*;

public class MysqlSigningHandler implements SigningHandler {

//    private static final Logger LOGGER = LogManager.getLogger(MysqlSigningHandler.class.getName());
    private Connection dbInstance;

    public MysqlSigningHandler() {
        dbInstance = DatabaseConnection.getInstance().getMysqlDriver();
    }

    public User getUser(String email) throws SQLException {
        User user = null;
        CallableStatement statement = dbInstance.prepareCall("{CALL search_for_user(?)}");
        statement.setString(1, email);
        statement.executeQuery();
        ResultSet results = statement.getResultSet();
        if (results.next()) {
            user = new User();
            user.setId("" + results.getInt("id"));
            user.setEmail(results.getString("email"));
            user.setPassword(results.getString("password"));
        }
        return user;
    }

    public String createUser(User user) throws SQLException {
        CallableStatement statement = dbInstance.prepareCall("{CALL Insert_User(?, ?)}");
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.executeQuery();
        return getUser(user.getEmail()).getId();
    }

}
