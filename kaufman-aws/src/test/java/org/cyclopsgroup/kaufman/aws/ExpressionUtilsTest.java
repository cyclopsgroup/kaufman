package org.cyclopsgroup.kaufman.aws;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExpressionUtilsTest
{
    @Test
    public void testWithNoExpression()
    {
        assertEquals( "abcde", ExpressionUtils.populate( "abcde" ) );
    }

    @Test
    public void testWithDefaultValue()
    {
        assertEquals( "abdefcde", ExpressionUtils.populate( "ab${x:def}cde" ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testWithInvalidVariable()
    {
        ExpressionUtils.populate( "ab${x}cde" );
    }
}
