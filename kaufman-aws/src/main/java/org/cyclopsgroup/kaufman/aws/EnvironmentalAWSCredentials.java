package org.cyclopsgroup.kaufman.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * Credentials injected with environment variables
 */
public class EnvironmentalAWSCredentials
    implements AWSCredentials, AWSCredentialsProvider
{
    private final String accessKeyId;

    private final String secretKey;

    public EnvironmentalAWSCredentials( String idVariableName, String secretVariableName )
    {
        this.accessKeyId = System.getenv( idVariableName );
        this.secretKey = System.getenv( secretVariableName );

        if ( accessKeyId == null || secretKey == null )
        {
            throw new IllegalStateException( "Envornment variables " + idVariableName + " and " + secretVariableName
                + " are expected" );
        }
    }

    public EnvironmentalAWSCredentials()
    {
        this( "AWS_ACCESS_KEY_ID", "AWS_SECRET_KEY" );
    }

    /**
     * @inheritDoc
     */
    @Override
    public AWSCredentials getCredentials()
    {
        return this;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void refresh()
    {
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getAWSAccessKeyId()
    {
        return accessKeyId;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getAWSSecretKey()
    {
        return secretKey;
    }
}
