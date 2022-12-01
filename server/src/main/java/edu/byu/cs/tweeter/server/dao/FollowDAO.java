package edu.byu.cs.tweeter.server.dao;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.FollowListResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements IFollowDAO {
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private Table followTable = dynamoDB.getTable("340_tweeter_follows");
    @Override
    public void followUser(User follower, User followee) {
        Item item = new Item().withPrimaryKey("follower_handle", follower.getAlias(), "followee_handle", followee.getAlias())
                .withString("follower_first_name", follower.getFirstName())
                .withString("follower_last_name", follower.getLastName())
                .withString("followee_first_name", followee.getFirstName())
                .withString("followee_last_name", followee.getLastName());

        followTable.putItem(item);
    }

    @Override
    public void unfollowUser(User follower, User followee) {
        DeleteItemSpec spec = new DeleteItemSpec().withPrimaryKey("follower_handle", follower.getAlias(), "followee_handle", followee.getAlias());
        followTable.deleteItem(spec);
    }

    @Override
    public boolean isFollower(User follower, User followee) {
        Index followsIndex = followTable.getIndex("follows_index");
        QuerySpec spec = new QuerySpec().withKeyConditionExpression("followee_handle = :followee and follower_handle = :follower")
                .withValueMap(new ValueMap().withString(":followee", followee.getAlias()).withString(":follower", follower.getAlias()))
                .withMaxResultSize(10);

        ItemCollection<QueryOutcome> items = followsIndex.query(spec);
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.getString("follower_handle").equals(follower.getAlias())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FollowListResponse getFollowers(String user, String lastItem) {
        QuerySpec spec = new QuerySpec().withKeyConditionExpression("followee_handle = :alias")
                .withValueMap(new ValueMap().withString(":alias", user))
                .withMaxResultSize(10);

        if (lastItem != null) {
            spec.withExclusiveStartKey("followee_handle", user, "follower_handle", lastItem);
        }

        Index followsIndex = followTable.getIndex("follows_index");
        ItemCollection<QueryOutcome> itemCollection = followsIndex.query(spec);
        Iterator<Item> itemIterator = itemCollection.iterator();
        ArrayList<User> followers = new ArrayList<>();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            String firstName = item.getString("follower_first_name");
            String lastName = item.getString("follower_last_name");
            String alias = item.getString("follower_handle");
            User follower = new User(firstName, lastName, alias, null);
            followers.add(follower);
        }

        boolean hasMorePages = itemCollection.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null;
        return new FollowListResponse(followers, hasMorePages);
    }

    @Override
    public FollowListResponse getFollowees(String user, String lastItem) {
        QuerySpec spec = new QuerySpec().withKeyConditionExpression("follower_handle = :alias")
                .withValueMap(new ValueMap().withString(":alias", user))
                .withMaxResultSize(10);

        if (lastItem != null) {
            spec.withExclusiveStartKey("follower_handle", user, "followee_handle", lastItem);
        }

        ItemCollection<QueryOutcome> itemCollection = followTable.query(spec);
        Iterator<Item> itemIterator = itemCollection.iterator();
        ArrayList<User> followers = new ArrayList<>();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            String firstName = item.getString("followee_first_name");
            String lastName = item.getString("followee_last_name");
            String alias = item.getString("followee_handle");
            User follower = new User(firstName, lastName, alias, null);
            followers.add(follower);
        }

        boolean hasMorePages = itemCollection.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null;
        return new FollowListResponse(followers, hasMorePages);
    }
}
