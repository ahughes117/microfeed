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
    public $db_pass = 'AineGifi117';
    //database schema
    public $db_schema = 'microfeed';
    
    //database table structure
    public $db_id = 'microID';
    public $db_title = 'Title';
    public $db_content = 'Content';
    public $db_author = 'Author';
    public $db_date = 'DatePosted';
    
    //database query (includes tables etc)    
    /*public $db_query = "SELECT $db_id , $db_title , $db_subtitle , $db_content , $db_author , $db_date ";
           $db_query .= "FROM $db_schema ";
            "ORDER BY $db_date DESC LIMIT 25";*/
    
    public $db_query = "SELECT microID, Title, Content, Author, DatePosted 
        FROM microfeed 
        ORDER BY DatePosted DESC LIMIT 25 ";
           
    
    //feed title
    public $fd_title = "Alex Hughes' Minifeed";
    //feed subtitle - mini description
    public $fd_subtitle = "Welcome to my minifeed. Here you can find remarkable things about my everyday life";
    //feed link
    public $fd_link = 'href="http://minifeed.ahughes.org/" rel="self"';
    //feed id
    public $fd_id = 'tag:ahughes.org,2013:http://minifeed.ahughes.org/syndication.php';
    
    //author name
    public $auth_name = "Alex Hughes";
    //author email
    public $auth_email = "ahughes@ahughes.org";
}
?>