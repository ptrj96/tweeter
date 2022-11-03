package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowUserRequest {
    private AuthToken authToken;
    private User followee;

    public FollowUserRequest(AuthToken authToken, User followee) {
        this.authToken = authToken;
        this.followee = followee;
    }

    public FollowUserRequest() {}

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
}
