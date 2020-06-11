package io.github.ssrack.javatotp;

import lombok.Builder;
import lombok.Getter;

/**
 * This pojo is used to pass the email properties to the method {@link io.github.ssrack.javatotp.EmailUtility#sendBarCodeToEmail(EmailProperties, String)}
 *
 * {@link #auth} shall be true if you want to setup the authentication, else false
 * {@link #enableTLS} shall be "true" if you want to communicate with the host in a secure channel, else false
 * {@link #hostName} should have the email server's hostname
 * {@link #port} should have the port number on which the email server is listening to
 * {@link #sender} should have the email address of the sender
 * {@link #recipient} should have the email address of the reciepient
 * Optional, not needed if {@link #auth is set to false}. {@link #password} of the sender in the given {@link #hostName}.
 *
 * @author saiteja77
 */
@Builder
@Getter
public class EmailProperties {

    private final boolean auth;
    private final String enableTLS;
    private final String hostName;
    private final String port;
    private final String sender;
    private final String recipient;
    private String password;
}
