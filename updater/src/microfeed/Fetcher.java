package microfeed;

import java.sql.*;
import java.util.*;
import sql.*;

/**
 * Class Fetcher This class contains the main functionality for the microfeed
 * application.
 *
 * @author Alex Hughes <alexhughes117@gmail.com>
 */
public class Fetcher {

    private Connector con;
    private ArrayList<Feed> feeds;
    private ArrayList<String> authors;

    public Fetcher(Connector aConnector) {
        con = aConnector;
    }

    public ArrayList<Feed> fetchFeeds() throws SQLException {
        feeds = new ArrayList();
        Feed feed;
        ResultSet feedR = con.sendQuery(""
                + "SELECT * "
                + "FROM microfeed ");

        while (feedR.next()) {
            feed = new Feed(feedR.getInt("microID"), feedR.getString("Author"),
                    feedR.getString("Title"), feedR.getString("Content"),
                    feedR.getTimestamp("DatePosted"), feedR.getInt("Status"));

            feeds.add(feed);
        }
        return feeds;
    }

    public ArrayList<String> fetchAuthors() throws SQLException {
        authors = new ArrayList();

        ResultSet authorR = con.sendQuery(""
                + "SELECT DISTINCT Author "
                + "FROM microfeed ");

        while (authorR.next()) {
            authors.add(authorR.getString("Author"));
        }
        return authors;
    }

    public void createFeed(Feed aFeed) throws SQLException {
        String update = ""
                + "INSERT INTO `microfeed` (`Author`, `Title`, `Content`, `Status`) VALUES "
                + "('" + aFeed.getAuthor() + "', '" + aFeed.getTitle() + "', '"
                + aFeed.getContent() + "', " + aFeed.getStatus() + ") ";

        con.sendUpdate(update);
    }

    public Feed fetchFeed(int anID) throws SQLException {
        Feed feed = null;

        ResultSet feedR = con.sendQuery(""
                + "SELECT * "
                + "FROM microfeed "
                + "WHERE microID = " + anID);

        while (feedR.next()) {
            feed = new Feed(feedR.getInt("microID"), feedR.getString("Author"),
                    feedR.getString("Title"), feedR.getString("Content"),
                    feedR.getTimestamp("DatePosted"), feedR.getInt("Status"));
        }

        return feed;
    }

    /**
     * This function fetches the latest draft from the database. If available it
     * returns it, otherwise it returns null.
     *
     * @return
     * @throws SQLException
     */
    public Feed fetchDraft() throws SQLException {
        Feed draft = null;

        ResultSet draftR = con.sendQuery(""
                + "SELECT Author, Title, Content "
                + "FROM microfeed "
                + "WHERE Status = 0 "
                + "ORDER BY DatePosted DESC "
                + "LIMIT 1 ");

        while (draftR.next()) {
            draft = new Feed(draftR.getString("Author"), draftR.getString("Title"),
                    draftR.getString("Content"), 0);
        }

        return draft;
    }

    /**
     * This function cleans all draft posts from database. It is used after a
     * post is made, to clean all previous draft versions.
     *
     * @throws SQLException
     */
    public void cleanDrafts() throws SQLException {
        con.sendUpdate(""
                + "DELETE "
                + "FROM microfeed "
                + "WHERE Status = 0 ");
    }

    public ArrayList<Feed> getFeeds() {
        return feeds;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }
}
