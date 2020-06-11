package io.github.ssrack.javatotp;

import lombok.Builder;
import lombok.Getter;

/**
 * This pojo is used to pass the email properties to the method {@link io.github.ssrack.javatotp.EmailUtility#sendBarCodeToEmail(EmailProperties, String)}
 *
 * All the properties {@link #auth}, {@link #enableTLS}, {@link #hostName}, {@link #port}, {@link #sender}, {@link #recipient} except {@link #password} are required. {@link #password} is only required when {@link #auth is true}
 *
 * @author saiteja77
 * @since 0.0.1.Release
 */
@Builder
@Getter
public class EmailProperties {

    /**
     * shall be true if you want to setup the authentication, else false
     */
    private final boolean auth;

    /**
     * shall be "true" if you want to communicate with the host in a secure channel, else false
     */
    private final String enableTLS;

    /**
     * should have the email server's hostname
     */
    private final String hostName;

    /**
     * should have the port number on which the email server is listening to
     */
    private final String port;

    /**
     * should have the email address of the sender
     */
    private final String sender;

    /**
     * should have the email address of the recipient
     */
    private final String recipient;

    /**
     * Optional, not needed if {@link #auth} is set to false. Should have the password of the sender in the given {@link #hostName}.
     */
    private String password;
}
