CREATE TABLE vestigio_historico (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vestigio_id UUID NOT NULL,
    usuario_responsavel_id UUID,
    descricao_modificacao TEXT NOT NULL,
    data_modificacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vestigio_historico FOREIGN KEY (vestigio_id) REFERENCES vestigios(id),
    CONSTRAINT fk_usuario_responsavel_historico FOREIGN KEY (usuario_responsavel_id) REFERENCES users(id)
);