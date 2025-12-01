<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

include "conn.php";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    if (!isset($_POST["ids"])) {
        echo json_encode(["success" => false, "message" => "Nenhum ID recebido"]);
        exit;
    }

    $ids = $_POST["ids"]; 

    $sql = "DELETE FROM eventos WHERE id IN ($ids)";

    if (mysqli_query($con, $sql)) {
        echo json_encode(["success" => true, "message" => "Eventos excluídos"]);
    } else {
        echo json_encode(["success" => false, "message" => "Erro ao excluir"]);
    }

} else {
    echo json_encode(["success" => false, "message" => "Método inválido"]);
}
?>
