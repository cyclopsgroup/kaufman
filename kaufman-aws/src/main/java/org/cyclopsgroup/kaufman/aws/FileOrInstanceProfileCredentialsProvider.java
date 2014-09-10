package org.cyclopsgroup.kaufman.aws;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;

public class FileOrInstanceProfileCredentialsProvider
    extends ChainedCredentialsProvider
{
    public FileOrInstanceProfileCredentialsProvider( File file )
        throws IOException
    {
        super( Arrays.asList( file, new InstanceProfileCredentialsProvider() ) );
    }

    public FileOrInstanceProfileCredentialsProvider( String file )
        throws IOException
    {
        super( Arrays.asList( file, new InstanceProfileCredentialsProvider() ) );
    }
}
