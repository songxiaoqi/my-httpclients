package com.kahui.httpclient.internal;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 *
 */
public class DefaultTrustManager implements X509TrustManager{
    /**
     * 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，并且返回是否可以确认和信任将其用于基于身份验证类型的客户端 SSL 身份验证。
     * @param x509Certificates
     * @param s
     * @throws CertificateException
     */
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    /**
     * 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，并且返回是否可以确认和信任将其用于基于身份验证类型的服务器 SSL 身份验证。
     * @param x509Certificates
     * @param s
     * @throws CertificateException
     */
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    /**
     * 返回受身份验证同位体信任的认证中心的数组。
     * @return
     */
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
