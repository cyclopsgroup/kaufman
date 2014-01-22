package org.cyclopsgroup.kaufman.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Transports
{
    public static <T> List<T> transportList( List<? extends Transportable<T>> sources,
                                             Class<T> destinationType )
    {
        if ( sources.isEmpty() )
        {
            return Collections.emptyList();
        }
        List<T> destination = new ArrayList<T>();
        for ( Transportable<T> source : sources )
        {
            T dest;
            try
            {
                dest = destinationType.newInstance();
            }
            catch ( InstantiationException e )
            {
                throw new IllegalStateException(
                                                 "Can't instantiate destination type "
                                                     + destinationType + ": "
                                                     + e.getMessage(), e );
            }
            catch ( IllegalAccessException e )
            {
                throw new IllegalStateException(
                                                 "Can't instantiate destination type "
                                                     + destinationType + ": "
                                                     + e.getMessage(), e );
            }
            destination.add( source.exportTo( dest ) );
        }
        return destination;
    }

    private Transports()
    {
    }
}
