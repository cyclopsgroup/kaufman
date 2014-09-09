package org.cyclopsgroup.kaufman.spring;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Properties;

import org.junit.Test;

public class PropertyPopulatingFactoryBeanTest
{
    public static class Person
    {
        private int age;

        private String fullName;

        private BigDecimal weight;

        public void setAge( int age )
        {
            this.age = age;
        }

        public void setFullName( String fullName )
        {
            this.fullName = fullName;
        }

        public void setWeight( BigDecimal weight )
        {
            this.weight = weight;
        }
    }

    @Test
    public void testWithPrefix()
        throws InvocationTargetException, IllegalAccessException
    {
        Properties props = new Properties();
        props.setProperty( "a.fullName", "Bender" );
        props.setProperty( "a.age", "5" );
        props.setProperty( "a.weight", "180.5" );
        props.setProperty( "a.job", "Bending" );
        props.setProperty( "fullName", "Frys" );
        props.setProperty( "b.age", "100" );
        PropertyPopulatingFactoryBean<Person> f =
            new PropertyPopulatingFactoryBean<Person>( new Person(), props );
        f.setPrefix( "a" );
        Person p = f.getObject();
        assertEquals( "Bender", p.fullName );
        assertEquals( 5, p.age );
        assertEquals( new BigDecimal( "180.5" ), p.weight );
    }
}
