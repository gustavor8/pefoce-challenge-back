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

    CONSTRAINT fk_responsavel_atual FOREIGN KEY (responsavel_atual_id) REFERENCES users(id)
);