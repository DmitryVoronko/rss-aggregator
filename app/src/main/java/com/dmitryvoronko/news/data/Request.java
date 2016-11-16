package com.dmitryvoronko.news.data;

import android.database.sqlite.SQLiteDatabase;

/**
 *
 * Created by Dmitry on 04/11/2016.
 */

interface Request<T>
{
    T executed(final SQLiteDatabase sqLiteDatabase);
}
