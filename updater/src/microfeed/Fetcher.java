
package microfeed;
import java.sql.*;
import java.util.*;
import sql.*;

/**
 * Class Fetcher
 * This class contains the main functionality for the microfeed application.
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
        
        while (feedR.next()){
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
        
        while(authorR.next()) {
            authors.add(authorR.getString("Author"));
        }
        return authors;
    }
    
    public ArrayList<Feed> getFeeds() {
        return feeds;
    }
    
    public ArrayList<String> getAuthors() {
        return authors;
    }
}
