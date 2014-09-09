package org.cyclopsgroup.kaufman.spring;

import java.util.Properties;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;

public class SubPropertiesFactoryBean
    implements FactoryBean<Properties>
{
    static Properties toProperties( ExtendedProperties ep )
    {
        Properties props = new Properties();
        for ( Object key : ep.keySet() )
        {
            props.setProperty( key.toString(), ep.getString( key.toString() ) );
        }
        return props;
    }

    private final String prefix;

    private String prefixAppended;

    private final Properties source;

    private String suffixAppended;

    public SubPropertiesFactoryBean( Properties source, String prefix )
    {
        Validate.notNull( source, "Source properties can't be NULL" );
        Validate.notEmpty( prefix, "Prefix can not be empty" );
        this.source = source;
        this.prefix = prefix;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Properties getObject()
    {
        ExtendedProperties ep =
            ExtendedProperties.convertProperties( source ).subset( prefix );
        Properties props = new Properties();
        for ( Object key : ep.keySet() )
        {
            String newKey = key.toString();
            if ( StringUtils.isNotBlank( prefixAppended ) )
            {
                newKey = prefixAppended + "." + newKey;
            }
            if ( StringUtils.isNotBlank( suffixAppended ) )
            {
                newKey = newKey + "." + suffixAppended;
            }
            props.setProperty( newKey, ep.getString( key.toString() ) );
        }
        return props;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<?> getObjectType()
    {
        return Properties.class;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setPrefixAppended( String prefixAppended )
    {
        this.prefixAppended = prefixAppended;
    }

    public void setSuffixAppended( String suffixAppended )
    {
        this.suffixAppended = suffixAppended;
    }
}
