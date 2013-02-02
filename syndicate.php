<?php
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
    <subtitle><?php $conf->fd_subtitle ?></subtitle>
    <link <?php $conf->fd_link ?> /> 
    <updated><?php echo date3339(); ?></updated>
    <author> 
        <name> <?php $conf->auth_name ?></name>
        <email><?php $conf->auth_email ?></email>
    </author>
    <id><?php $conf->fd_id ?></id> 


    <?php
    $i = 0;
    while ($row = mysql_fetch_array($result)) {
        if ($i > 0) {
            echo "</entry>";
        }
        $articleDate = $row['posted'];
        $articleDateRfc3339 = date3339(strtotime($articleDate));
        echo "<entry>";
        echo "<title>";
        echo $row['title'];
        echo "</title>";
        echo "<link type='text/html' href='http://www.fishinhole.com/reports/report.php?id=" . $row['id'] . "'/>";
        echo "<id>";
        echo "tag:fishinhole.com,2008:http://www.fishinhole.com/reports/wreport.php?id=" . $row['id'];
        echo "</id>";
        echo "<updated>";
        echo $articleDateRfc3339;
        echo "</updated>";
        echo "<author>";
        echo "<name>";
        echo $row['author'];
        echo "</name>";
        echo "</author>";
        echo "<summary>";
        echo $row['subtitle'];
        echo "</summary>";
        $i++;
    }
    ?>
</entry>
</feed>
