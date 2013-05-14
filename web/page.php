<?php

/*
 * Functionality for feed webpage.
 */

require_once ('conf.php');

/**
 * This function fetches and prints the feeds out of the database.
 * $number = SQL LIMIT 
 * 
 * @global type $con
 * @global type $conf
 * @param type $number
 * @return type
 * @throws Exception
 */
function fetch_feeds($number) {
    global $con;
    global $conf;

    try {
        $stmt = $con->prepare_statement($conf->db_get_feeds);

        //checking if query is bad written or DB table does not exist
        if (!$stmt)
            throw new Exception();

        $stmt->bind_param("i", $number);
        $stmt->execute();
        $stmt->bind_result($db_id, $db_title, $db_content, $db_date, $db_author);
        $stmt->store_result();

        //Writing the posts on the page
        echo '<p>&nbsp;</p>';
        while ($stmt->fetch()) {
            echo "<div class='wrapper'><p class = 'fd_title'><a href='{$conf->fd_link}{$db_id}'>{$db_title}</a></p>";
            echo "<p class='fd_content'><blockquote>{$db_content}</blockquote></p></div>";
        }
    } catch (Exception $x) {
        echo "<p>Either there are no feeds in the DB or something bad happened...</p>";
    }
    $stmt->close();
    return $number;
}

function fetch_feed($id) {

    global $con;
    global $conf;

    try {
        $stmt = $con->prepare_statement($conf->db_get_feed);

        //checking if query is bad written or DB table does not exist
        if (!$stmt)
            throw new Exception();

        $stmt->bind_param("i", $id);
        $stmt->execute();
        $stmt->bind_result($db_id, $db_title, $db_content, $db_date);
        $stmt->store_result();

        //Writing the post on the page
        echo '<p>&nbsp;</p>';
        if ($stmt->num_rows == 0)
            echo "<p>Either you tried to inject SQL but to no avail, OR (more likely) you're looking for something that
                <i>shall not exist, will not exist, or died and does not exist anymore.</i></p>
                <p>Apologies for that.</p>";
        else
            while ($stmt->fetch()) {
                echo "<div class='wrapper'><p class='fd_title'>{$db_title}</p>";
                echo "<p><blockquote>{$db_content}</blockquote></p>";
                $date = gmdate('D M jS, H:i:s', strtotime($db_date));
                echo "<p>{$date} Greenwich Mean Time (<a href='http://en.wikipedia.org/wiki/Greenwich_Mean_Time' 
        target='_blank'>GMT</a>)</p></div>";
            }
    } catch (Exception $x) {
        echo "<p>Either there are no feeds in the DB or something bad happened...</p>";
    }
    $stmt->close();
    return 0;
}

?>
