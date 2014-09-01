package org.cyclopsgroup.kaufman.spring;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;

public class MappedFactoryBean<T>
    implements FactoryBean<T>
{
    private T defaultValue;

    private final String key;

    private final Map<String, T> map;

    public MappedFactoryBean( Map<String, T> map, String key )
    {
        Validate.notNull( map, "Map can't be NULL" );
        Validate.notNull( key, "Key can't be NULL" );
        this.map = map;
        this.key = key;
    }

    /**
     * @inheritDoc
     */
    @Override
    public T getObject()
    {
        T value = map.get( key );
        if ( value != null )
        {
            return value;
        }
        if ( defaultValue != null )
        {
            return defaultValue;
        }
        throw new IllegalArgumentException( "Key " + key
            + " is not mapped to any value, these are valid keys: "
            + map.keySet() );
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<?> getObjectType()
    {
        return Object.class;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setDefaultValue( T defaultValue )
    {
        this.defaultValue = defaultValue;
    }
}
