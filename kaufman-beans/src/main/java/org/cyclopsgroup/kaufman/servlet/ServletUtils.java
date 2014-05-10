package org.cyclopsgroup.kaufman.servlet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils
{
    private static final Set<String> USER_AGENT_BOT_KEYWORDS =
        Collections.unmodifiableSet( new HashSet<String>(
                                                          Arrays.asList( "googlebot",
                                                                         "msnbot",
                                                                         "baiduspider",
                                                                         "elb-healthchecker",
                                                                         "zmeu",
                                                                         "python-urllib",
                                                                         "c4.sh",
                                                                         "massscan",
                                                                         "mediapartners" ) ) );

    public static boolean isBot( ServletRequest request )
    {
        return isBot( ( (HttpServletRequest) request ).getHeader( "User-Agent" ) );
    }

    public static boolean isBot( String userAgent )
    {
        for ( String blacklistedKeyword : USER_AGENT_BOT_KEYWORDS )
        {
            if ( userAgent.toLowerCase().contains( blacklistedKeyword ) )
            {
                return true;
            }
        }
        return false;
    }
}
