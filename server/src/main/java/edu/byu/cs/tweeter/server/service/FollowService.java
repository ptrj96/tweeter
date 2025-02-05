package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowUserRequest;
import edu.byu.cs.tweeter.model.net.request.FollowCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowListRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.util.Authentication;

import java.util.Random;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowListResponse getFollowees(FollowListRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new FollowListResponse("not authenticated");
        }
        return getFollowingDAO().getFollowees(request.getTargetUser(), request.getLastFolloweeAlias());
    }

    public FollowListResponse getFollowers(FollowListRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new FollowListResponse("not authenticated");
        }
        return getFollowingDAO().getFollowers(request.getTargetUser(), request.getLastFolloweeAlias());
    }

    public SuccessResponse followUser(FollowUserRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new SuccessResponse(false, "not authenticated");
        }
        if (!getFollowingDAO().isFollower(request.getTargetUser(), request.getFollowee())) {
            getUserDAO().addFolloweeCount(request.getTargetUser().getAlias());
            getUserDAO().addFollowerCount(request.getFollowee().getAlias());
            getFollowingDAO().followUser(request.getTargetUser(), request.getFollowee());
        }
        return new SuccessResponse(true);
    }

    public SuccessResponse unfollowUser(FollowUserRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new SuccessResponse(false, "not authenticated");
        }
        if (getFollowingDAO().isFollower(request.getTargetUser(), request.getFollowee())) {
            getUserDAO().subtractFolloweeCount(request.getTargetUser().getAlias());
            getUserDAO().subtractFollowerCount(request.getFollowee().getAlias());
            getFollowingDAO().unfollowUser(request.getTargetUser(), request.getFollowee());
        }
        return new SuccessResponse(true);
    }

    public FollowCountResponse getFollowerCount(FollowCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a targetUser");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new FollowCountResponse("not authenticated");
        }
        int count = getUserDAO().getFollowerCount(request.getTargetUser().getAlias());
        return new FollowCountResponse(count);
    }

    public FollowCountResponse getFolloweeCount(FollowCountRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a targetUser");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new FollowCountResponse("not authenticated");
        }
        int count = getUserDAO().getFolloweeCount(request.getTargetUser().getAlias());
        return new FollowCountResponse(count);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a targetUser");
        }
        else if (request.getUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a User");
        }
        boolean isAuthenticated = Authentication.isAuthenticated(request.getToken());
        if (!isAuthenticated) {
            return new IsFollowerResponse("not authenticated");
        }
        boolean isFollow = getFollowingDAO().isFollower(request.getTargetUser(), request.getUser());
        return new IsFollowerResponse(isFollow);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return (FollowDAO) new DAOFactory().create("FollowDAO");
    }
    UserDAO getUserDAO() {
        return (UserDAO) new DAOFactory().create("UserDAO");
    }
}
