package org.cyclopsgroup.kaufman.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;

public class ChainedCredentialsFactoryBeanTest
{
    @Test
    public void testWithFallback()
        throws IOException
    {
        ChainedCredentialsFactoryBean b =
            new ChainedCredentialsFactoryBean(
                                               Arrays.asList( "no/where",
                                                              new InstanceProfileCredentialsProvider() ) );
        assertTrue( b.getObject() instanceof InstanceProfileCredentialsProvider );
    }

    @Test
    public void testWithValidFile()
        throws IOException
    {
        ChainedCredentialsFactoryBean b =
            new ChainedCredentialsFactoryBean(
                                               Arrays.asList( "src/test/test-creds.properties",
                                                              new InstanceProfileCredentialsProvider() ) );
        AWSCredentials creds = b.getObject().getCredentials();
        assertEquals( "a", creds.getAWSAccessKeyId() );
        assertEquals( "b", creds.getAWSSecretKey() );
    }
}
