package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

public interface IUserDAO extends IDAO {
    void createUser(RegisterRequest request);
    User getUser(String alias);
    User login(String alias, String password);
    void addFollowerCount(String alias);
    void subtractFollowerCount(String alias);
    void addFolloweeCount(String alias);
    void subtractFolloweeCount(String alias);
    int getFollowerCount(String alias);
    int getFolloweeCount(String alias);
}
