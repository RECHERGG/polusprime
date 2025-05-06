package de.rechergg.polusprime;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PolusPrime {

    public void initialize() {
        log.trace("Dies ist eine TRACE-Nachricht");
        log.debug("Dies ist eine DEBUG-Nachricht");
        log.info("Dies ist eine INFO-Nachricht");
        log.warn("Dies ist eine WARN-Nachricht");
        log.error("Dies ist eine ERROR-Nachricht");
        log.fatal("Dies ist eine FATAL-Nachricht");
    }

}
