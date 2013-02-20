<?php

/*
 * Functionality for feed webpage.
 */

function fetchFeeds($conf) {

    $result = mysql_query($conf->db_query)
            or die('Query failed: ' . mysql_error());

    echo "<p><strong>my latest microfeeds:</strong></p>";

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo '<p class = "fd_title"><strong><a href="' . $conf->fd_link . $row[$conf->db_id] . '">' .
        $row[$conf->db_title] . "</a></strong></p>";
        
        echo '<p class="fd_content"><blockquote>' . $row[$conf->db_content] . '</blockquote></p>';
        $i++;
    }
}

function fetchFeed($conf, $id) {
        
    $result = mysql_query($conf->db_getFeed . $id)
            or die('Query failed: ' . mysql_error());

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo "<p><strong>" . $row[$conf->db_title] . "</strong></p>";
        
        echo "<p><blockquote>" . $row[$conf->db_content] . "</blockquote></p>";
        echo "<p>" . date('D M jS, h:i:s', strtotime($row[$conf->db_date])) . "</p>";
        echo "<p><a href='" . $conf->fd . "'>Back to microfeed stream </a></p>";
        
        $i++;
    }
}

?>
