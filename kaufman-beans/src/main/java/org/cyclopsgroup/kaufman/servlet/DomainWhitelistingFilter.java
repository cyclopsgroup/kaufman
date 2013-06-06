package org.cyclopsgroup.kaufman.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.DigestUtils;

public class DomainWhitelistingFilter
    extends AbstractRequestAuthorizingFilter
{
    private static final Log LOG =
        LogFactory.getLog( DomainWhitelistingFilter.class );

    private final Set<String> buildinDomains =
        Collections.unmodifiableSet( new HashSet<String>(
                                                          Arrays.asList( "127.0.0.1",
                                                                         "localhost" ) ) );

    private Set<String> whiteDomainHashes = Collections.emptySet();

    private Set<String> whiteDomainNames = Collections.emptySet();

    /**
     * @inheritDoc
     */
    @Override
    public void destroy()
    {
    }

    /**
     * @inheritDoc
     */
    @Override
    public void init( FilterConfig config )
        throws ServletException
    {
        String names = config.getInitParameter( "whiteDomainNames" );
        if ( StringUtils.isNotBlank( names ) )
        {
            whiteDomainNames =
                Collections.unmodifiableSet( new HashSet<String>(
                                                                  Arrays.asList( StringUtils.split( names,
                                                                                                    ',' ) ) ) );
            LOG.info( "These domains are accepted: " + whiteDomainNames );
        }

        String hashes = config.getInitParameter( "whiteDomainHashes" );
        if ( StringUtils.isNotBlank( hashes ) )
        {
            whiteDomainHashes =
                Collections.unmodifiableSet( new HashSet<String>(
                                                                  Arrays.asList( StringUtils.split( hashes,
                                                                                                    ',' ) ) ) );
            LOG.info( "These domain hashes are accepted: " + whiteDomainHashes );
        }
        if ( whiteDomainNames.isEmpty() && whiteDomainHashes.isEmpty() )
        {
            LOG.info( "No domain is configured, only local access to service is allowed" );
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    protected boolean isRequestAuthorized( HttpServletRequest request )
        throws ServletException, IOException
    {
        String serverName = request.getServerName();
        if ( buildinDomains.contains( serverName )
            || whiteDomainNames.contains( serverName ) )
        {
            return true;
        }
        return whiteDomainHashes.contains( DigestUtils.md5DigestAsHex( serverName.getBytes() ) );
    }
}
