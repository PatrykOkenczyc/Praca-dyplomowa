<?php
include "db.php";

$email = $_POST['email'];
$code = $_POST['code'];

$sql = "SELECT * FROM users WHERE email='$email'";
$result = $conn->query($sql);

$user = $result->fetch_assoc();

if ($user['reset_code'] == $code && strtotime($user['reset_expires']) > time()) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error"]);
}