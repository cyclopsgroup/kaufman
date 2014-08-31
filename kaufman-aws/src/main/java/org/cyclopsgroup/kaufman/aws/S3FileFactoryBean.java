package org.cyclopsgroup.kaufman.aws;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * A spring bean that downloads S3 object into a local file, return the file and
 * delete it when Spring context disposes.
 */
public class S3FileFactoryBean
    implements FactoryBean<File>, DisposableBean
{
    private static final Log LOG = LogFactory.getLog( S3FileFactoryBean.class );

    private final String bucketName, objectKey;

    private File localFile;

    private final AmazonS3 s3Client;

    public S3FileFactoryBean( AmazonS3 s3Client, String bucketName,
                              String objectKey )
    {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public S3FileFactoryBean( AWSCredentials creds, String bucketName,
                              String objectKey )
    {
        this( new StaticCredentialsProvider( creds ), bucketName, objectKey );
    }

    public S3FileFactoryBean( AWSCredentialsProvider creds, String bucketName,
                              String objectKey )
    {
        this( new AmazonS3Client( creds ), bucketName, objectKey );
    }

    public S3FileFactoryBean( String bucketName, String objectKey )
    {
        this( new InstanceProfileCredentialsProvider(), bucketName, objectKey );
    }

    @Override
    public void destroy()
    {
        if ( localFile.exists() )
        {
            localFile.delete();
            LOG.info( "Local file " + localFile + " is deleted" );
        }
    }

    @Override
    public File getObject()
        throws IOException
    {
        if ( localFile == null )
        {
            localFile =
                File.createTempFile( S3FileFactoryBean.class.getSimpleName(),
                                     "" );
            LOG.info( "Using default local file " + localFile );
        }
        LOG.info( String.format( "Getting object %s:%s into local file %s",
                                 bucketName, objectKey,
                                 localFile.getAbsolutePath() ) );
        ObjectMetadata result =
            s3Client.getObject( new GetObjectRequest( bucketName, objectKey ),
                                localFile );
        LOG.info( "Call to S3 succeeded and returned " + result );
        return localFile;
    }

    @Override
    public Class<File> getObjectType()
    {
        return File.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setLocalFile( File localFile )
    {
        this.localFile = localFile;
    }
}
