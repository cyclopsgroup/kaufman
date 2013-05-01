package org.cyclopsgroup.kaufman.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;

public class JaxbTypeVerifier
{
    public static class Builder
    {
        private Options options = new Options();

        private Builder()
        {
        }

        public JaxbTypeVerifier toVerifier()
        {
            return new JaxbTypeVerifier( options );
        }

        public Builder withRequireTrasnsient( boolean require )
        {
            options.requireTransient = require;
            return this;
        }

        public Builder withVerifyDataTimeAdapter( boolean verify )
        {
            options.verifyDateTimeAdapter = verify;
            return this;
        }
    }

    private static class Options
    {
        private boolean requireTransient = true;

        private boolean verifyAttribute = true;

        private boolean verifyClass = true;

        private boolean verifyDateTimeAdapter = true;

        private boolean verifyEnum = true;

        private boolean verifyEnumValue = true;
    }

    private class Visitor
        implements PackageScanner.ClassVisitor
    {
        @SuppressWarnings( { "unchecked", "rawtypes" } )
        @Override
        public void visitClass( Class<?> type )
        {
            if ( type.isInterface() || type.isAnnotation() )
            {
                return;
            }
            if ( type.isEnum() )
            {
                if ( !options.verifyEnum
                    || !type.isAnnotationPresent( XmlEnum.class ) )
                {
                    return;
                }
                verifyXmlEnum( (Class<? extends Enum>) type );
                return;
            }
            if ( type.isAnnotationPresent( XmlType.class )
                || type.isAnnotationPresent( XmlRootElement.class ) )
            {

                if ( !options.verifyClass )
                {
                    return;
                }
                verifyXmlElement( type );
                return;
            }
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static JaxbTypeVerifier newDefaultInstance()
    {
        return new JaxbTypeVerifier( new Options() );
    }

    private final Options options;

    private JaxbTypeVerifier( Options options )
    {
        this.options = options;
    }

    public void verifyPackage( String packageName )
        throws IOException
    {
        PackageScanner.scanPackage( packageName, new Visitor() );
    }

    private void verifyXmlElement( Class<?> elementType )
    {
        try
        {
            for ( PropertyDescriptor prop : Introspector.getBeanInfo( elementType ).getPropertyDescriptors() )
            {
                if ( prop.getReadMethod() == null
                    || prop.getName().equals( "class" )
                    || prop.getReadMethod().isAnnotationPresent( XmlTransient.class ) )
                {
                    continue;
                }
                if ( prop.getReadMethod().isAnnotationPresent( XmlElement.class )
                    || prop.getReadMethod().isAnnotationPresent( XmlAttribute.class ) )
                {
                    Method setter = prop.getWriteMethod();
                    assertNotNull( "Property " + prop.getName() + " in "
                        + elementType + " is missing setter", setter );

                    assertFalse( "Setter of " + prop.getName()
                                     + " is not supposed to be annotated in "
                                     + elementType,
                                 setter.isAnnotationPresent( XmlElement.class )
                                     || setter.isAnnotationPresent( XmlAttribute.class ) );
                    if ( options.verifyDateTimeAdapter
                        && prop.getPropertyType() == DateTime.class )
                    {

                    }
                    continue;
                }
                if ( options.requireTransient )
                {
                    fail( "Getter of "
                        + prop.getName()
                        + " in "
                        + elementType
                        + " is not annotated with XmlTrasient, XmlElement or XmlAttribute" );
                }
            }
        }
        catch ( IntrospectionException e )
        {
            throw new IllegalStateException( "Can't introspect element "
                + elementType, e );
        }
    }

    private <T extends Enum<T>> void verifyXmlEnum( Class<T> enumType )
    {
        for ( Enum<T> enu : EnumSet.allOf( enumType ) )
        {
            Field field;
            try
            {
                field = enumType.getField( enu.name() );
            }
            catch ( NoSuchFieldException e )
            {
                throw new IllegalStateException( "Can't get field "
                    + enu.name() + " of enum " + enumType, e );
            }
            XmlEnumValue value = field.getAnnotation( XmlEnumValue.class );
            assertNotNull( enumType + "." + enu.name()
                + " is not annotated with " + XmlEnumValue.class, value );
            if ( options.verifyEnumValue )
            {
                assertEquals( "Enum " + enumType + "." + enu.name()
                    + " is annotated with XmlEnumValue of wrong value() "
                    + value.value(), enu.name(), value.value() );
            }
        }
    }
}
