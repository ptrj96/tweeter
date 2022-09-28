package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    public  interface FollowingObserver {
        void handleGetFollowingSuccess(List<User> following, boolean morePages);
        void handleGetFollowingFailure(String message);
        void handleGetFollowingThrewException(Exception ex);
    }
    public  void getFollowing(AuthToken token, User user, int limit, User lastFollowee, FollowingObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(token, user, limit, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(getFollowingTask);
    }

    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {
        private FollowingObserver observer;
        public GetFollowingHandler(FollowingObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.handleGetFollowingSuccess(followees, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.handleGetFollowingFailure(message);
//                Toast.makeText(getContext(), "Failed to get following: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.handleGetFollowingThrewException(ex);
//                Toast.makeText(getContext(), "Failed to get following because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
