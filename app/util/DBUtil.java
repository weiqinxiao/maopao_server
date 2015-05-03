package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jiangecho on 15/5/3.
 */
public class DBUtil {
    private static final String QUERY_LAST_ID = "SELECT LAST_INSERT_ID()";
    private static final String QUERY_MAX_ID = "SELECT MAX(ID) FROM ";

    private static final String QUERY_LAST_RECORD = "SELECT * FROM %s ORDER BY %s DESC LIMIT %s";
    private static final String QUERY_LESS_LAST_RECORD = "SELECT * FROM %s WHERE %s < %s ORDER BY %s DESC LIMIT %s";

    private static final String QUERY_BY = "SELECT * FROM %s WHERE %s = '%s'";
    private static final String INCREASE_COLUMN_VALUE_BY_ONE = "UPDATE %s SET %s = %s + 1 WHERE id = %s";

    public static long queryLastId(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery(QUERY_LAST_ID);
        resultSet.next();
        return resultSet.getLong("LAST_INSERT_ID()");
    }

    public static long queryMaxId(Statement statement, String table) throws SQLException {
        String sql = QUERY_MAX_ID + table;
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        return resultSet.getLong("MAX(ID)");

    }

    /**
     *
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

    public static void increaseOneById(Statement statement, String table, String columnName, long id) throws SQLException {
        String sql = String.format(INCREASE_COLUMN_VALUE_BY_ONE, table, columnName, columnName, id);
        statement.execute(sql);
    }

    public static void updateColumnById(Statement statement, String table, String columnName, String columnNewValue, long id){
    }
}
