package proj.w41k4z.helpers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

import proj.w41k4z.helpers.annotation.TemporalPattern;

/**
 * The {@code DateHelper} class is used to provide helper methods for temporal
 * operations.
 * 
 * @see proj.w41k4z.helpers.annotation.TemporalPattern
 */
public class DateHelper {

    /**
     * Returns the supported temporal patterns for a given temporal element.
     * 
     * @param temporalElement The date field to get the supported date patterns
     *                        from.
     * @return The supported temporal patterns for the given date field or the
     *         default
     *         temporal format if the element is not annotated
     *         with @TemporalPattern.
     * 
     * @throws NoSuchMethodException     if the annotated element has no getType
     *                                   method.
     * @throws IllegalAccessException    if the getType method is not accessible.
     * @throws InvocationTargetException if the getType method throws an exception.
     */
    public static String[] getSupportedPatterns(AnnotatedElement temporalElement)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (temporalElement.isAnnotationPresent(TemporalPattern.class)) {
            return temporalElement.getAnnotation(TemporalPattern.class).value();
        }
        return new String[] {
                getValidTemporalFormat(
                        (Class<?>) temporalElement.getClass().getMethod("getType").invoke(temporalElement)) };
    }

    /**
     * Parse a given string temporal object to the specified pattern.
     * 
     * @param type    the type of the temporal object to format.
     * @param date    the string temporal object.
     * @param pattern the temporal pattern for parsing.
     * @return a new formatted java.util.Date object.
     * @throws ParseException            if the given temporal object is not
     *                                   compatible with
     *                                   the given temporal pattern.
     * @throws InvocationTargetException if the valueOf method throws an exception
     */
    public static java.util.Date format(Class<?> type, String date, String pattern)
            throws ParseException, InvocationTargetException {
        // Getting the java.util.Date object from the string temporal object
        java.text.SimpleDateFormat sourceFormat = new java.text.SimpleDateFormat(pattern);
        java.util.Date utilDate = sourceFormat.parse(date);

        // Formatting the java.util.Date object to the specified temporal pattern
        String validPattern = getValidTemporalFormat(type);
        java.text.SimpleDateFormat targetFormat = new java.text.SimpleDateFormat(validPattern);
        String formattedDateExpression = targetFormat.format(utilDate);
        try {
            Method valueOf = type.getMethod("valueOf", String.class);
            return (java.util.Date) type.cast(valueOf.invoke(type, formattedDateExpression));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The given type is not a date type");
        } catch (IllegalAccessException e) {
            /*
             * This exception will never be thrown because
             * an IllegalArgumentException will be thrown if
             * the type is not a date type (Specifically, the method valueOf
             * is only accessible for java.sql.Date, java.sql.Time and
             * java.sql.Timestamp)
             */
            return null;
        }
    }

    /**
     * Returns the temporal valid format according to the provided type
     * 
     * @param type The temporal type
     * @return The format for the specified temporal type
     */
    public static String getValidTemporalFormat(Class<?> type) {
        type = type.isArray() ? type.getComponentType() : type;
        switch (type.getSimpleName()) {
            case "Time":
                return "HH:mm:ss";
            case "Date":
                return "yyyy-MM-dd";
            case "Timestamp":
                return "yyyy-MM-dd HH:mm:ss";
            default:
                throw new IllegalArgumentException(
                        "The `" + type.getSimpleName() + "` type is not supported. This only supports java.util.Date");
        }
    }

    /**
     * Returns the current date
     * 
     * @return the current date
     */
    public static java.sql.Date getCurrentDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }
}
