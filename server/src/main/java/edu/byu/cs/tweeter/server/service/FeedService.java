package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StatusListRequest;
import edu.byu.cs.tweeter.model.net.response.StatusListResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.util.Authentication;

public class FeedService {
    public StatusListResponse getFeed(StatusListRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new StatusListResponse("not authenticated");
        }
        return getFeedDAO().getFeed(request);
    }

    FeedDAO getFeedDAO() {
        return (FeedDAO) new DAOFactory().create("FeedDAO");
    }
}
