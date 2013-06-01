package org.cyclopsgroup.kaufman.wfv;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;

@XmlType
public class FieldValidationResult
{
    private List<String> failureMessages;

    private String fieldName;

    private boolean successful;

    @XmlElementWrapper
    @XmlElement( name = "message" )
    public final List<String> getFailureMessages()
    {
        return failureMessages;
    }

    @XmlElement
    public final String getFieldName()
    {
        return fieldName;
    }

    @XmlElement
    public final boolean isSuccessful()
    {
        return successful;
    }

    public final void setFailureMessages( List<String> failureMessages )
    {
        this.failureMessages = failureMessages;
    }

    public final void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    public final void setSuccessful( boolean successful )
    {
        this.successful = successful;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
