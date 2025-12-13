package cobblemonquestsextended.cobblemon_quests_extended.logger;

import cobblemonquestsextended.cobblemon_quests_extended.config.CobblemonQuestsConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;

public class CobblemonQuestsLogger {

    private static final Logger logger = Logger.getLogger(MOD_ID);

    public void log(Level level, String message) {
        logger.log(level, message);
    }

    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs an info message with format arguments.
     *
     * @param message the message with {} placeholders
     * @param args    the arguments to replace placeholders
     */
    public void info(String message, Object... args) {
        logger.info(formatMessage(message, args));
    }

    public void warning(String message) {
        if (!CobblemonQuestsConfig.suppressWarnings) {
            logger.warning(message);
        }
    }

    /**
     * Logs a warning message. Alias for warning() to match common logging conventions.
     *
     * @param message the warning message
     */
    public void warn(String message) {
        warning(message);
    }

    /**
     * Logs a warning message with format arguments.
     *
     * @param message the message with {} placeholders
     * @param args    the arguments to replace placeholders
     */
    public void warn(String message, Object... args) {
        if (!CobblemonQuestsConfig.suppressWarnings) {
            logger.warning(formatMessage(message, args));
        }
    }

    /**
     * Logs a debug message. Only logged when debug mode is enabled.
     *
     * @param message the debug message
     */
    public void debug(String message) {
        logger.fine(message);
    }

    /**
     * Logs a debug message with format arguments.
     *
     * @param message the message with {} placeholders
     * @param args    the arguments to replace placeholders
     */
    public void debug(String message, Object... args) {
        logger.fine(formatMessage(message, args));
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     */
    public void error(String message) {
        logger.severe(message);
    }

    /**
     * Logs an error message with format arguments.
     *
     * @param message the message with {} placeholders
     * @param args    the arguments to replace placeholders
     */
    public void error(String message, Object... args) {
        logger.severe(formatMessage(message, args));
    }

    /**
     * Formats a message by replacing {} placeholders with arguments.
     * Similar to SLF4J-style formatting.
     *
     * @param message the message template
     * @param args    the arguments to insert
     * @return the formatted message
     */
    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < message.length()) {
            if (i < message.length() - 1 && message.charAt(i) == '{' && message.charAt(i + 1) == '}') {
                if (argIndex < args.length) {
                    result.append(args[argIndex++]);
                } else {
                    result.append("{}");
                }
                i += 2;
            } else {
                result.append(message.charAt(i));
                i++;
            }
        }

        return result.toString();
    }
}
