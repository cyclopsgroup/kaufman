package org.cyclopsgroup.kaufman.servlet;

import java.io.IOException;

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

/**
 * Filter that redirects requests from secondary domains to the primary domain
 */
public class DomainRedirectionFilter
    implements Filter
{
    private static final Log LOG =
        LogFactory.getLog( DomainRedirectionFilter.class );

    private String baseUrl;

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response,
                          FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        if ( !req.getMethod().equalsIgnoreCase( "get" ) )
        {
            chain.doFilter( request, response );
            return;
        }
        String requestedUrl = req.getRequestURL().toString();
        String serverName = req.getServerName();
        if ( requestedUrl.startsWith( baseUrl )
            || serverName.equals( "localhost" ) )
        {
            chain.doFilter( request, response );
            return;
        }
        String requestedBase = req.getScheme() + "://" + serverName;
        if ( !requestedUrl.startsWith( requestedBase ) )
        {
            throw new IllegalStateException( "Requested URL = " + requestedUrl
                + ", requestedBase = " + requestedBase
                + ", something is wrong here!" );
        }

        String redirectUrl =
            baseUrl + StringUtils.removeStart( requestedUrl, requestedBase );
        LOG.info( "Redirecting request to new location: " + redirectUrl );
        ( (HttpServletResponse) response ).sendRedirect( redirectUrl );
    }

    @Override
    public void init( FilterConfig config )
        throws ServletException
    {
        baseUrl = config.getInitParameter( "baseUrl" );
    }
}
