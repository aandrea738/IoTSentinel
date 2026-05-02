DO $$
BEGIN
    IF to_regclass('public.sensorebase') IS NOT NULL THEN
        ALTER TABLE SensoreBase
            DROP CONSTRAINT IF EXISTS sensorebase_dtype_check;

        ALTER TABLE SensoreBase
            ADD CONSTRAINT sensorebase_dtype_check
            CHECK (DTYPE IN ('TEMPERATURA', 'PRESSIONE', 'VIBRAZIONE', 'CO2'));

        INSERT INTO SensoreBase (id, DTYPE, stato)
        VALUES
            (1, 'TEMPERATURA', 0),
            (2, 'TEMPERATURA', 0),
            (3, 'PRESSIONE', 0),
            (4, 'PRESSIONE', 0),
            (5, 'VIBRAZIONE', 0),
            (6, 'VIBRAZIONE', 0),
            (7, 'CO2', 0),
            (8, 'CO2', 0)
        ON CONFLICT (id) DO NOTHING;

        PERFORM setval(pg_get_serial_sequence('SensoreBase', 'id'), GREATEST(8, COALESCE(MAX(id), 0)))
        FROM SensoreBase;
    END IF;
END $$;
