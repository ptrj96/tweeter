package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.FollowCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowListResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StatusListResponse;
import edu.byu.cs.tweeter.server.dao.*;
import org.junit.jupiter.api.Test;

import java.awt.image.TileObserver;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UserDAOTest {
    UserDAO dao = new UserDAO();
    AuthTokenDAO authTokenDAO = new AuthTokenDAO();

    @Test
    public void testCreateUser() {
        RegisterRequest registerRequest = new RegisterRequest("@TestUser", "password", "First", "Last", "aaaaaaaaaaaa");
        UserService userService = new UserService();
        LoginResponse registerResponse = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("@TestUser", "password");
        LoginResponse loginResponse = userService.login(loginRequest);
        AuthToken token = loginResponse.getAuthToken();

        RegisterRequest testRegister1 = new RegisterRequest("@Jim", "password", "Jim" , "Test", "aaaaaaaaaaaa");
        RegisterRequest testRegister2 = new RegisterRequest("@Jake", "password", "Jake" , "Test", "aaaaaaaaaaaa");
        RegisterRequest testRegister3 = new RegisterRequest("@John", "password", "John" , "Test", "aaaaaaaaaaaa");
        RegisterRequest testRegister4 = new RegisterRequest("@Jeff", "password", "Jeff" , "Test", "aaaaaaaaaaaa");
        LoginResponse jim = userService.register(testRegister1);
        LoginResponse jake = userService.register(testRegister2);
        LoginResponse john = userService.register(testRegister3);
        LoginResponse jeff = userService.register(testRegister4);

        FollowUserRequest followJim = new FollowUserRequest(token, jim.getUser(), loginResponse.getUser());
        FollowUserRequest followJake = new FollowUserRequest(token, jake.getUser(), loginResponse.getUser());
        FollowUserRequest followJohn = new FollowUserRequest(token, john.getUser(), loginResponse.getUser());
        FollowUserRequest followJeff = new FollowUserRequest(token, jeff.getUser(), loginResponse.getUser());
        FollowUserRequest testFollow = new FollowUserRequest(token, loginResponse.getUser(), jim.getUser());
        FollowService followService = new FollowService();
        followService.followUser(followJim);
        followService.followUser(followJake);
        followService.followUser(followJohn);
        followService.followUser(followJeff);
        followService.followUser(testFollow);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        String now = dateFormat.format(new Date().getTime());
        List<String> testList = new ArrayList<>();
        Status status = new Status("This is a test post", loginResponse.getUser(), now, testList, testList);
        PostStatusRequest postStatusRequest = new PostStatusRequest(token, loginResponse.getUser(), status);
        StatusService statusService = new StatusService();
        statusService.postStatus(postStatusRequest);
        status = new Status("This is jims status", jim.getUser(), now, testList, testList);
        postStatusRequest = new PostStatusRequest(jim.getAuthToken(), jim.getUser(), status);
        statusService.postStatus(postStatusRequest);

        FeedService feedService = new FeedService();
        StatusListRequest jimFeedRequest = new StatusListRequest(jim.getAuthToken(), "@Jim", 25, null);
        StatusListResponse jimFeed = feedService.getFeed(jimFeedRequest);
        System.out.println(jimFeed.getFeed().get(0));

//        followService.unfollowUser(followJeff);
        FollowListRequest followListRequest = new FollowListRequest(token, loginResponse.getUser().getAlias(), 10, null);
        FollowListResponse followListResponse = followService.getFollowees(followListRequest);
        System.out.println(followListResponse.getFollowList().size());
        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(token, jim.getUser(), loginResponse.getUser());
        System.out.println(followService.isFollower(isFollowerRequest).isFollower());
    }
}
