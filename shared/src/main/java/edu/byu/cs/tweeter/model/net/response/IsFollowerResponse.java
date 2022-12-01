package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends  Response {
    private boolean follower;

    public boolean isFollower() {
        return follower;
    }

    public boolean follower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }

    public IsFollowerResponse(boolean follower) {
        super(true, null);
        this.follower = follower;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }
}
