package fr.keke142.botlogger.database;

public interface IRepository {
    String[] getTable();
    void registerPreparedStatements(ConnectionHandler connection);
}

