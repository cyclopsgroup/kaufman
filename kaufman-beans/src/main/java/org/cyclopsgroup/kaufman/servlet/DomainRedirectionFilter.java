package org.cyclopsgroup.kaufman.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
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

    private static class ForwardedHttpRequest
        extends HttpServletRequestWrapper
    {
        private final int serverPort;

        private final String scheme;

        private final boolean forwarded;

        protected ForwardedHttpRequest( HttpServletRequest request )
        {
            super( request );

            boolean changed = false;
            if ( request.getHeader( HEADER_X_FORWARDED_PORT ) != null )
            {
                this.serverPort =
                    Integer.parseInt( request.getHeader( HEADER_X_FORWARDED_PORT ) );
                changed = true;
            }
            else
            {
                this.serverPort = super.getServerPort();
            }

            if ( request.getHeader( HEADER_X_FORWARDED_PROTO ) != null )
            {
                this.scheme = request.getHeader( HEADER_X_FORWARDED_PROTO );
                changed = true;
            }
            else
            {
                this.scheme = super.getScheme();
            }
            this.forwarded = changed;
        }

        /**
         * @inheritDoc
         */
        @Override
        public StringBuffer getRequestURL()
        {
            if ( !forwarded )
            {
                return super.getRequestURL();
            }
            String originalBase =
                buildUrl( super.getScheme(), super.getServerName(),
                          super.getServerPort() );
            String url = super.getRequestURL().toString();
            if ( url.startsWith( originalBase ) )
            {
                String newBase =
                    buildUrl( scheme, super.getServerName(), serverPort );
                return new StringBuffer( newBase ).append( url.substring( originalBase.length() ) );
            }
            return super.getRequestURL();
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getScheme()
        {
            return scheme;
        }

        /**
         * @inheritDoc
         */
        @Override
        public int getServerPort()
        {
            return serverPort;
        }
    }

    private static final Log LOG =
        LogFactory.getLog( DomainRedirectionFilter.class );

    private String baseUrl;

    /**
     * @inheritDoc
     */
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

        HttpServletRequest req =
            new ForwardedHttpRequest( (HttpServletRequest) request );

        // ignore non-GET reuqests
        if ( req.getServerName().equals( "localhost" )
            || !req.getMethod().equalsIgnoreCase( "get" ) )
        {
            chain.doFilter( req, response );
            return;
        }

        String requestBase =
            buildUrl( req.getScheme(), req.getServerName(), req.getServerPort() );

        // If request is ready from primary domain, continue and return
        if ( requestBase.equalsIgnoreCase( baseUrl ) )
        {
            chain.doFilter( req, response );
            return;
        }

        String requestedUrl = req.getRequestURL().toString();
        String redirectUrl =
            baseUrl + requestedUrl.substring( requestBase.length() );
        LOG.info( "Redirecting request from " + requestedUrl
            + " to new location: " + redirectUrl );
        if ( StringUtils.isNotBlank( req.getQueryString() ) )
        {
            redirectUrl += ( "?" + req.getQueryString() );
        }
        ( (HttpServletResponse) response ).sendRedirect( redirectUrl );
    }

    @Override
    public void init( FilterConfig config )
        throws ServletException
    {
        baseUrl = config.getInitParameter( "baseUrl" );
    }
}
