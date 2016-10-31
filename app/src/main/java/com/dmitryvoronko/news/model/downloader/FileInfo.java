package com.dmitryvoronko.news.model.downloader;

import lombok.Data;

/**
 * Created by Dmitry on 31/10/2016.
 */
@Data
public final class FileInfo
{
    private final String link;
    private final String fileName;

    private FileInfo(final String link, final String fileName)
    {
        this.link = link;
        this.fileName = fileName;
    }
}
