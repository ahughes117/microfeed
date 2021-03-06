<?php

require_once('ip.php');

$my_ip = insert_ip();
if (isset($_POST['ip'])) {

    $ip = gethostbyname(urldecode($_POST['ip']));
    if (filter_var($_POST['ip'], FILTER_VALIDATE_IP))
        $request_ip = insert_request($_POST['ip']);
    elseif (strcmp($ip, $_POST['ip']) != 0)
        $request_ip = insert_request($ip);
} elseif (isset($_POST['domain'])) {

    $ip = gethostbyname(urldecode($_POST['domain']));
    if (filter_var($_POST['domain'], FILTER_VALIDATE_IP))
        $request_ip = insert_request($_POST['domain']);
    elseif (strcmp($ip, $_POST['domain']) != 0) {
        $request_ip = insert_request($ip);
    }
}

//closing the connection
close_connection(); 

?>
