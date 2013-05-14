<?php

require_once ('conf.php');

global $conf;

$header = str_replace("#header_img#", $conf->fd_header_img, $conf->header);
$header = str_replace("#fd_img#", $conf->fd_img, $header);

echo $header;

?>
