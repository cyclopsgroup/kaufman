package org.cyclopsgroup.kaufman.interfaces;

public interface Transportable<T>
{
    T exportTo( T destination );

    void importFrom( T source );
}
