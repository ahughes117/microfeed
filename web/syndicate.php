<?php

/*
 * All functionality of the micro-atom-feed 
 */

include 'conf.php';

$conf = new Configuration();

header('Content-type: text/xml');

$link = mysql_connect($conf->db_url, $conf->db_usr, $conf->db_pass)
        or die('Could not connect: ' . mysql_error());

mysql_select_db($conf->db_schema)
        or die('Could not select database');

$result = mysql_query($conf->db_query)
        or die('Query failed: ' . mysql_error());

echo "<?xml version='1.0' encoding='iso-8859-1' ?>";

function date3339($timestamp = 0) {
    if (!$timestamp) {
        $timestamp = time();
    }

    $date = date('Y-m-d\TH:i:s', $timestamp);

    $matches = array();
    if (preg_match('/^([\-+])(\d{2})(\d{2})$/', date('O', $timestamp), $matches)) {
        $date .= $matches[1] . $matches[2] . ':' . $matches[3];
    } else {
        $date .= 'Z';
    }

    return $date;
}
?>

<feed xml:lang="en-US" xmlns="http://www.w3.org/2005/Atom"> 
    <title><?php $conf->fd_title ?></title> 
    <link <?php $conf->fd_link ?> /> 
    <updated><?php echo date3339(); ?></updated>
    <author> 
        <name> <?php echo $conf->auth_name ?></name>
    </author>
    <id><?php $conf->fd_id ?></id> 


    <?php
    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        if ($i > 0) {
            echo "</entry>";
        }
        $articleDate = $row[$conf->db_date];
        $articleDateRfc3339 = date3339(strtotime($articleDate));
        echo "<entry>";
        echo "<title>";
        echo $row['Title'];
        echo "</title>";
        echo "<link type='text/html' href='". $conf->fd_link . $row[$conf->db_id] . "'/>";
        echo "<id>";
        echo $conf->fd_id . $row[$conf->db_id];
        echo "</id>";
        echo "<updated>";
        echo $articleDateRfc3339;
        echo "</updated>";
        echo "<author>";
        echo "<name>";
        echo $row['Author'];
        echo "</name>";
        echo "</author>";
        echo "<summary>";
        echo $row['Content'];
        echo "</summary>";
        $i++;
    }
    ?>
</entry>
</feed>
