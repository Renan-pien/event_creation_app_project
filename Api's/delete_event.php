<?php
header("Content-Type: application/json");
require_once "conexao.php";

// Só aceita POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Método inválido. Use POST."
    ]);
    exit;
}

// ID enviado
$id = $_POST["id"] ?? null;

if (!$id || !is_numeric($id)) {
    echo json_encode([
        "success" => false,
        "message" => "ID inválido ou ausente"
    ]);
    exit;
}

// Prepara comando seguro
$sql = "DELETE FROM eventos WHERE id = ?";
$stmt = $con->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "success" => false,
        "message" => "Erro no prepare",
        "erro" => $con->error
    ]);
    exit;
}

$stmt->bind_param("i", $id);

// Execução
if ($stmt->execute()) {

    // Se não excluiu nenhuma linha -> ID não existe
    if ($stmt->affected_rows === 0) {
        echo json_encode([
            "success" => false,
            "message" => "Nenhum evento encontrado com esse ID"
        ]);
    } else {
        echo json_encode([
            "success" => true,
            "message" => "Evento excluído com sucesso",
            "id" => $id
        ]);
    }

} else {
    echo json_encode([
        "success" => false,
        "message" => "Erro ao excluir evento",
        "erro" => $stmt->error
    ]);
}

$stmt->close();
$con->close();
