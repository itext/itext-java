/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.source;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.io.util.StreamUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Factory to create {@link IRandomAccessSource} objects based on various types of sources
 */
public final class RandomAccessSourceFactory {

    /**
     * The default value for the forceRead flag
     */
    private static boolean forceReadDefaultValue = false;

    /**
     * Whether the full content of the source should be read into memory at construction
     */
    private boolean forceRead = forceReadDefaultValue;

    /**
     * Whether {@link java.io.RandomAccessFile} should be used instead of a {@link java.nio.channels.FileChannel}, where applicable
     */
    private boolean usePlainRandomAccess = false;

    /**
     * Whether the underlying file should have a RW lock on it or just an R lock
     */
    private boolean exclusivelyLockFile = false;

    /**
     * Creates a factory that will give preference to accessing the underling data source using memory mapped files
     */
    public RandomAccessSourceFactory() {
    }

    /**
     * Determines the default value for the forceRead flag
     * @param forceRead true if by default the full content will be read, false otherwise
     */
    public static void setForceReadDefaultValue(boolean forceRead) {
        forceReadDefaultValue = forceRead;
    }

    /**
     * Determines whether the full content of the source will be read into memory
     * @param forceRead true if the full content will be read, false otherwise
     * @return this object (this allows chaining of method calls)
     */
    public RandomAccessSourceFactory setForceRead(boolean forceRead){
        this.forceRead = forceRead;
        return this;
    }

    /**
     * Determines whether {@link java.io.RandomAccessFile} should be used as the primary data access mechanism
     * @param usePlainRandomAccess whether {@link java.io.RandomAccessFile} should be used as the primary data access mechanism
     * @return this object (this allows chaining of method calls)
     */
    public RandomAccessSourceFactory setUsePlainRandomAccess(boolean usePlainRandomAccess){
        this.usePlainRandomAccess = usePlainRandomAccess;
        return this;
    }

    public RandomAccessSourceFactory setExclusivelyLockFile(boolean exclusivelyLockFile){
        this.exclusivelyLockFile = exclusivelyLockFile;
        return this;
    }

    /**
     * Creates a {@link IRandomAccessSource} based on a byte array
     * @param data the byte array
     * @return the newly created {@link IRandomAccessSource}
     */
    public IRandomAccessSource createSource(byte[] data){
        return new ArrayRandomAccessSource(data);
    }

    public IRandomAccessSource createSource(RandomAccessFile raf) throws java.io.IOException {
        return new RAFRandomAccessSource(raf);
    }

    /**
     * Creates a {@link IRandomAccessSource} based on a URL.  The data available at the URL is read into memory and used
     * as the source for the {@link IRandomAccessSource}
     * @param url the url to read from
     * @return the newly created {@link IRandomAccessSource}
     * @throws java.io.IOException in case of any I/O error.
     */
    public IRandomAccessSource createSource(URL url) throws java.io.IOException{
        InputStream stream = url.openStream();
        try {
            return createSource(stream);
        }
        finally {
            try {
                stream.close();
            } catch(java.io.IOException ignored) { }
        }
    }

    /**
     * Creates or extracts a {@link IRandomAccessSource} based on an {@link InputStream}.
     *
     * <p>
     * If the InputStream is an instance of {@link RASInputStream} then extracts the source from it.
     * Otherwise The full content of the InputStream is read into memory and used
     * as the source for the {@link IRandomAccessSource}
     *
     * @param inputStream the stream to read from
     *
     * @return the newly created or extracted {@link IRandomAccessSource}
     *
     * @throws java.io.IOException in case of any I/O error.
     */
    public IRandomAccessSource extractOrCreateSource(InputStream inputStream) throws java.io.IOException {
        if (inputStream instanceof RASInputStream) {
            return ((RASInputStream) inputStream).getSource();
        }
        return createSource(StreamUtil.inputStreamToArray(inputStream));
    }

    /**
     * Creates a {@link IRandomAccessSource} based on an {@link InputStream}.
     *
     * <p>
     * The full content of the InputStream is read into memory and used
     * as the source for the {@link IRandomAccessSource}
     *
     * @param inputStream the stream to read from
     *
     * @return the newly created {@link IRandomAccessSource}
     *
     * @throws java.io.IOException in case of any I/O error.
     */
    public IRandomAccessSource createSource(InputStream inputStream) throws java.io.IOException{
        return createSource(StreamUtil.inputStreamToArray(inputStream));
    }

