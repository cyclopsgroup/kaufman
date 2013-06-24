package org.cyclopsgroup.kaufman.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InvocationLoggingDecorator
{
    private static class Handler<T>
        implements InvocationHandler
    {
        private final T target;

        private final Class<T> interfaceType;

        private Handler( Class<T> interfaceType, T target )
        {
            this.interfaceType = interfaceType;
            this.target = target;
        }

        /**
         * @inheritDoc
         */
        @Override
        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            if ( method.getDeclaringClass() == Object.class )
            {
                return method.invoke( target, args );
            }

            String trace = "[" + RandomStringUtils.randomAlphabetic( 8 ) + "]";
            Log log = LogFactory.getLog( interfaceType );
            log.info( trace + " Invoking " + method.getName() + "("
                + Arrays.toString( args ) + ") against " + target );
            long start = System.currentTimeMillis();
            boolean successful = false;
            Object result = null;
            try
            {
                result = method.invoke( target, args );
                successful = true;
                return result;
            }
            catch ( Throwable e )
            {
                log.error( "Invocation failed: " + e.getMessage(), e );
                if ( e instanceof InvocationTargetException
                    && ( (InvocationTargetException) e ).getCause() != null )
                {
                    throw e.getCause();
                }
                else
                {
                    throw e;
                }
            }
            finally
            {
                long elapsed = System.currentTimeMillis() - start;
                if ( successful )
                {
                    log.info( trace + " Invocation of " + method.getName()
                        + " succeeded and returned " + result + " after "
                        + elapsed + "ms" );
                }
                else
                {
                    log.error( trace + " Invocation of " + method.getName()
                        + " failed after " + elapsed + "ms" );
                }
            }
        }
    }

    public static <T> T decorate( Class<T> interfaceType, T target )
    {
        return interfaceType.cast( Proxy.newProxyInstance( InvocationLoggingDecorator.class.getClassLoader(),
                                                           new Class<?>[] { interfaceType },
                                                           new Handler<T>(
                                                                           interfaceType,
                                                                           target ) ) );
    }
}
