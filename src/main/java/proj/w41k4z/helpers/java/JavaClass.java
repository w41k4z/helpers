package proj.w41k4z.helpers.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import proj.w41k4z.helpers.DateHelper;
import proj.w41k4z.helpers.StringHelper;

/**
 * The {@code JavaClass} class is used to provide some useful methods for java
 * class manipulation.
 */
public class JavaClass {

    /**
     * Returns all methods annotated with the given annotation class(es).
     * 
     * @param javaClass         the java class.
     * @param annotationClasses the annotation class(es).
     * @return all methods annotated with the given annotation class(es).
     */
    @SafeVarargs
    public static Method[] getMethodByAnnotation(Class<?> javaClass, Class<? extends Annotation>... annotationClasses) {
        List<Method> methods = Arrays.stream(javaClass.getMethods())
                .filter(method -> Arrays.stream(annotationClasses).anyMatch(method::isAnnotationPresent))
                .collect(Collectors.toList());
        return methods.toArray(new Method[0]);
    }

    /**
     * Returns all fields annotated with the given annotation class(es).
     * 
     * @param javaClass         the java class.
     * @param annotationClasses the annotation class(es).
     * @return all fields annotated with the given annotation class(es).
     */
    @SafeVarargs
    public static Field[] getFieldByAnnotation(Class<?> javaClass, Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = Arrays.stream(javaClass.getDeclaredFields())
                .filter(field -> Arrays.stream(annotationClasses).anyMatch(field::isAnnotationPresent))
                .collect(Collectors.toList());
        return fields.toArray(new Field[0]);
    }

    /**
     * Sets a field value for the given target object using its setter. Supports up
     * to an array of 1 dimension.
     * 
     * @param object the object to set the field value from.
     * @param data   the parameter value for the field setter.
     * @param field  the field to set the value for.
     * 
     * @throws NoSuchMethodException     if the field has no setter (following the
     *                                   java naming convention)
     * @throws InvocationTargetException if the setter throws an exception
     * @throws IllegalAccessException    if the setter is not public
     * @throws InstantiationException    if the field cannot be instantiated
     * @throws IllegalArgumentException  if the setter is not public
     */
    public static void setObjectFieldValue(Object object, Object data, Field field)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException,
            InstantiationException {
        if (data == null) {
            throw new NullPointerException("The data to set cannot be null. Field: " + field.getName());
        }
        if (data.getClass().isArray() && data.getClass().getComponentType().isArray()) {
            throw new IllegalArgumentException("Multidimensional arrays are not supported");
        }
        Class<?> fieldClass = field.getType().isArray() ? field.getType().getComponentType()
                : field.getType();
        Method setter = object.getClass().getMethod(StringHelper.toCamelCase("set", field.getName()),
                field.getType());
        // if the field is a date type
        if (java.util.Date.class.isAssignableFrom(fieldClass)) {
            setTemporalFieldValue(object, data, field, setter);
        } else {
            setNormalFieldValue(object, data, field, setter);
        }

    }

    /**
     * Returns the value of the given field for the given object.
     * 
     * @param object the object to get the field value from.
     * @param field  the field to get the value for.
     * @return the value of the given field for the given object.
     * @throws SecurityException         if the field is not accessible.
     * @throws NoSuchMethodException     if the field has no getter (following the
     *                                   java naming convention)
     * @throws InvocationTargetException if the getter throws an exception
     * @throws IllegalArgumentException  if the getter is not public
     * @throws IllegalAccessException    if the getter is not public
     */
    public static Object getObjectFieldValue(Object object, Field field) throws NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getter = object.getClass().getMethod(StringHelper.toCamelCase("get", field.getName()));
        return getter.invoke(object);
    }

    private static void setNormalFieldValue(Object object, Object data, Field field, Method setter)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
            SecurityException {
        Object castedData = null;
        if (field.getType().isArray()) {
            Object[] array = (Object[]) data;
            Object[] castedArray = (Object[]) Array.newInstance(field.getType().getComponentType(), array.length);
            for (int i = 0; i < array.length; i++) {
                try {
                    // Basic type : INTEGER, STRING, BOOLEAN, DOUBLE, FLOAT, LONG, SHORT, BYTE
                    castedArray[i] = field.getType().getComponentType().getConstructor(String.class)
                            .newInstance(array[i].toString());
                } catch (NoSuchMethodException e) {
                    // This means that the data is not an array of primitive type.
                    castedArray[i] = field.getType().getComponentType().cast(array[i]);
                }
            }
            castedData = castedArray;
        } else {
            try {
                // Basic type : INTEGER, STRING, BOOLEAN, DOUBLE, FLOAT, LONG, SHORT, BYTE
                castedData = field.getType().getConstructor(String.class).newInstance(data.toString());
            } catch (Exception e) {
                // Object type
                castedData = data;
            }
        }
        setter.invoke(object, castedData);
    }

    private static void setTemporalFieldValue(Object object, Object data, Field field, Method setter)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object castedData = null;
        String[] temporalFormats = DateHelper.getSupportedPatterns(field);
        if (field.getType().isArray()) {
            Object[] array = (Object[]) data;
            Object[] castedArray = (Object[]) Array.newInstance(field.getType().getComponentType(), array.length);
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < temporalFormats.length; j++) {
                    try {
                        castedArray[i] = DateHelper.format(field.getType().getComponentType(),
                                array[j].toString().trim(),
                                temporalFormats[j]);
                        break;
                    } catch (ParseException e) {
                        if (j == temporalFormats.length - 1) {
                            throw new IllegalArgumentException("The temporal format is not supported");
                        }
                    }
                }
            }
            castedData = castedArray;
        } else {
            for (int i = 0; i < temporalFormats.length; i++) {
                try {
                    castedData = DateHelper.format(field.getType().getComponentType(), data.toString().trim(),
                            temporalFormats[i]);
                    break;
                } catch (ParseException e) {
                    if (i == temporalFormats.length - 1) {
                        throw new IllegalArgumentException("The date format is not supported");
                    }
                }
            }
        }
        setter.invoke(object, castedData);
    }
}
