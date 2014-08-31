package org.cyclopsgroup.kaufman.spring;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MappedFactoryBeanTest
{
    @Test
    public void testDefaultValue()
    {
        Map<String, String> m = new HashMap<String, String>();
        m.put( "a", "aa" );
        MappedFactoryBean<String> b = new MappedFactoryBean<String>( m, "b" );
        b.setDefaultValue( "cc" );
        assertEquals( "cc", b.getObject() );
    }

    @Test
    public void testGetValue()
    {
        Map<String, String> m = new HashMap<String, String>();
        m.put( "a", "aa" );
        m.put( "b", "bb" );
        MappedFactoryBean<String> b = new MappedFactoryBean<String>( m, "b" );
        assertEquals( "bb", b.getObject() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidKey()
    {
        Map<String, String> m = new HashMap<String, String>();
        m.put( "a", "aa" );
        MappedFactoryBean<String> b = new MappedFactoryBean<String>( m, "b" );
        b.getObject();
    }
}
