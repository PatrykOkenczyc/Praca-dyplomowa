<?php
include "db.php";

$user_id = $_POST['user_id'];
$avg = $_POST['avg'];
$min = $_POST['min'];
$max = $_POST['max'];

$sql = "INSERT INTO scores (user_id, avg, min, max)
        VALUES ('$user_id', '$avg', '$min', '$max')";

if ($conn->query($sql)) {
    echo "ok";
} else {
    echo "error";
}
?>