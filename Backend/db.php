<?php
$host = "localhost";
$user = "root";
$pass = "";
$db = "reflexapp";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die("Connection failed");
}
?>