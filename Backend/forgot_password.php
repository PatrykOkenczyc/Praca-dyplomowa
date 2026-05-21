<?php
include "db.php";

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';
require 'PHPMailer/src/Exception.php';

$email = $_POST['email'];

$sql = "SELECT * FROM users WHERE email='$email'";
$result = $conn->query($sql);

if ($result->num_rows == 0) {
    echo json_encode(["status" => "error"]);
    exit;
}

$code = rand(100000, 999999);
$expires = date("Y-m-d H:i:s", strtotime("+10 minutes"));

$conn->query("UPDATE users SET reset_code='$code', reset_expires='$expires' WHERE email='$email'");

$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    
    $mail->Username = 'patrykokenczyc@gmail.com';
    $mail->Password = 'onfk gwec fbqf fees';

    $mail->SMTPSecure = 'tls';
    $mail->Port = 587;

    $mail->setFrom('patrykokenczyc@gmail.com', 'Reflex App');
    $mail->addAddress($email);

    $mail->Subject = 'Reset hasla';
    $mail->Body = "Twoj kod resetu: $code";

    $mail->send();

    echo json_encode(["status" => "success"]);

} catch (Exception $e) {
    echo json_encode(["status" => "error"]);
}