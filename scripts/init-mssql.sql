IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'terrakube')
BEGIN
    CREATE DATABASE terrakube;
    PRINT 'Database terrakube created.';
END
ELSE
BEGIN
    PRINT 'Database terrakube already exists.';
END
GO