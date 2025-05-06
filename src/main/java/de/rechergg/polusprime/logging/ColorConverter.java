package de.rechergg.polusprime.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Color converter for Log4j2 patterns
 */
@Plugin(name = "ColorConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"color"})
public class ColorConverter extends LogEventPatternConverter {
    private static final Map<Integer, AnsiColor> LEVEL_COLORS;

    static {
        LEVEL_COLORS = new HashMap<>();
        LEVEL_COLORS.put(Level.ERROR.intLevel(), AnsiColor.RED);
        LEVEL_COLORS.put(Level.WARN.intLevel(), AnsiColor.YELLOW);
        LEVEL_COLORS.put(Level.INFO.intLevel(), AnsiColor.GREEN);
        LEVEL_COLORS.put(Level.DEBUG.intLevel(), AnsiColor.BLUE);
        LEVEL_COLORS.put(Level.TRACE.intLevel(), AnsiColor.BLACK);
    }

    private final String option;

    /**
     * Private constructor
     */
    protected ColorConverter(String name, String style, String[] options) {
        super(name, style);
        this.option = options.length > 0 ? options[0] : null;
    }

    /**
     * Gets an instance of the converter
     */
    public static ColorConverter newInstance(final String[] options) {
        return new ColorConverter("color", "color", options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        String input = toAppendTo.toString();
        toAppendTo.setLength(0);

        if (option != null) {
            AnsiColor ansiColor = AnsiColor.fromName(option);
            if (ansiColor != null) {
                toAppendTo.append(ansiColor.colorize(input));
                return;
            }
        }

        int level = event.getLevel().intLevel();
        AnsiColor levelColor = LEVEL_COLORS.getOrDefault(level, AnsiColor.WHITE);
        toAppendTo.append(levelColor.colorize(input));
    }
}