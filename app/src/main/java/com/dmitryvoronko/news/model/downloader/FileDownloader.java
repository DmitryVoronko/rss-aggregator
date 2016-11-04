package com.dmitryvoronko.news.model.downloader;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dmitry on 31/10/2016.
 */

final class FileDownloader
{
    private static final String TAG = "FILE_DOWNLOADER";

    private final ContextWrapper contextWrapper;

    public FileDownloader(final ContextWrapper contextWrapper)
    {
        this.contextWrapper = contextWrapper;
    }

    public boolean downloadFile(final FileInfo fileInfo)
    {
        try
        {
            final URL url = new URL(fileInfo.getLink());
            final URLConnection connection = url.openConnection();
            final HttpURLConnection httpConnection = (HttpURLConnection) connection;

            final int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                final InputStream inputStream = httpConnection.getInputStream();

                return fileCreated(fileInfo.getFileName(), inputStream);
            }

        } catch (final MalformedURLException e)
        {
            Log.d(TAG, "downloadFile: Malformed URL Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "downloadFile: TOTAL_ERROR Exception", e);
        }

        return false;
    }

    private boolean fileCreated(final String fileName,
                                final InputStream inputStream)
    {
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try
        {
            final FileOutputStream fileOutputStream =
                    contextWrapper.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(fileOutputStream);

            final InputStreamReader streamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(streamReader);

            String line;
            while ((line = reader.readLine()) != null)
            {
                writer.write(line);
            }

            return true;

        } catch (final FileNotFoundException e)
        {
            Log.d(TAG, "fileCreated: File Not Found Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "fileCreated: TOTAL_ERROR Exception", e);
        } finally
        {
            closeWriter(writer);
            closeReader(reader);
        }
        return false;
    }

    private void closeWriter(OutputStreamWriter writer)
    {
        if (writer != null)
        {
            try
            {
                writer.flush();
                writer.close();
            } catch (final IOException e)
            {
                Log.d(TAG, "closeWriter: TOTAL_ERROR Exception", e);
            }
        }
    }

    private void closeReader(BufferedReader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            } catch (final IOException e)
            {
                Log.d(TAG, "closeReader: TOTAL_ERROR Exception", e);
            }
        }
    }

}
