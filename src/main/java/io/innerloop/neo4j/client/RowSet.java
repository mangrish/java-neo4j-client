package io.innerloop.neo4j.client;

import java.util.List;

/**
 * A RowSet is similar to a JDBC ResultSet only it is very stripped down.
 * <p>
 * This object will be returned when making a Row based statement.
 */
public class RowSet
{
    private final String[] columnNames;

    private final List<Object[]> rows;

    private final int totalRows;

    private int currentRow;

    /**
     * Creates a new RowSet with the specified column names and data.
     *
     * @param columnNames
     *         The names of each column in the order they appear in the row data.
     * @param rows
     *         The rows of data that correlate to the column names.
     */
    public RowSet(String[] columnNames, List<Object[]> rows)
    {
        this.columnNames = columnNames;
        this.rows = rows;
        this.totalRows = this.rows.size();
        this.currentRow = 0;
    }

    /**
     * Determines if there is another row in the result set.
     *
     * @return true, if there is another row in this row set.
     */
    public boolean hasNext()
    {
        return totalRows > currentRow;
    }

    /**
     * Determines if there is another row in the result set.
     *
     * @return true, if there is another row in this row set.
     */
    public Object[] next()
    {
        return rows.get(currentRow++);
    }

    /**
     * Retrieves the column names in this result.
     *
     * @return The names of the columns in the order they appear in the row data.
     */
    public String[] getColumnNames()
    {
        return columnNames;
    }

    /**
     * Retrieves the value for a column in the current row as a String.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a string in the specified column.
     */
    public String getString(int column)
    {
        return (String) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as an int.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as an int in the specified column.
     */
    public int getInt(int column)
    {
        return (int) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a long.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a long in the specified column.
     */
    public long getLong(int column)
    {
        return ((Number)getRow(currentRow)[column]).longValue();
    }

    /**
     * Retrieves the value for a column in the current row as a float.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a float in the specified column.
     */
    public float getFloat(int column)
    {
        return (float) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a double.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a double in the specified column.
     */
    public double getDouble(int column)
    {
        return (double) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a boolean.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a boolean in the specified column.
     */
    public boolean getBoolean(int column)
    {
        return (boolean) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a short.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a short in the specified column.
     */
    public short getShort(int column)
    {
        return (short) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a byte.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a byte in the specified column.
     */
    public byte getByte(int column)
    {
        return (byte) getRow(currentRow)[column];
    }

    /**
     * Retrieves the value for a column in the current row as a char.
     *
     * @param column The column number to retrieve the value from.
     *
     * @return The value as a char in the specified column.
     */
    public char getChar(int column)
    {
        return (char) getRow(currentRow)[column];
    }

    private Object[] getRow(int index)
    {
        return rows.get(index);
    }
}
