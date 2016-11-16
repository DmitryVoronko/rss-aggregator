package com.dmitryvoronko.news.util.downloader;

import com.dmitryvoronko.news.model.data.Channel;

import lombok.Data;
import lombok.NonNull;

/**
 *
 * Created by Dmitry on 31/10/2016.
 */
@Data
public final class FileInfo
{
    private final String link;
    private final String fileName;

    private FileInfo(@NonNull final String link, @NonNull final String fileName)
    {
        this.link = link;
        this.fileName = fileName;
    }

    @NonNull public static FileInfo create(@NonNull final Channel channel)
    {
        final String link = channel.getLink();
        final String fileName = channel.getId() + channel.getTitle() + ".xml";

        return new FileInfo(link, fileName);
    }
}
