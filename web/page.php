<?php

/*
 * Functionality for feed webpage.
 */

function fetchFeeds($conf) {

    $result = mysql_query($conf->db_query)
            or die('Query failed: ' . mysql_error());

    echo "<p><strong>latest feeds:</strong></p>";

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo "<p><strong><a href='" . $conf->fd_link . $row[$conf->db_id] . "'>" .
        $row[$conf->db_title] . "</a></strong></p>";
        
        echo "<p>" . $row[$conf->db_content] . "</p>";
        $i++;
    }
}

function fetchFeed($conf, $id) {

    $result = mysql_query($conf->$db_getFeed . $id)
            or die('Query failed: ' . mysql_error());

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo "<p><strong><a href='" . $conf->fd_link . $row[$conf->db_id] . "'>" .
        $row[$conf->db_title] . "</a></strong></p>";
        
        echo "<p>" . $row[$conf->db_content] . "</p>";
        echo "<p>" . $row[$conf->db_date] . "</p>";
        
        $i++;
    }
}

?>
