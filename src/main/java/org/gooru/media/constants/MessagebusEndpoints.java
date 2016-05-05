package org.gooru.media.constants;

/**
 * It contains the definition for the "Message Bus End Points" which are
 * addresses on which the consumers are listening.
 */
public final class MessagebusEndpoints {
    public static final String MBEP_AUTH = "org.gooru.nucleus.media.message.bus.auth";
    public static final String MBEP_METRICS = "org.gooru.nucleus.media.message.bus.metrics";
    public static final String MBEP_CONVERTER = "org.gooru.nucleus.media.message.bus.converter";

    private MessagebusEndpoints() {
        throw new AssertionError();
    }
}
