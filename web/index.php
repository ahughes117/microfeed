<!--
This is the webpage-like presentable stream.
-->
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>
            <?php
            include 'conf.php';
            $conf = new Configuration();
            echo $conf->fd_title;
            ?>
        </title>
        <link href="style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body class="fd_content">
        <?php
        
        include 'page.php';
        $link = mysql_connect($conf->db_url, $conf->db_usr, $conf->db_pass)
                or die('Could not connect: ' . mysql_error());
        
        mysql_set_charset("utf8");

        mysql_select_db($conf->db_schema)
                or die('Could not select database');
        
        $microID = htmlspecialchars($_GET["microID"]);
        
        if($microID != null){
            fetchFeed($conf, $microID);
        } else {
            fetchFeeds($conf);
        }
        ?>
    </body>
</html>
