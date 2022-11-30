package edu.byu.cs.tweeter.server.util;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Authentication {
    public static boolean isAuthenticated(AuthToken token) {
        DAOFactory daoFactory = new DAOFactory();
        AuthTokenDAO authTokenDAO = (AuthTokenDAO) daoFactory.create("AuthTokenDAO");

        AuthToken dbToken = authTokenDAO.getAuthToken(token.getToken());
        if (dbToken == null) {
            return false;
        }

        return validAuthToken(dbToken);
    }
    public static boolean validAuthToken(AuthToken token) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        long tokenDateTime;
        try {
            tokenDateTime = dateFormat.parse(token.getDatetime()).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (tokenDateTime < new Date().getTime()) {
            DAOFactory daoFactory = new DAOFactory();
            AuthTokenDAO authTokenDAO = (AuthTokenDAO) daoFactory.create("AuthTokenDAO");

            authTokenDAO.updateAuthToken(token.getToken());
            return true;
        }
        return false;
    }
}
