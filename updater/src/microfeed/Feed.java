
package microfeed;

import java.sql.Timestamp;

/**
 * Class Feed. The entity class for the microfeeds
 * 
 * @author Alex Hughes <ahughes@ahughes.org>
 */
public class Feed {
    
    private int microID;
    private String author;
    private String title;
    private String content;
    private Timestamp datePosted;
    private int status;

    /**
     * Full Constructor, mainly used when fetching feeds from DB
     * 
     * @param microID
     * @param author
     * @param title
     * @param content
     * @param datePosted 
     * @param status
     */
    public Feed(int microID, String author, String title, String content, 
            Timestamp datePosted, int status) {
        this.microID = microID;
        this.author = author;
        this.title = title;
        this.content = content;
        this.datePosted = datePosted;
        this.status = status;
    }

    /**
     * Partial Constructor, mainly used when creating new feeds.
     * 
     * @param author
     * @param title
     * @param content 
     * @param status 
     */
    public Feed(String author, String title, String content, int status) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getDatePosted() {
        return datePosted;
    }

    public int getMicroID() {
        return microID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
