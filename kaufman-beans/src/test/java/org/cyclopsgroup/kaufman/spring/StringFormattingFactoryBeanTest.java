package org.cyclopsgroup.kaufman.spring;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class StringFormattingFactoryBeanTest
{
    @Test
    public void testFormat()
    {
        StringFormattingFactoryBean b =
            new StringFormattingFactoryBean( "hello %s" );
        b.setParameters( Arrays.asList( "world" ) );
        assertEquals( "hello world", b.getObject() );
    }
}
