package org.cyclopsgroup.kaufman.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DomainWhitelistingFilter
    implements Filter
{
    private static final Log LOG =
        LogFactory.getLog( DomainWhitelistingFilter.class );

    private Set<String> pathIgnored =
        Collections.unmodifiableSet( new HashSet<String>(
                                                          Arrays.asList( "/ping" ) ) );

    private Set<String> whitelistedDomains = Collections.emptySet();

    /**
     * @inheritDoc
     */
    @Override
    public void init( FilterConfig config )
        throws ServletException
    {
        String param = config.getInitParameter( "whitelistedDomains" );
        if ( StringUtils.isBlank( param ) )
        {
            LOG.info( "No configuraton is provided, only local access is accepted" );
        }
        else
        {
            whitelistedDomains =
                Collections.unmodifiableSet( new HashSet<String>(
                                                                  Arrays.asList( StringUtils.split( param,
                                                                                                    ',' ) ) ) );
            LOG.info( "These domains are accepted: " + whitelistedDomains );
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response,
                          FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        String path =
            StringUtils.stripToEmpty( req.getServletPath() )
                + StringUtils.stripToEmpty( req.getPathInfo() );
        if ( pathIgnored.contains( path ) )
        {
            chain.doFilter( request, response );
            return;
        }

        String serverName = request.getServerName();
        if ( !serverName.equalsIgnoreCase( "localhost" )
            && !whitelistedDomains.contains( serverName ) )
        {
            ( (HttpServletResponse) response ).sendError( HttpServletResponse.SC_UNAUTHORIZED,
                                                          "Unauthorized access against server domain "
                                                              + serverName );
            return;
        }
        chain.doFilter( request, response );
    }

    /**
     * @inheritDoc
     */
    @Override
    public void destroy()
    {
    }
}
