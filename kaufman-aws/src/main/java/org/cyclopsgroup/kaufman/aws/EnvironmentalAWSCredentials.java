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
        // Depend on the version of Tomcat, it seems Amazon BeanStalk passes environment variable in different ways.
        // Such code captures environment variable no matter how BeanStalk passes it.
        this.accessKeyId = System.getProperty( idVariableName, System.getenv( idVariableName ) );
        this.secretKey = System.getProperty( secretVariableName, System.getenv( secretVariableName ) );

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
