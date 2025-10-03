CREATE TABLE transferencia_vestigios (
    transferencia_id UUID NOT NULL,
    vestigio_id UUID NOT NULL,
    CONSTRAINT fk_transferencia FOREIGN KEY (transferencia_id) REFERENCES transferencias(id),
    CONSTRAINT fk_vestigio FOREIGN KEY (vestigio_id) REFERENCES vestigios(id),
    PRIMARY KEY (transferencia_id, vestigio_id)
);