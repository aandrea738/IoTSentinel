DO $$
BEGIN
    -- Verifica se la tabella RegolaSoglia esiste
    IF to_regclass('public.regolasoglia') IS NOT NULL THEN
        -- Inserisci le regole di soglia per catturare i valori "isError" del SimulatoreRunner
        -- Simulatore genera per isError: TEMPERATURA > 80, PRESSIONE > 5, CO2 > 1000
        INSERT INTO RegolaSoglia (id, nome, tipoMisurazione, soglia, gravita)
        VALUES
            (1, 'Allarme Temperatura Critica (Solo Sensore 1)', 'TEMPERATURA', 50.0, 'ALTA'),
            (2, 'Allarme Pressione Pericolosa (Solo Sensore 3)', 'PRESSIONE', 3.5, 'ALTA'),
            (3, 'Allarme CO2 Fuori Norma (Solo Sensore 7)', 'CO2', 800.0, 'MEDIA')
        ON CONFLICT (id) DO UPDATE SET 
            nome = EXCLUDED.nome, 
            tipoMisurazione = EXCLUDED.tipoMisurazione,
            soglia = EXCLUDED.soglia, 
            gravita = EXCLUDED.gravita;

        -- Aggiorna la sequenza per l'id di RegolaSoglia
        PERFORM setval(pg_get_serial_sequence('RegolaSoglia', 'id'), GREATEST(3, COALESCE(MAX(id), 0)))
        FROM RegolaSoglia;

        -- Collega le regole a sensori specifici popolando la tabella della collection idSensori
        DELETE FROM RegolaSoglia_idSensori WHERE RegolaSoglia_id IN (1, 2, 3);
        
        -- Regola 1 -> Sensore 1
        -- Regola 2 -> Sensore 3
        -- Regola 3 -> Sensore 7
        INSERT INTO RegolaSoglia_idSensori (RegolaSoglia_id, idSensori)
        VALUES 
            (1, 1),
            (2, 3),
            (3, 7);

    END IF;
END $$;
