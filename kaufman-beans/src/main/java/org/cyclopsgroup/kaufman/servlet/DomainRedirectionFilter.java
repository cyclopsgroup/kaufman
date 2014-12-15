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
 * Filter that redirects requests to the primary domain if request is not from
 * it. For example, it can be configured to redirect http://mysite.com to
 * http://www.mysite.com, or from http://www.mysite.com to
 * https://www.mysite.com.
 */
public class DomainRedirectionFilter
    implements Filter
{
    private static final String HEADER_X_FORWARDED_PROTO = "X-Forwarded-Proto";

    private static final String HEADER_X_FORWARDED_PORT = "X-Forwarded-Port";

    private static final Log LOG =
        LogFactory.getLog( DomainRedirectionFilter.class );

    private String baseUrl;

    @Override
    public void destroy()
    {
    }

    private static String buildUrl( String scheme, String hostName, int port )
    {
        StringBuilder url =
            new StringBuilder( scheme.toLowerCase() ).append( "://" ).append( hostName );
        if ( ( scheme.equalsIgnoreCase( "http" ) && port != 80 )
            || ( scheme.equalsIgnoreCase( "https" ) && port != 443 ) )
        {
            url.append( ":" ).append( port );
        }
        return url.toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response,
                          FilterChain chain )
        throws IOException, ServletException
    {
        // Ignore requests from non-human
        if ( ServletUtils.isBot( request ) )
        {
            chain.doFilter( request, response );
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;

        // ignore non-GET reuqests
        if ( req.getServerName().equals( "localhost" )
            || !req.getMethod().equalsIgnoreCase( "get" ) )
        {
            chain.doFilter( request, response );
            return;
        }

        // If PROTO header is specified, the request scheme is what's specified
        // in header instead of the value returned by getScheme() method.
        String requestedScheme = req.getScheme();
        if ( req.getHeader( HEADER_X_FORWARDED_PROTO ) != null )
        {
            requestedScheme = req.getHeader( HEADER_X_FORWARDED_PROTO );
        }

        // If PORT header is specified, the request scheme is what's specified
        // in header instead of the value returned by getServerPort() method
        int requestedPort = req.getServerPort();
        if ( req.getHeader( HEADER_X_FORWARDED_PORT ) != null )
        {
            requestedPort =
                Integer.parseInt( req.getHeader( HEADER_X_FORWARDED_PORT ) );
        }
        String requestedBase =
            buildUrl( requestedScheme, req.getServerName(), requestedPort );

        // If request is ready from primary domain, continue and return
        if ( requestedBase.equalsIgnoreCase( baseUrl ) )
        {
            chain.doFilter( request, response );
            return;
        }

        String requestedUrl = req.getRequestURL().toString();
        String redirectUrl =
            baseUrl
                + StringUtils.removeStart( requestedUrl,
                                           buildUrl( req.getScheme(),
                                                     req.getServerName(),
                                                     req.getServerPort() ) );
        LOG.info( "Redirecting request from " + requestedUrl
            + " to new location: " + redirectUrl );
        ( (HttpServletResponse) response ).sendRedirect( redirectUrl );
    }

    @Override
    public void init( FilterConfig config )
        throws ServletException
    {
        baseUrl = config.getInitParameter( "baseUrl" );
    }
}
