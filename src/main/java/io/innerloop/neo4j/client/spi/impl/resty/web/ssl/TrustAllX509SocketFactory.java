package io.innerloop.neo4j.client.spi.impl.resty.web.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class TrustAllX509SocketFactory {
    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, TrustAllX509Certificates.TRUST_MANAGER, null);
        return sc.getSocketFactory();
    }

}
