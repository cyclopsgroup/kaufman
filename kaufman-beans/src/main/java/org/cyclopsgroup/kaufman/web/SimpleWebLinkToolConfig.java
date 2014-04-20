package org.cyclopsgroup.kaufman.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SimpleWebLinkToolConfig
    implements WebLinkToolConfig
{
    private Set<Pattern> externalResourcePatterns;

    private String externalResourceUrl;

    public Set<Pattern> getExternalResourcePatterns()
    {
        return externalResourcePatterns;
    }

    public String getExternalResourceUrl()
    {
        return externalResourceUrl;
    }

    public void setExternalResourcePaths( List<String> paths )
    {
        Set<Pattern> patterns = new HashSet<Pattern>();
        for ( String path : paths )
        {
            patterns.add( Pattern.compile( path ) );
        }
        setExternalResourcePatterns( patterns );
    }

    public void setExternalResourcePatterns( Set<Pattern> externalResourcePatterns )
    {
        this.externalResourcePatterns =
            Collections.unmodifiableSet( externalResourcePatterns );
    }

    public void setExternalResourceUrl( String externalResourceUrl )
    {
        this.externalResourceUrl = externalResourceUrl;
    }
}
