package org.cyclopsgroup.kaufman;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class PropertiesHierarchyUtils
{
    public static Properties subset( Properties source, String prefix )
    {
        Properties props = new Properties();
        for ( Object keyObject : source.keySet() )
        {
            String key = (String) keyObject;
            if ( !key.startsWith( prefix + "." ) )
            {
                continue;
            }
            String shortKey = StringUtils.removeStart( key, prefix + "." );
            props.setProperty( shortKey, source.getProperty( key ) );
        }
        return props;
    }

    public static Properties expandInclusion( LocateableResource resource )
        throws IOException
    {
        Properties source = new Properties();
        InputStream in = resource.openToRead();
        try
        {
            source.load( in );
        }
        finally
        {
            in.close();
        }
        String includeProperty = source.getProperty( "include", null );
        source.remove( "include" );
        if ( StringUtils.isBlank( includeProperty ) )
        {
            return source;
        }

        String[] includes = StringUtils.split( includeProperty, ',' );
        Properties result = new Properties();
        for ( String include : includes )
        {
            Properties props = expandInclusion( resource.locate( include ) );
            result.putAll( props );
        }
        result.putAll( source );
        return result;
    }
}
