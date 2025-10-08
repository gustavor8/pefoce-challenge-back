--Table Usuario
CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    cargo VARCHAR(100),
    departamento VARCHAR(100),
    certificado_digital TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--Table Vestigios
CREATE TABLE vestigios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo VARCHAR(100) NOT NULL,
    descricao TEXT,
    local_coleta VARCHAR(255),
    data_coleta TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL,
    responsavel_atual_id UUID NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_responsavel_atual FOREIGN KEY (responsavel_atual_id) REFERENCES usuarios(id)
);

--Table Blockchain
CREATE TABLE blockchain (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_bloco BIGINT NOT NULL UNIQUE,
    hash_anterior VARCHAR(256) NOT NULL,
    hash_atual VARCHAR(256) NOT NULL UNIQUE,
    carimbo_de_tempo TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Table Tranferencia
CREATE TABLE transferencias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    blockchain_id UUID UNIQUE,
    responsavel_origem_id UUID NOT NULL,
    responsavel_destino_id UUID NOT NULL,
    motivo TEXT,
    data_transferencia TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assinatura_digital TEXT,
    hash_transacao VARCHAR(255),

    CONSTRAINT fk_responsavel_origem FOREIGN KEY (responsavel_origem_id) REFERENCES usuarios(id),
    CONSTRAINT fk_responsavel_destino FOREIGN KEY (responsavel_destino_id) REFERENCES usuarios(id),
    CONSTRAINT fk_bloco_blockchain FOREIGN KEY (blockchain_id) REFERENCES blockchain(id)
);

-- Relation Transferencia_Vestigio
CREATE TABLE transferencia_vestigios (
    transferencia_id UUID NOT NULL,
    vestigio_id UUID NOT NULL,
    CONSTRAINT fk_transferencia FOREIGN KEY (transferencia_id) REFERENCES transferencias(id),
    CONSTRAINT fk_vestigio FOREIGN KEY (vestigio_id) REFERENCES vestigios(id),
    PRIMARY KEY (transferencia_id, vestigio_id)
);