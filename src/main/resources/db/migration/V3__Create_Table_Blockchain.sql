CREATE TABLE blockchain (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_bloco BIGINT NOT NULL UNIQUE,
    hash_anterior VARCHAR(255) NOT NULL,
    hash_atual VARCHAR(255) NOT NULL UNIQUE,
    carimbo_de_tempo TIMESTAMP WITH TIME ZONE NOT NULL
);
