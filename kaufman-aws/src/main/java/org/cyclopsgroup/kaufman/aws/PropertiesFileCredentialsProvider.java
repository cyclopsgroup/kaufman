package org.cyclopsgroup.kaufman.aws;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;

/**
 * This implementation of {@link AWSCredentialsProvider} allows to create an
 * instance from an invalid properties file. Only if the properties file is
 * still invalid at the time it's used, the call throws exception.
 */
public class PropertiesFileCredentialsProvider
    implements AWSCredentialsProvider
{
    private final File propertiesFile;

    private AWSCredentials credentials;

    public PropertiesFileCredentialsProvider( File propertiesFile )
    {
        this.propertiesFile = propertiesFile;
    }

    public PropertiesFileCredentialsProvider( String propertiesFilePath )
    {
        this( new File( propertiesFilePath ) );
    }

    /**
     * @inheritDoc
     */
    @Override
    public synchronized AWSCredentials getCredentials()
    {
        if ( credentials == null )
        {
            refresh();
        }
        return credentials;
    }

    /**
     * @inheritDoc
     */
    @Override
    public synchronized void refresh()
    {
        try
        {
            credentials = new PropertiesCredentials( propertiesFile );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "Given properties file "
                + propertiesFile + " can't result a proper AWSCredentials: "
                + e.getMessage(), e );
        }
    }
}
