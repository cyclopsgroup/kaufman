package org.cyclopsgroup.kaufman.interfaces;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Adapter to convert {@link DateTime} back and forth for JAXB
 *
 * @author <a href="mailto:jiaqi@cyclopsgroup.org">Jiaqi Guo</a>
 */
public class JodaDateTimeAdapter
    extends XmlAdapter<String, DateTime>
{
    private static final DateTimeFormatter FORMAT = ISODateTimeFormat.dateTime();

    private static final DateTimeFormatter PARSER = ISODateTimeFormat.dateTimeParser();

    /**
     * @inheritDoc
     */
    @Override
    public String marshal( DateTime v )
        throws Exception
    {
        return FORMAT.print( v );
    }

    /**
     * @inheritDoc
     */
    @Override
    public DateTime unmarshal( String v )
        throws Exception
    {
        return PARSER.parseDateTime( v );
    }
}
