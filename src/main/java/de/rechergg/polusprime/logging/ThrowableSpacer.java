package de.rechergg.polusprime.logging;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

/**
 * Throwable spacer converter for Log4j2 patterns
 */
@Plugin(name = "ThrowableSpacer", category = PatternConverter.CATEGORY)
@ConverterKeys({"ts"})
public class ThrowableSpacer extends ThrowablePatternConverter {

    /**
     * Private constructor
     */
    protected ThrowableSpacer(String[] options) {
        super("ThrowableSpacer", "throwable", options);
    }

    /**
     * Gets an instance of the converter
     */
    public static ThrowableSpacer newInstance(final String[] options) {
        return new ThrowableSpacer(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        super.format(event, toAppendTo);
        if (event.getThrown() != null) {
            toAppendTo.append(System.lineSeparator());
        }
    }
}