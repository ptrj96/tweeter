package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StatusListRequest;
import edu.byu.cs.tweeter.model.net.response.StatusListResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class FeedDAO implements IFeedDAO {
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private Table feedTable = dynamoDB.getTable("340_tweeter_feed");
    @Override
    public void addFeed(Status status, User follower) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        try {
            Date datetime = dateFormat.parse(status.getDate());
            feedTable.putItem(new Item().withPrimaryKey("alias", follower.getAlias(), "dt", datetime.getTime())
                    .withString("status", status.getPost())
                    .withList("urls", status.getUrls())
                    .withList("mentions", status.getMentions()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StatusListResponse getFeed(StatusListRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        QuerySpec spec = new QuerySpec().withKeyConditionExpression("alias = :alias")
                .withValueMap(new ValueMap().withString(":alias", request.getUserAlias()))
                .withMaxResultSize(request.getLimit())
                .withScanIndexForward(false);

        if (request.getLastStatus() != null) {
            try {
                Date date = dateFormat.parse(request.getLastStatus().getDate());
                spec.withExclusiveStartKey("alias", request.getUserAlias(), "dt", date.getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        ItemCollection<QueryOutcome> itemCollection = feedTable.query(spec);
        Iterator<Item> itemIterator = itemCollection.iterator();
        ArrayList<Status> feed = new ArrayList<>();
        DAOFactory daoFactory = new DAOFactory();
        UserDAO userDAO = (UserDAO) daoFactory.create("UserDAO");
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            String datetime = dateFormat.format(item.getLong("dt"));
            User user = userDAO.getUser(item.getString("alias"));
            Status status = new Status(item.getString("status"), user, datetime, item.getList("urls"), item.getList("mentions"));
            feed.add(status);
        }

        boolean hasMorePages = itemCollection.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey() != null;
        return new StatusListResponse(feed, hasMorePages);
    }
}
