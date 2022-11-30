package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.SuccessResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.util.Authentication;
import edu.byu.cs.tweeter.util.FakeData;

import java.util.UUID;

public class UserService {
    DAOFactory daoFactory = new DAOFactory();

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        UserDAO userDAO = (UserDAO) daoFactory.create("UserDAO");
        User user = userDAO.login(request.getUsername(), request.getPassword());
        if (user == null) {
            return new LoginResponse("failed to login");
        }
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
        AuthTokenDAO authTokenDAO = (AuthTokenDAO) daoFactory.create("AuthTokenDAO");
        authTokenDAO.createAuthToken(authToken);
        return new LoginResponse(user, authToken);
    }

    public LoginResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        }
        else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }
        else if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        }
        else if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        }

        UserDAO userDAO = (UserDAO) daoFactory.create("UserDAO");
        userDAO.createUser(request);
        User user = userDAO.getUser(request.getUsername());
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
        AuthTokenDAO authTokenDAO = (AuthTokenDAO) daoFactory.create("AuthTokenDAO");
        authTokenDAO.createAuthToken(authToken);
        return new LoginResponse(user, authToken);
    }

    public SuccessResponse logout(LogoutRequest request) {
        AuthTokenDAO authTokenDAO = (AuthTokenDAO) daoFactory.create("AuthTokenDAO");
        authTokenDAO.deleteAuthToken(request.getAuthToken().getToken());
        return new SuccessResponse(true);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */

    public UserResponse getUser(UserRequest request) {
        boolean isAuthenticated = Authentication.isAuthenticated(request.getToken());
        if (isAuthenticated) {
            UserDAO userDAO = (UserDAO) daoFactory.create("UserDAO");
            User user = userDAO.getUser(request.getUser());
            return new UserResponse(user);
        }
        return new UserResponse("not authenticated");
    }
}
