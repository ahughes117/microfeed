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
    </head>
    <body>
        <?php
        
        include 'page.php';
        $link = mysql_connect($conf->db_url, $conf->db_usr, $conf->db_pass)
                or die('Could not connect: ' . mysql_error());

        mysql_select_db($conf->db_schema)
                or die('Could not select database');
        fetchFeeds($conf);
        
        ?>
    </body>
</html>