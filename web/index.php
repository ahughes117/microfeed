<?php
    //Including MI6 tracker
    include_once ('mysql_mi6.php');
    $conf;
    
    if($conf->mi6_enabled) {
        include('tracker.php');
    }
?>
<!--
This is the webpage-like presentable stream.
-->
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>
            <?php
            //uncomment following 2 lines for debugging mode
//            error_reporting(E_ALL | E_STRICT);
//            ini_set("display_errors", "1");
            require_once ('mysql.php');
            
            global $conf;
            echo $conf->fd_title;
            ?>
        </title>
        <link href="style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body class="fd_content">
        <?php
        //initial number of feeds
        $feedN = 10;
        
        require_once ('header.php');
        require_once ('page.php');
        
        $microID = htmlspecialchars($_GET["microID"]);
        $alias = htmlspecialchars($_GET["alias"]);

        if (htmlspecialchars($_GET["feedN"]) != NULL)
            $feedN = htmlspecialchars($_GET["feedN"]);

        if ($microID != null && $alias == null) {
            fetch_feed($microID, "id");
        } elseif ($alias != null && $microID == null) {
            fetch_feed($alias, "alias");
        }else {
            $feedN += fetch_feeds($feedN);
        }
        echo "<p><a href='?feedN=$feedN'>More Feeds</a></p>";
        ?>
        <p>Microfeed is proudly powered by <a 
                href="http://github.com/ahughes117/microfeed/">Microfeed</a>. 
            MicrofeedCeption.</p>
    </body>
</html>
