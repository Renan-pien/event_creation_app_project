<?php
header("Content-Type: application/json");
include("conexao.php");

if (!isset($_POST["email"]) || !isset($_POST["senha"])) {
    echo json_encode(["success" => false, "message" => "Parâmetros ausentes"]);
    exit;
}

$email = $_POST["email"];
$senha = $_POST["senha"];

// compara usando senha criada com PASSWORD()
$sql = "SELECT * FROM usuarios WHERE email = '$email' LIMIT 1";
$result = mysqli_query($con, $sql);

if (mysqli_num_rows($result) > 0) {
    $user = mysqli_fetch_assoc($result);

    if (password_verify($senha, $user['senha'])) {
    echo json_encode([
        "success" => true,
        "message" => "Login ok",
        "user" => [
            "id" => $user['id'],
            "email" => $user['email'],
            "usuario" => $user['usuario'] 
        ]
    ]);
    } else {
        echo json_encode(["success" => false, "message" => "Senha incorreta"]);
    } 
    

} else {
    echo json_encode(["success" => false, "message" => "Email não encontrado"]);
}

mysqli_close($con);
?>
