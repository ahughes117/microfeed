<?php

/**
 * The "driver-wrapper" database class
 *
 * @author ahughes
 */
require_once ('conf.php');

global $con;
$con = new MySQL();

class MySQL {

    public $conf;
    public $mysqli;

    public function MySQL() {
        $this->conf = new Configuration();
        $this->mysqli = new mysqli($this->conf->db_url, $this->conf->db_usr, $this->conf->db_pass, $this->conf->db_schema);
    }
    
     /**
     * This function creates and returns a prepared statement.
     * If stmt == NULL, then query is not written properly, or DB_ERROR occured
     * 
     * @param type $stmt
     * @return type
     */
    public function prepare_statement($stmt) {
        return $this->mysqli->prepare($stmt);
    }

}

?>
