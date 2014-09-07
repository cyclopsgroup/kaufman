package org.cyclopsgroup.kaufman.aws;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ExpressionUtils
{
    public static String populate( String input )
    {
        Map<Object, Object> props = new HashMap<Object, Object>();
        props.putAll( System.getenv() );
        props.putAll( System.getProperties() );
        return populateInternally( input, props );
    }

    public static String populate( String input, Map<String, String> props )
    {
        return populateInternally( input, props );
    }

    public static String populate( String input, Properties props )
    {
        return populateInternally( input, props );
    }

    private static String populateInternally( String input,
                                              Map<? extends Object, ? extends Object> map )
    {
        StringBuilder result = new StringBuilder();
        String in = input;
        int start;
        for ( ;; )
        {
            start = in.indexOf( "${" );
            if ( start == -1 )
            {
                result.append( in );
                break;
            }
            int end = in.indexOf( "}" );
            if ( end < start )
            {
                throw new IllegalArgumentException( "} is before ${ in " + in );
            }
            result.append( in.substring( 0, start ) );
            String expression = in.substring( start + 2, end );
            String defaultValue = null;
            int delimiter = expression.indexOf( ':' );
            String var;
            if ( delimiter == -1 )
            {
                var = expression;
            }
            else
            {
                var = expression.substring( 0, delimiter );
                defaultValue = expression.substring( delimiter + 1 );
            }
            String value = (String) map.get( var );
            if ( value == null && defaultValue == null )
            {
                throw new IllegalArgumentException( "Variable " + var
                    + " is not defined" );
            }
            result.append( value == null ? defaultValue : value );
            if ( end == in.length() - 1 )
            {
                break;
            }
            in = in.substring( end + 1 );
        }
        return result.toString();
    }
}
