package org.cyclopsgroup.kaufman.aws.dycfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

import com.amazonaws.services.s3.AmazonS3;

public class S3PropertiesFactoryBean
    implements FactoryBean<Properties>
{
    private final Properties props;

    public S3PropertiesFactoryBean( AmazonS3 s3, String bucketName,
                                    String objectKey )
        throws IOException

    {
        props = new Properties();
        InputStream in =
            s3.getObject( bucketName, objectKey ).getObjectContent();
        try
        {
            props.load( in );
        }
        finally
        {
            in.close();
        }
    }

    @Override
    public Properties getObject()
    {
        return props;
    }

    @Override
    public Class<Properties> getObjectType()
    {
        return Properties.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
