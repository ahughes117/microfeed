<?php

/*
 * Functionality for feed webpage.
 */

function fetchFeeds($conf, $number) {

    $result = mysql_query($conf->db_query . "LIMIT " . $number)
            or die('Query failed: ' . mysql_error());

    $header = '<table border="0" cellspacing="5" cellpadding="5">
  <tr>
    <td width="101" height="146"><img src="' . $conf->fd_img . '" width="134" height="134" /></td>
    <td>
    <p class = "fd_title">Alex Hughes || microfeeds: </p><p> </p>
    <p class="fd_content">You can have them freshly baked on your plate everyday 
        using your favourite feed reader: <a href="http://microfeed.ahughes.org/feed.php">
"http://microfeed.ahughes.org/feed.php"</a></p>
    </td>
  </tr>
</table>';

    echo $header;
    
    echo '<p>&nbsp</p>';

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo '<p class = "fd_title"><strong><a href="' . $conf->fd_link . $row[$conf->db_id] . '">' .
        $row[$conf->db_title] . "</a></strong></p>";

        echo '<p class="fd_content"><blockquote>' . $row[$conf->db_content] . '</blockquote></p>';
        $i++;
    }
    return $number;
}

function fetchFeed($conf, $id) {

    $result = mysql_query($conf->db_getFeed . $id)
            or die('Query failed: ' . mysql_error());

    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        echo "<p><strong>" . $row[$conf->db_title] . "</strong></p>";

        echo "<p><blockquote>" . $row[$conf->db_content] . "</blockquote></p>";
        echo "<p>" . date('D M jS, h:i:s', strtotime($row[$conf->db_date])) . "</p>";
        /* echo "<p><a href='" . $conf->fd . "'>Back to microfeed stream 
          </a></p>"; */

        $i++;
    }
    if ($i == 0) {
        echo "<p><strong>Either you are trying to inject SQL or the thing you 
            are looking for doesn't exist <anymore, hasn't ever existed, 
            whatever>.</strong></p>";
        /* echo "<p><a href='" . $conf->fd . "'>Back to microfeed 
          stream</a></p>"; */
    }
}

?>
