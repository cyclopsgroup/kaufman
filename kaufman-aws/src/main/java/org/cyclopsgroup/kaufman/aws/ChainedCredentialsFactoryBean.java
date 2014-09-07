package org.cyclopsgroup.kaufman.aws;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;

/**
 * A {@link FactoryBean} of {@link AWSCredentialsProvider} that creates bean
 * based on given list of objects. The objects can be instance of
 * {@link AWSCredentials}, {@link AWSCredentialsProvider}, in which case they
 * will be returned directly, or {@link File}, {@link String} in which case a
 * {@link PropertiesCredentials} will be returned ONLY IF the specified file
 * exists. If specified file does not exist, the factory bean will attempt to
 * create credentials from the next object in chain.
 */
public class ChainedCredentialsFactoryBean
    implements FactoryBean<AWSCredentialsProvider>
{
    private static final Log LOG =
        LogFactory.getLog( ChainedCredentialsFactoryBean.class );

    private final List<Object> chain;

    /**
     * @param chain List of objects to create credentials from
     */
    public ChainedCredentialsFactoryBean( List<? extends Object> chain )
    {
        this.chain = Collections.unmodifiableList( chain );
    }

    /**
     * @inheritDoc
     */
    @Override
    public AWSCredentialsProvider getObject()
        throws IOException
    {
        for ( Object initObject : chain )
        {
            if ( initObject instanceof AWSCredentialsProvider )
            {
                LOG.info( "Returning crendentials provider " + initObject );
                return (AWSCredentialsProvider) initObject;
            }
            if ( initObject instanceof AWSCredentials )
            {
                LOG.info( "Returning credentials " + initObject );
                return new StaticCredentialsProvider(
                                                      (AWSCredentials) initObject );
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
                return new StaticCredentialsProvider(
                                                      new PropertiesCredentials(
                                                                                 file ) );
            }
        }
        throw new IllegalStateException(
                                         "Can't create AWSCredentialsProvider from chain "
                                             + chain );
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<AWSCredentialsProvider> getObjectType()
    {
        return AWSCredentialsProvider.class;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
