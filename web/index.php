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
            //error_reporting(E_ALL | E_STRICT);
            //ini_set("display_errors", "1");
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

        if (htmlspecialchars($_GET["feedN"]) != NULL)
            $feedN = htmlspecialchars($_GET["feedN"]);

        if ($microID != null) {
            fetch_feed($microID);
        } else {
            $feedN += fetch_feeds($feedN);
        }
        echo "<p><a href='?feedN=$feedN'>More Feeds</a></p>";
        ?>
        <p>Microfeed is proudly powered by <a 
                href="http://github.com/ahughes117/microfeed/">Microfeed</a>. 
            MicrofeedCeption.</p>
    </body>
</html>
