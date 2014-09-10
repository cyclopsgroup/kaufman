package org.cyclopsgroup.kaufman.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;

public class ChainedCredentialsProviderTest
{
    @Test
    public void testWithFallback()
        throws IOException
    {
        ChainedCredentialsProvider b =
            new ChainedCredentialsProvider(
                                            Arrays.asList( "no/where",
                                                           new InstanceProfileCredentialsProvider() ) );
        assertTrue( b.getInstance() instanceof InstanceProfileCredentialsProvider );
    }

    @Test
    public void testWithValidFile()
        throws IOException
    {
        ChainedCredentialsProvider b =
            new ChainedCredentialsProvider(
                                            Arrays.asList( "src/test/test-creds.properties",
                                                           new InstanceProfileCredentialsProvider() ) );
        AWSCredentials creds = b.getCredentials();
        assertEquals( "a", creds.getAWSAccessKeyId() );
        assertEquals( "b", creds.getAWSSecretKey() );
    }
}
