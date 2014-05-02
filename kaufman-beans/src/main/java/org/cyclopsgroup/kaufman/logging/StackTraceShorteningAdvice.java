package org.cyclopsgroup.kaufman.logging;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

public class StackTraceShorteningAdvice
{
    private static final Log LOG =
        LogFactory.getLog( StackTraceShorteningAdvice.class );

    public Object execute( ProceedingJoinPoint point )
        throws Throwable
    {
        try
        {
            return point.proceed();
        }
        catch ( Throwable e )
        {
            String trace = RandomStringUtils.randomAlphabetic( 8 );
            LOG.error( "Invocation of "
                           + point
                           + " failed: "
                           + e.getMessage()
                           + ". To keep stack trace short, a smaller RuntimeException with trace ["
                           + trace + "] is thrown instead.", e );
            throw new RuntimeException( "[" + trace + "], invocation " + point
                + " failed:" + e.getMessage() );
        }
    }
}
