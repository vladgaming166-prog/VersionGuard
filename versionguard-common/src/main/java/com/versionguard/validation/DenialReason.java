package com.versionguard.validation;

/**
 * Why a client was refused. Used for logging and commands, not player-facing copy.
 */
public enum DenialReason {
    BELOW_MINIMUM("below the minimum supported version"),
    ABOVE_MAXIMUM("above the maximum supported version"),
    SNAPSHOT("snapshot or development client"),
    EXPERIMENTAL("experimental or unofficial client"),
    UNKNOWN_PROTOCOL("unknown protocol"),
    DETECTION_FAILED("version detection failed");

    private final String description;

    DenialReason(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
