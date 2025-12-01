<?php
header("Content-Type: application/json");
require_once "conexao.php";

if (!isset($_POST['ids'])) {
    echo json_encode(["success" => false, "message" => "IDs não enviados"]);
    exit;
}

$ids = $_POST['ids'];
$query = "DELETE FROM eventos WHERE id IN ($ids)";

if ($conn->query($query)) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "message" => "Erro ao excluir"]);
}
?>
