package org.cyclopsgroup.kaufman.logging;

import java.util.Arrays;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * AspectJ advice that logs input and output of method invocation
 */
public class InvocationLoggingAdvice
{
    public Object execute( ProceedingJoinPoint point )
        throws Throwable
    {
        String trace = "[" + RandomStringUtils.randomAlphabetic( 8 ) + "]";
        Log log = LogFactory.getLog( point.getSignature().getDeclaringType() );
        log.info( trace + " Invoking " + point.getSignature().getName() + "("
            + Arrays.toString( point.getArgs() ) + ") against "
            + point.getTarget() );
        long start = System.currentTimeMillis();
        boolean successful = false;
        Object result = null;
        try
        {
            result = point.proceed();
            successful = true;
            return result;
        }
        finally
        {
            long elapsed = System.currentTimeMillis() - start;
            if ( successful )
            {
                log.info( trace + " Invocation of "
                    + point.getSignature().getName()
                    + " succeeded and returned " + result + " after " + elapsed
                    + "ms" );
            }
            else
            {
                log.error( trace + " Invocation of "
                    + point.getSignature().getName() + " failed after "
                    + elapsed + "ms" );
            }
        }
    }
}
