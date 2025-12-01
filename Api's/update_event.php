<?php

header("Content-Type: application/json");

// Aceita somente POST
if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["success" => false, "message" => "Método inválido"]);
    exit;
}

require_once "conexao.php";

// Recebe dados
$id    = $_POST["id"] ?? null;
$nome  = $_POST["nome"] ?? null;
$local = $_POST["local"] ?? null;
$data  = $_POST["data"] ?? null;  // dd/MM/yyyy
$hora  = $_POST["hora"] ?? null;  // HH:mm

// Validação básica
if (!$id || !$nome || !$local || !$data || !$hora) {
    echo json_encode(["success" => false, "message" => "Parâmetros ausentes"]);
    exit;
}


$dataBR   = str_replace('/', '-', $data); 
$dataSQL  = date("Y-m-d", strtotime($dataBR));

$dtEvento = strtotime("$dataSQL $hora");
$agora    = time();

if ($dtEvento === false) {
    echo json_encode(["success" => false, "message" => "Data ou hora inválida"]);
    exit;
}

// Evento atualizado para um horário no passado?
if ($dtEvento <= $agora) {
    echo json_encode(["success" => false, "message" => "O evento deve ser marcado para um horário futuro"]);
    exit;
}


// Atualiza evento
$sql = "
    UPDATE eventos 
    SET nome_evento = ?, 
        local_evento = ?, 
        data_evento = ?, 
        hora_evento = ?
    WHERE id = ?
";

$stmt = $con->prepare($sql);

if (!$stmt) {
    echo json_encode(["success" => false, "message" => "Erro no prepare", "erro" => $con->error]);
    exit;
}

$stmt->bind_param("ssssi", $nome, $local, $dataSQL, $hora, $id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true, 
        "message" => "Evento atualizado com sucesso",
        "id"       => $id  // devolve ID para re-agendar alarme
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Erro ao atualizar evento",
        "erro"     => $stmt->error
    ]);
}

$stmt->close();
$con->close();
