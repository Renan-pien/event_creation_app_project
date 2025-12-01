<?php
header("Content-Type: application/json");
require_once "conexao.php";


$id = $_GET["id"] ?? null;

if (!$id || !is_numeric($id)) {
    echo json_encode([
        "success" => false,
        "message" => "ID inválido ou ausente"
    ]);
    exit;
}

// Consulta
$sql = "
    SELECT id, nome_evento, local_evento, data_evento, hora_evento
    FROM eventos
    WHERE id = ?
";

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
$stmt->execute();
$result = $stmt->get_result();

// Se não existir evento
if ($result->num_rows === 0) {
    echo json_encode([
        "success" => false,
        "message" => "Evento não encontrado"
    ]);
    exit;
}

$evento = $result->fetch_assoc();

// Formata data para dd/MM/yyyy
if (!empty($evento["data_evento"])) {
    $evento["data_evento"] = date("d/m/Y", strtotime($evento["data_evento"]));
}

echo json_encode([
    "success" => true,
    "evento" => $evento
]);

$stmt->close();
$con->close();
