package org.cyclopsgroup.kaufman.aws;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;

public class ChainedCredentialsProvider
    implements AWSCredentialsProvider
{
    private static final Log LOG =
        LogFactory.getLog( ChainedCredentialsProvider.class );

    private final AWSCredentialsProvider instance;

    public ChainedCredentialsProvider( List<Object> chain )
        throws IOException
    {
        for ( Object initObject : chain )
        {
            if ( initObject instanceof AWSCredentialsProvider )
            {
                LOG.info( "Returning crendentials provider " + initObject );
                instance = (AWSCredentialsProvider) initObject;
                return;
            }
            if ( initObject instanceof AWSCredentials )
            {
                LOG.info( "Returning credentials " + initObject );
                instance =
                    new StaticCredentialsProvider( (AWSCredentials) initObject );
                return;
            }
            if ( initObject instanceof File || initObject instanceof String )
            {
                File file;
                if ( initObject instanceof String )
                {
                    file =
                        new File(
                                  ExpressionUtils.populate( ( (String) initObject ).trim() ) );
                }
                else
                {
                    file = (File) initObject;
                }
                if ( !file.exists() )
                {
                    LOG.info( "Skip file " + file + " since it does't exist" );
                    continue;
                }
                LOG.info( "Return credentials in file " + file );
                instance =
                    new StaticCredentialsProvider(
                                                   new PropertiesCredentials(
                                                                              file ) );
                return;
            }
        }
        throw new IllegalStateException(
                                         "Can't create AWSCredentialsProvider from chain "
                                             + chain );
    }

    @Override
    public AWSCredentials getCredentials()
    {
        return instance.getCredentials();
    }

    final AWSCredentialsProvider getInstance()
    {
        return instance;
    }

    @Override
    public void refresh()
    {
        instance.refresh();
    }
}
