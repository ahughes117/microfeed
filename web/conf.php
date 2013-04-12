<?php 

/*
 * Configuration file for minifeed
 */

class Configuration {
    //database url
    public $db_url = 'localhost';
    //database user
    public $db_usr = 'root';
    //database password
    public $db_pass = 'password';
    //database schema
    public $db_schema = 'microfeed';
    
    //database table structure
    public $db_id = 'microID';
    public $db_title = 'Title';
    public $db_author = 'Author';
    public $db_content = 'Content';
    public $db_date = 'DatePosted';
    public $db_status = 'Status';
    public $db_table = 'microfeed';
    
    //TODO Find a way to write the query soft coded 
    public $db_query = "
        SELECT * 
        FROM microfeed 
        WHERE Status = 1 
        ORDER BY DatePosted DESC "; 
    
    public $db_getFeed = "
        SELECT * 
        FROM microfeed
        WHERE microID = ";
           
    
    //feed title
    public $fd_title = "Alex Hughes' Microfeed";
    //feed
    public $fd = 'http://microfeed.ahughes.org/';
    //feed link
    public $fd_link = 
"http://microfeed.ahughes.org?microID=";
    //feed id
    public $fd_id = "tag:ahughes.org,2013:http://localhost/microfeed/microID=";
    
    //feed image
    public $fd_img = "http://ahughes.org/images/ahughes.jpg";
    
    //author name
    public $auth_name = "Alex Hughes";
}
?>
