<?php

//Including MI6 tracker
include_once ('mysql_mi6.php');
$conf;

if ($conf->mi6_enabled) {
    include('tracker.php');
}

/*
 * All functionality of the micro-atom-feed 
 */

//uncomment following 2 lines for debugging mode
//error_reporting(E_ALL | E_STRICT);
//ini_set("display_errors", "1");

require_once ('mysql.php');

header('Content-type: text/xml');

global $con;
global $conf;

$stmt = $con->prepare_statement($conf->db_get_feeds);

if (!$stmt)
    echo "<p>Ouch. Something really wrong happened. Apologies</p>";

//hardcoding the default feed size
$limit = 50;
$stmt->bind_param("i", $limit);
$stmt->execute();
$stmt->bind_result($id, $title, $alias, $content, $date, $author);
$stmt->store_result();

echo "<?xml version='1.0' encoding='UTF-8' ?>";

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
    <title><?php echo $conf->fd_title ?></title> 
    <link href="http://microfeed.ahughes.org/microfeed/feed.php" rel="self" /> 
    <updated><?php echo date3339(); ?></updated>
    <author> 
        <name> <?php echo $conf->auth_name ?></name>
    </author>
    <id><?php echo $conf->fd_id ?></id> 


    <?php
    $i = 0;
    while ($stmt->fetch()) {
        if ($i > 0) {
            echo "</entry>";
        }
        $articleDateRfc3339 = date3339(strtotime($date));
        echo "<entry>";
        echo '<title type="html">';
        echo $title;
        echo "</title>";
        echo "<link type='text/html' href='" . $conf->fd_link_id . $id . "'/>";
        echo "<id>";
        echo $conf->fd_id . $id;
        echo "</id>";
        echo "<updated>";
        echo $articleDateRfc3339;
        echo "</updated>";
        echo "<author>";
        echo "<name>";
        echo $author;
        echo "</name>";
        echo "</author>";
        echo '<summary type="html"><![CDATA[';
        echo $content;
        echo "]]></summary>";
        $i++;
    }
    ?>
</entry>
</feed>
