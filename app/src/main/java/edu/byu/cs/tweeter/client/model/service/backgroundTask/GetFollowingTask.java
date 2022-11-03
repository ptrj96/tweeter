package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        try {
            ServerFacade facade = new ServerFacade();
            String targetUserAlias = getTargetUser() == null ? null : getTargetUser().toString();
            String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().toString();

            FollowingRequest request = new FollowingRequest(getAuthToken(), targetUserAlias, getLimit(), lastFolloweeAlias);
            FollowingResponse response = facade.getFollowees(request);

            if (response.isSuccess()) {
                return new Pair<>(response.getFollowees(), response.getHasMorePages());
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
        catch (Exception ex) {
            sendExceptionMessage(ex);
        }


        return null;
    }
}
