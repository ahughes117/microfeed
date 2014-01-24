<?php 

/*
 * Configuration file for minifeed
 */

global $conf;
$conf = new Configuration();

//Configuration file for microfeed. (Still a mess)
class Configuration {
    
    //database url
    public $db_url = 'localhost';
    //database user
    public $db_usr = 'root';
    //database password
    public $db_pass = 'AineGifi117';
    //database schema
    public $db_schema = 'ahughes_microfeed';
        
    public $db_get_feeds = "
        SELECT microID, Title, Alias, Content, DatePosted, Author 
        FROM microfeed 
        WHERE Status = 1 
        ORDER BY DatePosted DESC 
        LIMIT ? "; 
    
    public $db_get_feed_id = "
        SELECT microID, Title, Content, DatePosted
        FROM microfeed
        WHERE microID = ? ";
           
    public $db_get_feed_alias = "
        SELECT microID, Title, Content, DatePosted 
        FROM microfeed 
        WHERE Alias = ? ";
    
    //feed title
    public $fd_title = "Alex Hughes' Microfeed";
    //feed
    public $fd = 'http://microfeed.ahughes.org/';
    //feed link using id
    public $fd_link_id = "http://microfeed.ahughes.org?microID=";
    //feed link using alias
    public $fd_link_alias = "http://microfeed.ahughes.org?alias=";
    //feed id
    public $fd_id = "tag:ahughes.org,2013:http://microfeed.ahughes.org/microID=";
    
    //feed image
    public $fd_img = "img/ahughes.jpg";
    //header image
    public $fd_header_img = "img/header.jpg";
    
    //The header text customisable to your needs etc (don't touch text between hashes)
    public $header = "<p><img src='#header_img#' width=1000 height='127' /></p>
        <table border='0' cellspacing='5' cellpadding='5'>
        <tr>
        <td width='101' height='146'><img src='#fd_img#' width='134' height='134' />
        </td>
        <td>
        <p class = 'fd_title'>Alex Hughes || microfeeds: </p>
        <p class='fd_content'>You can have them fresh on your plate everyday
        using your favourite feed reader: 
        <a href='http://microfeed.ahughes.org/feed.php'>'http://microfeed.ahughes.org/feed.php'</a></p>
        <p>Special thanks to my friend and colleague 
        <a href='http://www.karenpasquel.co.uk' target='_blank'>Karen</a> 
        for the 5 minute godly touches on my horrible CSS file.</p> </td></tr></table>";
    
    //author name
    public $auth_name = "Alex Hughes";
    
    //MI6 Tracker configuration follows
    public $mi6_url = "localhost";
    public $mi6_user = "root";
    public $mi6_pass = "AineGifi117";
    public $mi6_schema = "mi6";
    public $mi6_enabled = true;
}
?>
