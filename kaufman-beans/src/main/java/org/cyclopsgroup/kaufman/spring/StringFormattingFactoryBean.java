package org.cyclopsgroup.kaufman.spring;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

public class StringFormattingFactoryBean
    implements FactoryBean<String>
{
    private static final Log LOG =
        LogFactory.getLog( StringFormattingFactoryBean.class );

    private final String format;

    private List<? extends Object> parameters;

    public StringFormattingFactoryBean( String format )
    {
        this.format = format;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getObject()
    {
        Object[] params;
        if ( parameters == null || parameters.isEmpty() )
        {
            params = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        else
        {
            params = parameters.toArray();
        }
        LOG.info( "Merging format=" + format + ", params="
            + Arrays.toString( params ) );
        String result = String.format( format, params );
        LOG.info( "Returning " + result );
        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<String> getObjectType()
    {
        return String.class;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setParameters( List<? extends Object> parameters )
    {
        this.parameters = parameters;
    }
}
