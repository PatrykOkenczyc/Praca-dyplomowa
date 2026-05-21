<?php
include "db.php";

$email = $_POST['email'];
$password = $_POST['password'];

$sql = "SELECT * FROM users WHERE email='$email'";
$result = $conn->query($sql);

if ($result->num_rows == 0) {
    echo json_encode(["status" => "error", "message" => "Nie ma takiego użytkownika"]);
    exit;
}

$user = $result->fetch_assoc();

if (password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => "success",
        "user_id" => $user['id']
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Złe hasło"]);
}
?>