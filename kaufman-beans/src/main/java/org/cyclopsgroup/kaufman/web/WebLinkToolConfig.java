package org.cyclopsgroup.kaufman.web;

import java.util.Set;
import java.util.regex.Pattern;

public interface WebLinkToolConfig
{
    Set<Pattern> getExternalResourcePatterns();

    String getExternalResourceUrl();
}
