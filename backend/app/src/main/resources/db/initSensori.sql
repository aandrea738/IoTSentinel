DO $$
BEGIN
    IF to_regclass('public.sensorebase') IS NOT NULL THEN
        ALTER TABLE SensoreBase
            DROP CONSTRAINT IF EXISTS sensorebase_dtype_check;

        ALTER TABLE SensoreBase
            ADD CONSTRAINT sensorebase_dtype_check
            CHECK (DTYPE IN ('TEMPERATURA', 'PRESSIONE', 'VIBRAZIONE', 'CO2'));

        INSERT INTO SensoreBase (id, DTYPE, stato, unitaDiMisura, seriale)
        VALUES
            (1, 'TEMPERATURA', 0, '°C', 'SN-TEMP-001'),
            (2, 'TEMPERATURA', 0, '°C', 'SN-TEMP-002'),
            (3, 'PRESSIONE', 0, 'bar', 'SN-PRES-001'),
            (4, 'PRESSIONE', 0, 'bar', 'SN-PRES-002'),
            (5, 'VIBRAZIONE', 0, 'Hz', 'SN-VIBR-001'),
            (6, 'VIBRAZIONE', 0, 'Hz', 'SN-VIBR-002'),
            (7, 'CO2', 0, 'ppm', 'SN-CO2-001'),
            (8, 'CO2', 0, 'ppm', 'SN-CO2-002')
        ON CONFLICT (id) DO UPDATE SET unitaDiMisura = EXCLUDED.unitaDiMisura, seriale = EXCLUDED.seriale;

        PERFORM setval(pg_get_serial_sequence('SensoreBase', 'id'), GREATEST(8, COALESCE(MAX(id), 0)))
        FROM SensoreBase;
    END IF;
END $$;
