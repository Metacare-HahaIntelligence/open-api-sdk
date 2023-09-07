/*
 * Copyright © 2021 HaHa Cloud Information Technology Co., Ltd. All rights reserved.
 */

package com.hahacloud.openapi;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 李丰
 * @version [版本号, 2021/11/4]
 */

public class WsseHttpHeader {
    /**
     * WSSE头部
     */
    public static final String WSSE_HEAD = "WSSE ";

    public static final String WSSE_HEADER = "x-wsse";

    public static final String AUTH_HEADER = "Authorization";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String DEFAULT_REALM = "SDP";

    private static final String DEFAULT_PROFILE = "UsernameToken";

    private static final String DEFAULT_TYPE = "Appkey";

    private static String userNameExpression = "Username=\"([A-Za-z0-9+/=]+)\"";

    private static String nonceExpression = "Nonce=\"([A-Za-z0-9+/=]+)\"";

    private static String passwordDigestExpression = "PasswordDigest=\"([A-Za-z0-9+/=]+)\"";

    private static String parseCreatedExpression = "Created=\"(\\d{4}-\\d{1,2}-\\d{1,2}T\\d{2}:\\d{2}:\\d{2}Z)\"";

    private static String reamExpression = "realm=\"([A-Za-z0-9+/=]+)\"";

    private static String profileExpression = "profile=\"([A-Za-z0-9+/=]+)\"";

    private static String typeExpression = "type=\"([A-Za-z0-9+/=]+)\"";

    private static Pattern headerWssePattern;

    private static Pattern headerTokenPattern;

    /**
     * realm="SDP"
     */
    private String realm;

    /**
     * profile头部值="UsernameToken"
     */
    private String profile;

    /**
     * type=Appkey
     */
    private String type;

    /**
     * Username
     */
    private String userName;

    /**
     * PasswordDigest
     */
    private String passwordDigest;

    /**
     * nonce随机数
     */
    private String nonce;

    /**
     * 时间戳=yyyy-MM-dd'T'HH:mm:ss'Z'
     */
    private String created;

    /**
     * Getters & Setters.
     */
    public String getUserName() {
        return userName;
    }

    public String getPasswordDigest() {
        return passwordDigest;
    }

    public String getNonce() {
        return nonce;
    }

    public String getCreated() {
        return created;
    }

    public String getProfile() {
        return profile;
    }

    public String getRealm() {
        return realm;
    }

    public String getType() {
        return type;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public static void setUserNameExpression(String userNameExpression) {
        WsseHttpHeader.userNameExpression = userNameExpression;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public static WsseHttpHeader parseHttpHeader(String httpTokenHeader, String httpWsseHeader) throws Exception {
        return parseHttpHeader(httpTokenHeader, httpWsseHeader, null, null, null);
    }

    public static WsseHttpHeader parseHttpHeader(String httpTokenHeader, String httpWsseHeader, String defaultRealm,
                                                 String defaultProfile, String defaultType) throws Exception {
        WsseHttpHeader wsseHeader = new WsseHttpHeader();
        if (StringUtils.isNotEmpty(httpTokenHeader)) {
            httpTokenHeader = httpTokenHeader.replaceAll(", ", ",");
            Matcher matcher = getHeaderTokenPattern().matcher(httpTokenHeader);
            if (matcher.matches()) {
                wsseHeader.setRealm(matcher.group(1));
                wsseHeader.setProfile(matcher.group(2));
                wsseHeader.setType(matcher.group(3));
            }
        }

        if (StringUtils.isEmpty(defaultRealm)) {
            defaultRealm = DEFAULT_REALM;
        }
        if (StringUtils.isEmpty(defaultProfile)) {
            defaultProfile = DEFAULT_PROFILE;
        }
        if (StringUtils.isEmpty(defaultType)) {
            defaultType = DEFAULT_TYPE;
        }

        if (!defaultRealm.equals(wsseHeader.getRealm()) || !defaultProfile.equals(wsseHeader.getProfile())
                || !defaultType.equals(wsseHeader.getType())) {
            throw new Exception("Invalid Autorization : " + httpTokenHeader);
        }

        if (!StringUtils.isEmpty(httpWsseHeader)) {
            if (!httpWsseHeader.startsWith(wsseHeader.getProfile())) {
                throw new Exception("Invalid WSSE : " + httpWsseHeader);
            }
            httpWsseHeader = httpWsseHeader.replaceAll(", ", ",");
            Matcher matcher = getHeaderPattern(wsseHeader.getProfile()).matcher(httpWsseHeader);
            if (matcher.matches()) {
                wsseHeader.setUserName(matcher.group(1));
                wsseHeader.setPasswordDigest(matcher.group(2));
                wsseHeader.setNonce(matcher.group(3));
                wsseHeader.setCreated(matcher.group(4));

                if (Base64.getDecoder().decode(wsseHeader.getPasswordDigest()) == null) {
                    throw new Exception("Invalid Password Digest : " + wsseHeader.getPasswordDigest());
                }
            }
        }
        return wsseHeader;
    }

    public String createPasswordDigest(String password) {
        String sha256hex = DigestUtils.sha256Hex(getNonce() + getCreated() + password);
        return Base64.getEncoder().encodeToString(sha256hex.getBytes(StandardCharsets.UTF_8));
    }

    public static WsseHttpHeader createDefaultWsse(String appKey, String secret) {
        WsseHttpHeader wsseHttpHeader = new WsseHttpHeader();
        wsseHttpHeader.setRealm(DEFAULT_REALM);
        wsseHttpHeader.setProfile(DEFAULT_PROFILE);
        wsseHttpHeader.setType(DEFAULT_TYPE);
        wsseHttpHeader.setUserName(appKey);
        wsseHttpHeader.setNonce(UUID.randomUUID().toString().replaceAll("-", ""));
        wsseHttpHeader.setCreated(LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        wsseHttpHeader.setPasswordDigest(wsseHttpHeader.createPasswordDigest(secret));
        return wsseHttpHeader;
    }

    public String getWsseHeader() {
        return String.format(getProfile() + " Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"",
                getUserName(), getPasswordDigest(), getNonce(), getCreated());
    }

    public String getTokenHeader() {
        return WSSE_HEAD + "realm=\"" + realm + "\",profile=\"" + profile + "\",type=\"" + type + "\"";
    }

    /**
     *
     */
    WsseHttpHeader() {
        super();
    }


    /**
     * @return Returns the headerPattern.
     */
    public static Pattern getHeaderPattern(String profile) {
        if (headerWssePattern == null) {
            StringBuffer sb = new StringBuffer(profile + " ");
            sb.append(userNameExpression);
            sb.append(",");
            sb.append(passwordDigestExpression);
            sb.append(",");
            sb.append(nonceExpression);
            sb.append(",");
            sb.append(parseCreatedExpression);
            headerWssePattern = Pattern.compile(sb.toString());
        }
        return headerWssePattern;
    }

    public static Pattern getHeaderTokenPattern() {
        if (headerTokenPattern == null) {
            StringBuffer sb = new StringBuffer(WSSE_HEAD);
            sb.append(reamExpression);
            sb.append(",");
            sb.append(profileExpression);
            sb.append(",");
            sb.append(typeExpression);
            headerTokenPattern = Pattern.compile(sb.toString());
        }
        return headerTokenPattern;
    }
}
