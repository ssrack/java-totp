package org.bitbyte.javatotp;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailProperties {
    private final boolean auth;
    private final String enableTLS;
    private final String hostName;
    private final String port;
    private final String sender;
    private final String recipient;
    private final String password;
}
