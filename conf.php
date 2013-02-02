<?php 

/*
 * Configuration file for minifeed
 */

class Configuration {
    //database url
    public $db_url = '83.212.100.114';
    //database user
    public $db_usr = 'minifeed';
    //database password
    public $db_pass = 'deefinim117';
    //database schema
    public $db_schema = 'miniblog';
    //database query (includes tables etc)
    public $db_query = 'SELECT miniID, Title, Title, Subtitle, Author, Posted
        FROM miniblog 
        ORDER BY POST DESC LIMIT 25';
    
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