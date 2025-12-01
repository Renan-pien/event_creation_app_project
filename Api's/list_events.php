<?php
header("Content-Type: application/json");
require_once "conexao.php";


$sql = "
    SELECT 
        id, 
        nome_evento, 
        local_evento, 
        data_evento, 
        hora_evento
    FROM eventos
    ORDER BY data_evento ASC, hora_evento ASC
";

$result = $con->query($sql);

if (!$result) {
    echo json_encode([
        "success" => false,
        "message" => "Erro ao buscar eventos",
        "erro" => $con->error
    ]);
    exit;
}

$eventos = [];

while ($row = $result->fetch_assoc()) {
    $eventos[] = $row;
}

echo json_encode([
    "success" => true,
    "quantidade" => count($eventos),
    "eventos" => $eventos
]);

$con->close();
