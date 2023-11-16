package proj.w41k4z.helpers.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code TemporalPattern} annotation helps indicating supported temporal
 * patterns for the {@code proj.w41k4z.helpers.DateHelper} class
 * This mainly concerns the {@code java.util.Date} class such as
 * {@code java.sql.Date}, {@code java.sql.Time}, {@code java.sql.Timestamp}.
 * Parsing a temporal type from a string is done using
 * {@code java.util.Date.valueOf(String)} (all its subclass has this method)
 * but this method requires a specific temporal format
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TemporalPattern {
    /**
     * List of all the supported patterns for the annotated element ().
     * 
     * @return the date pattern(s) to be supported
     */
    String[] value();
}
