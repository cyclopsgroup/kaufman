package org.cyclopsgroup.kaufman.interfaces;

public interface Transportable<T>
{
    void exportTo( T destination );

    void importFrom( T source );
}
