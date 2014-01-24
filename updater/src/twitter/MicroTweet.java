package twitter;

import java.io.IOException;
import sql.Connector;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import util.Credentials;

/**
 * This class contains the twitter functionality of the application
 *
 * @author ahughes
 */
public class MicroTweet {

    private TwitterAuth ta;
    private Twitter twitter;

    /**
     * Constructor for MicroTweet object. Includes credential importing, as well
     * as Twitter authorisation.
     *
     * @throws TwitterException
     * @throws IOException
     */
    public MicroTweet() throws TwitterException, IOException {

        //Importing twitter credentials...
        ta = (TwitterAuth) Credentials.loadCredentials("twitter.dat");

        if (ta != null) {

            //Creating and authorising twitter object...
            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(ta.getConsumerKey(), ta.getConsumerKeySecret());
            AccessToken oathAccessToken = new AccessToken(ta.getAccessToken(),
                    ta.getAccessTokenSecret());
            twitter.setOAuthAccessToken(oathAccessToken);
            twitter.verifyCredentials();

            if (Connector.LOGGER) {
                System.out.println("Twitter Authorisation Successful...");
            }
        } else {
            throw (new IOException());
        }
    }

    /**
     * Updates a twitter status and connects it to the microfeed webpage.
     *
     * @param aStatus
     * @throws TwitterException
     */
    public void updateStatus(String aStatus) throws TwitterException {
        if (aStatus.length() <= 140) {
            System.out.println(aStatus);
            twitter.updateStatus(aStatus);
        } else {
            throw new TwitterException("Max Character Error");
        }
    }
    
    //returns the URL
    public String getUrl() {
        return ta.getUrl();
    }

    /**
     * Composes a suitable tweet for the microfeed post, using the title and the
     * content. Makes sure that the tweet is less than 140 characters
     *
     * @param aTitle
     * @param aContent
     * @param aLink
     * @return
     */
    public String composeTweet(String aTitle, String aLink) {
        String tweet;

        String urlSeparator = " || ";

        //text lengths
        int tweetN = 140;
        int urlN = aLink.length();  //better safe than sorry
        int separN = 4;

        //if title + url is less than 140, tweet as is.
        if (aTitle.length() + separN + urlN < tweetN) {
            tweet = aTitle + urlSeparator + aLink;
        } else {
            tweet = aTitle.substring(0, tweetN - urlN - separN - 3);
            tweet += "..." + urlSeparator + aLink;
        }

        return tweet;
    }
}
