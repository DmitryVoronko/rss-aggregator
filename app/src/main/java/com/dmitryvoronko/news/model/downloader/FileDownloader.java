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

public final class FileDownloader
{
    private static final String TAG = "FILE_DOWNLOADER";

    private ContextWrapper contextWrapper;

    public FileDownloader(final ContextWrapper contextWrapper)
    {
        this.contextWrapper = contextWrapper;
    }

    public boolean downloadFile(FileInfo fileInfo)
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

                final boolean fileCreated = fileCreated(fileInfo.getFileName(), inputStream);

                return fileCreated;
            }

        } catch (MalformedURLException e)
        {
            Log.d(TAG, "downloadFile: Malformed URL Exception", e);
        } catch (IOException e)
        {
            Log.d(TAG, "downloadFile: IO Exception", e);
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

        } catch (FileNotFoundException e)
        {
            Log.d(TAG, "fileCreated: File Not Found Exception", e);
        } catch (IOException e)
        {
            Log.d(TAG, "fileCreated: IO Exception", e);
        } finally
        {
            closeWriter(writer);
            closeReader(reader);
        }
        return false;
    }

    private void closeReader(BufferedReader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            } catch (IOException e)
            {
                Log.d(TAG, "closeReader: IO Exception", e);
            }
        }
    }

    private void closeWriter(OutputStreamWriter writer)
    {
        if (writer != null)
        {
            try
            {
                writer.flush();
                writer.close();
            } catch (IOException e)
            {
                Log.d(TAG, "closeWriter: IO Exception", e);
            }
        }
    }

}
