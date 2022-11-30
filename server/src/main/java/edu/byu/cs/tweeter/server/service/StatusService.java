package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.Follower;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowListResponse;
import edu.byu.cs.tweeter.model.net.response.SuccessResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.util.Authentication;

import java.util.ArrayList;
import java.util.List;

public class StatusService {
    public SuccessResponse postStatus(PostStatusRequest request) {
        boolean isAuthenticated = Authentication.isAuthenticated(request.getAuthToken());
        if (!isAuthenticated) {
            return new SuccessResponse(false, "not authenticated");
        }

        DAOFactory daoFactory = new DAOFactory();
        StoryDAO storyDAO = (StoryDAO) daoFactory.create("StoryDAO");
        FeedDAO feedDAO = (FeedDAO) daoFactory.create("FeedDAO");
        FollowDAO followDAO = (FollowDAO) daoFactory.create("FollowDAO");

        storyDAO.addStory(request.getStatus());
        List<Follower> followerList = new ArrayList<>();
        FollowListResponse response = followDAO.getFollowers(request.getCurUser().getAlias(), null);
        followerList.addAll(response.getFollowList());

        while (response.getHasMorePages()) {
            String lastItem = response.getFollowList().get(response.getFollowList().size() - 1).getAlias();
            response = followDAO.getFollowers(request.getCurUser().getAlias(), lastItem);
            followerList.addAll(response.getFollowList());
        }

        for (Follower follower : followerList) {
            feedDAO.addFeed(request.getStatus(), follower);
        }

        return new SuccessResponse(true);
    }
}
