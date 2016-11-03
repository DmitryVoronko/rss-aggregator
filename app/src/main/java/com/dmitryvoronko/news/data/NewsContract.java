package com.dmitryvoronko.news.data;


/**
 * Created by Dmitry on 22/10/2016.
 */

final class NewsContract
{
    static final String DATABASE_CREATE = ChannelsTable.CREATE_TABLE + ArticlesTable.CREATE_TABLE;
    static final String DATABASE_DELETE = ChannelsTable.DELETE_TABLE + ArticlesTable.DELETE_TABLE;

    private static final String CREATE_TABLE = "create table";
    private static final String INTEGER = " integer";
    private static final String PRIMARY_KEY = " primary key";
    private static final String AUTOINCREMENT = " autoincrement";
    private static final String NOT_NULL = " not null";
    private static final String UNIQUE = " unique";
    private static final String TEXT = " text";
    private static final String FOREIGN_KEY = "foreign key";
    private static final String REFERENCES = "references";
    private static final String COMMA_SEP = ", ";
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";


    interface BaseTable
            extends android.provider.BaseColumns
    {
        String _TITLE = "_title";
        String _LINK = "_url";
        String _DESCRIPTION = "_description";
        String _PUB_DATE = "_pub_date";
        String _STATE = "_state";
    }

    final class ChannelsTable
            implements BaseTable
    {
        static final String _TABLE_NAME = "ChannelsTable";

        private static final String CREATE_TABLE = NewsContract.CREATE_TABLE + " " +
                _TABLE_NAME + " (" + _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                _TITLE + TEXT + NOT_NULL + COMMA_SEP +
                _DESCRIPTION + " " + TEXT + " " + NOT_NULL + COMMA_SEP +
                _LINK + TEXT + NOT_NULL + UNIQUE + COMMA_SEP +
                _PUB_DATE + TEXT + NOT_NULL + COMMA_SEP +
                _STATE + TEXT + NOT_NULL + ");";

        private static final String DELETE_TABLE = DROP_TABLE_IF_EXISTS + _TABLE_NAME;
    }

    final class ArticlesTable
            implements BaseTable
    {
        static final String _TABLE_NAME = "ArticlesTable";

        static final String _CHANNEL_ID = "_channel_id";
        static final String _PUB_DATE = "_pub_date";


        private static final String CREATE_TABLE = NewsContract.CREATE_TABLE + " " +
                _TABLE_NAME + " (" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                _CHANNEL_ID + INTEGER + NOT_NULL + COMMA_SEP +
                _TITLE + TEXT + NOT_NULL + COMMA_SEP +
                _DESCRIPTION + TEXT + NOT_NULL + COMMA_SEP +
                _LINK + TEXT + NOT_NULL + UNIQUE + COMMA_SEP +
                _PUB_DATE + TEXT + NOT_NULL + COMMA_SEP +
                _STATE + TEXT + NOT_NULL + COMMA_SEP +
                FOREIGN_KEY + "(" + _CHANNEL_ID + ") " + REFERENCES + " " +
                ChannelsTable._TABLE_NAME + "(" + ChannelsTable._ID + ");";

        private static final String DELETE_TABLE = DROP_TABLE_IF_EXISTS + _TABLE_NAME;
    }
}
