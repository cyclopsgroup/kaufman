package org.cyclopsgroup.kaufman.wfv;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

@XmlRootElement( name = "FormValidationResult" )
public class FormValidationResult
{
    private List<FieldValidationResult> fields;

    private boolean successful;

    @XmlElementWrapper
    @XmlElement( name = "field" )
    public final List<FieldValidationResult> getFields()
    {
        return fields;
    }

    @XmlElement
    public final boolean isSuccessful()
    {
        return successful;
    }

    public final void setFields( List<FieldValidationResult> fields )
    {
        this.fields = fields;
    }

    public final void setSuccessful( boolean successful )
    {
        this.successful = successful;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
