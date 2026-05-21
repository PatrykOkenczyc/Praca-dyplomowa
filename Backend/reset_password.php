<?php
include "db.php";

$email = $_POST['email'];
$password = password_hash($_POST['password'], PASSWORD_DEFAULT);

$sql = "UPDATE users 
        SET password='$password', reset_code=NULL, reset_expires=NULL 
        WHERE email='$email'";

if ($conn->query($sql)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error"]);
}