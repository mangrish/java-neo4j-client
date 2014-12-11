package io.innerloop.neo4j.client;

import java.util.Arrays;
import java.util.List;

/**
 * Created by markangrish on 29/10/2014.
 */
public class RowSet
{
    private final String[] columnNames;

    private final List<Object[]> rows;

    private final int totalRows;

    private int currentRow;

    public RowSet(String[] columnNames, List<Object[]> rows)
    {
        this.columnNames = columnNames;
        this.rows = rows;
        this.totalRows = this.rows.size();
        this.currentRow = 0;
    }

    public boolean next()
    {
        return totalRows > ++currentRow;
    }

    public String[] getColumnNames()
    {
        return Arrays.copyOf(columnNames, columnNames.length);
    }

    public String getString(int column)
    {
        return (String) getRow(currentRow)[column];
    }

    public int getInt(int column)
    {
        return (int) getRow(currentRow)[column];
    }

    public long getLong(int column)
    {
        return (long) getRow(currentRow)[column];
    }

    public float getFloat(int column)
    {
        return (float) getRow(currentRow)[column];
    }

    public double getDouble(int column)
    {
        return (double) getRow(currentRow)[column];
    }

    public boolean getBoolean(int column)
    {
        return (boolean) getRow(currentRow)[column];
    }

    public short getShort(int column)
    {
        return (short) getRow(currentRow)[column];
    }

    public byte getByte(int column)
    {
        return (byte) getRow(currentRow)[column];
    }

    public char getChar(int column)
    {
        return (char) getRow(currentRow)[column];
    }

    private Object[] getRow(int index)
    {
        return rows.get(index);
    }
}
