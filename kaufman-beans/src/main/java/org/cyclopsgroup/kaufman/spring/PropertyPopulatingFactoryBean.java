package org.cyclopsgroup.kaufman.spring;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.cyclopsgroup.kaufman.PropertiesHierarchyUtils;
import org.springframework.beans.factory.FactoryBean;

public class PropertyPopulatingFactoryBean<T>
    implements FactoryBean<T>
{
    private final T bean;

    private String prefix;

    private final Properties properties;

    public PropertyPopulatingFactoryBean( T bean, Properties properties )
    {
        Validate.notNull( bean, "Bean can't be NULL" );
        Validate.notNull( properties, "Source properties can't be NULL" );
        this.bean = bean;
        this.properties = properties;
    }

    /**
     * @inheritDoc
     */
    @Override
    public T getObject()
        throws InvocationTargetException, IllegalAccessException
    {
        Map<String, String> props = new HashMap<String, String>();
        if ( StringUtils.isNotEmpty( prefix ) )
        {
            Properties subProps =
                PropertiesHierarchyUtils.subset( properties, prefix );
            for ( Object key : subProps.keySet() )
            {
                props.put( key.toString(), subProps.getProperty( key.toString() ) );
            }
        }
        else
        {
            for ( Object key : properties.keySet() )
            {
                props.put( key.toString(),
                           properties.getProperty( key.toString() ) );
            }
        }
        BeanUtils.populate( bean, props );
        return bean;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<?> getObjectType()
    {
        return bean.getClass();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }
}
