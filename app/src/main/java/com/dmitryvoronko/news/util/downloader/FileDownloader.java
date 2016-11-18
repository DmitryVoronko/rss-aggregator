package com.dmitryvoronko.news.util.downloader;

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

import lombok.Cleanup;

/**
 *
 * Created by Dmitry on 31/10/2016.
 */

public final class FileDownloader
{
    private static final String TAG = "FILE_DOWNLOADER";

    private final ContextWrapper contextWrapper;

    public FileDownloader(final ContextWrapper contextWrapper)
    {
        this.contextWrapper = contextWrapper;
    }

    public void download(final FileInfo fileInfo)
    {
        try
        {
            final URL url = new URL(fileInfo.getLink());
            final URLConnection connection = url.openConnection();
            final HttpURLConnection httpConnection = (HttpURLConnection) connection;

            final int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                @Cleanup final InputStream inputStream = httpConnection.getInputStream();

                downloadFile(fileInfo.getFileName(), inputStream);
            }

        } catch (final MalformedURLException e)
        {
            Log.d(TAG, "download: Malformed URL Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "download: TOTAL_ERROR Exception", e);
        }

    }

    private void downloadFile(final String fileName,
                              final InputStream inputStream)
    {
        try
        {
            final FileOutputStream fileOutputStream =
                    contextWrapper.openFileOutput(fileName, Context.MODE_PRIVATE);
            @Cleanup final OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);

            final InputStreamReader streamReader = new InputStreamReader(inputStream);
            @Cleanup final BufferedReader reader = new BufferedReader(streamReader);

            String line;
            while ((line = reader.readLine()) != null)
            {
                writer.write(line);
            }
        } catch (final FileNotFoundException e)
        {
            Log.d(TAG, "downloadFile: File Not Found Exception", e);
        } catch (final IOException e)
        {
            Log.d(TAG, "downloadFile: TOTAL_ERROR Exception", e);
        }
    }

}
