package org.cyclopsgroup.kaufman;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.Validate;

public abstract class LocateableResource
{
    private static class FileResource
        extends LocateableResource
    {
        private final File file;

        private FileResource( File file )
        {
            Validate.notNull( file, "File can't be NULL" );
            this.file = file;
        }

        /**
         * @inheritDoc
         */
        @Override
        public LocateableResource locate( String relativePath )
        {
            String parentPath = file.getParentFile().getAbsolutePath();
            if ( !parentPath.endsWith( SystemUtils.PATH_SEPARATOR ) )
            {
                parentPath += SystemUtils.PATH_SEPARATOR;
            }
            return new FileResource( new File( parentPath + relativePath ) );
        }

        /**
         * @inheritDoc
         */
        @Override
        public InputStream openToRead()
            throws IOException
        {
            return new FileInputStream( file );
        }

        /**
         * @inheritDoc
         */
        @Override
        public String toString()
        {
            return "file://" + file;
        }
    }

    public static LocateableResource fromFile( File file )
    {
        return new FileResource( file );
    }

    public abstract LocateableResource locate( String relativePath );

    public abstract InputStream openToRead()
        throws IOException;
}
