package util;

import play.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by jiangecho on 15/5/3.
 */
public class DBUtil {
    private static final String QUERY_LAST_ID = "SELECT LAST_INSERT_ID()";
    private static final String QUERY_MAX_ID = "SELECT MAX(ID) FROM ";

    private static final String QUERY_LAST_RECORD = "SELECT * FROM %s ORDER BY %s DESC LIMIT %s";
    private static final String QUERY_LAST_RECORD_WITH_WHERE = "SELECT * FROM %s WHERE %s ORDER BY %s DESC LIMIT %s";
    private static final String QUERY_LESS_LAST_RECORD = "SELECT * FROM %s WHERE %s < %s ORDER BY %s DESC LIMIT %s";
    private static final String QUERY_LESS_LAST_RECORD_WITH_WHERE = "SELECT * FROM %s WHERE %s < %s and %s ORDER BY %s DESC LIMIT %s";

    private static final String QUERY_BY = "SELECT * FROM %s WHERE %s = '%s'";
    private static final String INCREASE_COLUMN_VALUE_BY_ONE = "UPDATE %s SET %s = %s + 1 WHERE id = %s";
    private static final String DECREASE_COLUMN_VALUE_BY_ONE = "UPDATE %s SET %s = %s - 1 WHERE id = %s";

    public static long queryLastId(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery(QUERY_LAST_ID);
        long lastId = -1;
        if (resultSet.next()) {
            lastId = resultSet.getLong("LAST_INSERT_ID()");
        }
        resultSet.close();
        return lastId;
    }

    public static long queryMaxId(Statement statement, String table) throws SQLException {
        String sql = QUERY_MAX_ID + table;
        ResultSet resultSet = statement.executeQuery(sql);
        long maxId = -1;
        if (resultSet.next()) {
            maxId = resultSet.getLong("MAX(ID)");
        }
        resultSet.next();
        return maxId;

    }

    /**
     * @param statement
     * @param table
     * @param orderBy
     * @param limit
     * @return
     * @throws SQLException
     */
    public static ResultSet queryLastRecord(Statement statement, String table, String orderBy, int limit) throws SQLException {
        String sql = String.format(QUERY_LAST_RECORD, table, orderBy, limit);
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public static ResultSet queryLastRecord(Statement statement, String table, String where, String orderBy, int limit) throws SQLException {
        String sql = String.format(QUERY_LAST_RECORD_WITH_WHERE, table, where, orderBy, limit);
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    /**
     * orderBy only support the following types:
     * int, long and so on, which can compare with > or <
     *
     * @param statement
     * @param table
     * @param orderBy
     * @param startValue
     * @param limit
     * @return
     * @throws SQLException
     */
    public static ResultSet queryLessLastRecord(Statement statement, String table, String orderBy, String startValue, int limit) throws SQLException {
        String sql = String.format(QUERY_LESS_LAST_RECORD, table, orderBy, startValue, orderBy, limit);
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public static ResultSet queryLessLastRecord(Statement statement, String table, String where, String orderBy, String startValue, int limit) throws SQLException {
        String sql = String.format(QUERY_LESS_LAST_RECORD_WITH_WHERE, table, orderBy, startValue, where, orderBy, limit);
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    /**
     * only support columns which can compare with =
     *
     * @param statement
     * @param table
     * @param columnName
     * @param columnValue
     * @return
     */
    public static ResultSet queryBy(Statement statement, String table, String columnName, String columnValue) throws SQLException {
        String sql = String.format(QUERY_BY, table, columnName, columnValue);
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    /**
     * 把某个字段加1
     *
     * @param statement
     * @param table
     * @param columnName
     * @param id
     * @throws SQLException
     */
    public static void increaseOneById(Statement statement, String table, String columnName, long id) throws SQLException {
        String sql = String.format(INCREASE_COLUMN_VALUE_BY_ONE, table, columnName, columnName, id);
        statement.execute(sql);
    }

    public static void decreaseOneById(Statement statement, String table, String columnName, long id) throws SQLException {
        String sql = String.format(DECREASE_COLUMN_VALUE_BY_ONE, table, columnName, columnName, id);
        statement.execute(sql);
    }

    public static void updateColumnById(Statement statement, String table, String columnName, String columnNewValue, long id) {
    }

    public static long insert(String sql) {
        Connection connection = DB.getConnection();
        Statement statement = null;
        long id = -1;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
            id = queryLastId(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {

            }
        }

        return id;
    }

    /**
     * do not close statement
     *
     * @param statement
     * @param sql
     * @return
     */
    public static long insert(Statement statement, String sql) {
        if (statement == null || StringUtil.isEmputy(sql)) {
            return -1;
        }
        long id = -1;
        try {
            statement.execute(sql);
            id = queryLastId(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static int bulkInsert(List<String> insertSql) {
        if (insertSql == null || insertSql.size() == 0) {
            return -1;
        }

        int count = -1;
        Connection connection = DB.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (String sql : insertSql) {
                statement.addBatch(sql);
            }
            int[] result = statement.executeBatch();
            count = result == null ? 0 : result.length;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    public static ResultSet query(Statement statement, String sql) throws SQLException {
        return statement.executeQuery(sql);
    }
}
