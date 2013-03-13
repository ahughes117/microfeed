
package twitter;

import java.io.Serializable;
import util.Credentials;

/**
 * The Twitter Authorisation System entity class
 * @author ahughes
 */
public class TwitterAuth extends Credentials implements Serializable {
    
    private String consumerKey;
    private String consumerKeySecret;
    private String accessToken;
    private String accessTokenSecret;

    /**
     * Full Constructor
     * 
     * @param consumerKey
     * @param consumerKeySecret
     * @param accessToken
     * @param accessTokenSecret 
     */
    public TwitterAuth(String consumerKey, String consumerKeySecret, String accessToken, String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerKeySecret = consumerKeySecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerKeySecret() {
        return consumerKeySecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }
}
