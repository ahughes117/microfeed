<?php

/*
 * MySQL connector wrapper class
 * All basic functionality is included here.
 * 
 */

require_once('conf.php');

global $mi6_con;
$mi6_con = new MySQLMI6();

class MySQLMI6 {

    public $config;
    public $mysqli;

    public function MySQLMI6() {
        $this->config = new Configuration();
        $this->mysqli = new mysqli($this->config->mi6_url, $this->config->mi6_user, 
                $this->config->mi6_pass, $this->config->mi6_schema);
    }

    /**
     * This function creates and returns a prepared statement.
     * 
     * @param type $aStatement
     * @return type
     */
    public function prepare_statement_mi6($aStatement) {
        return ($this->mysqli->prepare($aStatement));
    }

    /**
     * This function executes an update and returns true if succesful, false if
     * failed.
     * 
     * @param type $aQuery
     * @return boolean
     */
    public function executeUpdate($aQuery) {
        if (($this->mysqli->query($aQuery)) === TRUE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function executes a query and returns the result. DO NOT USE with 
     * parameter passing, use a prepared statement instead.
     * 
     * USED ONLY FOR STATIC FIXED QUERIES
     * 
     * @param type $aQuery
     * @return type
     */
    public function send_query($aQuery) {
        $result = $this->mysqli->query($aQuery);
        return $result;
    }

    /**
     * Returns the last inserted id
     * 
     * @return type
     */
    public function inserted_id() {
        return $this->mysqli->insert_id;
    }

    /**
     * Attempts to close a connection
     * 
     * @return type
     */
    public function close_connection() {
        return $this->mysqli->close();
    }

}

?>
