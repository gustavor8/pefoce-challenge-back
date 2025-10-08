-- Índices na tabela 'vestigios'
CREATE INDEX IF NOT EXISTS idx_vestigios_status ON vestigios(status);
CREATE INDEX IF NOT EXISTS idx_vestigios_responsavel_atual ON vestigios(responsavel_atual_id);

-- Índices na tabela 'transferencias'
CREATE INDEX IF NOT EXISTS idx_transferencias_responsavel_origem ON transferencias(responsavel_origem_id);
CREATE INDEX IF NOT EXISTS idx_transferencias_responsavel_destino ON transferencias(responsavel_destino_id);

-- Índice na tabela de junção 'transferencia_vestigios'
-- Acelera a busca de todas as transferências de um vestígio específico
CREATE INDEX IF NOT EXISTS idx_transferencia_vestigios_vestigio_id ON transferencia_vestigios(vestigio_id);