    /**
     * Creates a {@link IRandomAccessSource} based on a filename string.
     * If the filename describes a URL, a URL based source is created
     * If the filename describes a file on disk, the contents may be read into memory (if {@code forceRead} is true),
     * opened using memory mapped file channel (if usePlainRandomAccess is false), or
     * opened using {@link RandomAccessFile} access (if usePlainRandomAccess is true)
     * This call will automatically fail over to using {@link RandomAccessFile} if the memory map operation fails
     * @param filename the name of the file or resource to create the {@link IRandomAccessSource} for
     * @return the newly created {@link IRandomAccessSource}
     * @throws java.io.IOException in case of any I/O error
     */
    public IRandomAccessSource createBestSource(String filename) throws java.io.IOException{
        File file = new File(filename);
        if (!file.canRead()) {
            if (filename.startsWith("file:/")
                    || filename.startsWith("http://")
                    || filename.startsWith("https://")
                    || filename.startsWith("jar:")
                    || filename.startsWith("wsjar:")
                    || filename.startsWith("vfszip:")) {
                return createSource(new URL(filename));
            } else {
                return createByReadingToMemory(filename);
            }
        }

        if (forceRead){
            return createByReadingToMemory(FileUtil.getInputStreamForFile(filename));
        }

        String openMode = exclusivelyLockFile ? "rw" : "r";

        RandomAccessFile raf = new RandomAccessFile(file, openMode);
        if (exclusivelyLockFile){
            raf.getChannel().lock();
        }

        if (usePlainRandomAccess){
            return new RAFRandomAccessSource(raf);
        }

        try{

            // files with zero length can't be mapped and will throw an IllegalArgumentException.
            // Just open using a simple RAF source.
            if (raf.length() <= 0)
                return new RAFRandomAccessSource(raf);

            try {

                // ownership of the RAF passes to whatever source is created by createBestSource.
                return createBestSource(raf.getChannel());
            } catch (java.io.IOException e){
                if (exceptionIsMapFailureException(e)) {
                    return new RAFRandomAccessSource(raf);
                }
                throw e;
            }
        } catch (Exception e) {
            // If RAFRandomAccessSource constructor or createBestSource throws, then we must close the RAF we created.

            try {
                raf.close();
            } catch (java.io.IOException ignore){}
            throw e;
        }
    }

    /**
     * Creates a {@link IRandomAccessSource} based on memory mapping a file channel.
     * Unless you are explicitly working with a {@code FileChannel} already, it is better to use
     * {@link RandomAccessSourceFactory#createBestSource(String)}.
     * If the file is large, it will be opened using a paging strategy.
     * @param channel the name of the file or resource to create the {@link IRandomAccessSource} for
     * @return the newly created {@link IRandomAccessSource}
     * @throws java.io.IOException in case of any I/O error
     */
    public IRandomAccessSource createBestSource(FileChannel channel) throws java.io.IOException {

        // if less than the fully mapped usage of PagedFileChannelRandomAccessSource,
        // just map the whole thing and be done with it
        if (channel.size() <= PagedChannelRandomAccessSource.DEFAULT_TOTAL_BUFSIZE){
            return new GetBufferedRandomAccessSource(new FileChannelRandomAccessSource(channel));
        } else {
            return new GetBufferedRandomAccessSource(new PagedChannelRandomAccessSource(channel));
        }
    }

    public IRandomAccessSource createRanged(IRandomAccessSource source, long[] ranges) throws java.io.IOException {
        IRandomAccessSource[] sources = new IRandomAccessSource[ranges.length/2];
        for(int i = 0; i < ranges.length; i+=2){
            sources[i/2] = new WindowRandomAccessSource(source, ranges[i], ranges[i+1]);
        }
        return new GroupedRandomAccessSource(sources);
    }

    /**
     * Creates a new {@link IRandomAccessSource} by reading the specified file/resource into memory
     * @param filename the name of the resource to read
     * @return the newly created {@link IRandomAccessSource}
     * @throws java.io.IOException if reading the underling file or stream fails
     */
    private IRandomAccessSource createByReadingToMemory(String filename) throws java.io.IOException {
        InputStream stream = ResourceUtil.getResourceStream(filename);
        if (stream == null) {
            throw new java.io.IOException(MessageFormatUtil.format(IoExceptionMessageConstant.NOT_FOUND_AS_FILE_OR_RESOURCE, filename));
        }
        return createByReadingToMemory(stream);
    }

    /**
     * Creates a new {@link IRandomAccessSource} by reading the specified file/resource into memory
     * @param stream the name of the resource to read
     * @return the newly created {@link IRandomAccessSource}
     * @throws java.io.IOException if reading the underling file or stream fails
     */
    private IRandomAccessSource createByReadingToMemory(InputStream stream) throws java.io.IOException {
        try {
            return new ArrayRandomAccessSource(StreamUtil.inputStreamToArray(stream));
        }
        finally {
            try {
                stream.close();
            } catch (java.io.IOException ignored) { }
        }
    }

    /**
     * Utility method that determines whether a given java.io.IOException is the result
     * of a failure to map a memory mapped file.  It would be better if the runtime
     * provided a special exception for this case, but it doesn't, so we have to rely
     * on parsing the exception message.
     * @param e the exception to check
     * @return true if the exception was the result of a failure to map a memory mapped file
     */
    private static boolean exceptionIsMapFailureException(java.io.IOException e){
        if (e.getMessage() != null && e.getMessage().contains("Map failed"))
            return true;
        return false;
    }
}
