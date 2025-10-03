CREATE TABLE transferencias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bloco_blockchain_id UUID,
    responsavel_origem_id UUID NOT NULL,
    responsavel_destino_id UUID NOT NULL,
    motivo TEXT,
    data_transferencia TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assinatura_digital TEXT,
    hash_transacao VARCHAR(255),

    CONSTRAINT fk_responsavel_origem FOREIGN KEY (responsavel_origem_id) REFERENCES users(id),
    CONSTRAINT fk_responsavel_destino FOREIGN KEY (responsavel_destino_id) REFERENCES users(id),
    CONSTRAINT fk_bloco_blockchain FOREIGN KEY (bloco_blockchain_id) REFERENCES blockchain(id)
);