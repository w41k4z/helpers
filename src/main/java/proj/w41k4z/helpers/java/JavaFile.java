package proj.w41k4z.helpers.java;

import java.io.File;

import proj.w41k4z.helpers.exception.JavaFileException;

/**
 * The {@code JavaFile} class is used to represent a Java file.
 */
public class JavaFile {

    private File javaFile;

    /**
     * Default constructor.
     */
    public JavaFile() {
    }

    /**
     * Constructor with the file parameter.
     * 
     * @param file the file to be set.
     * @throws Exception if the file is not a Java file.
     */
    public JavaFile(File file) throws Exception {
        this.setJavaFile(file);
    }

    /**
     * This method sets the Java file.
     * 
     * @param file the file to be set.
     * @throws JavaFileException if the file is not a Java file.
     */
    public void setJavaFile(File file) throws JavaFileException {
        if (!isJavaFile(file))
            throw new JavaFileException();
        this.javaFile = file;
    }

    /**
     * Returns the java File object field attached to this object.
     * 
     * @return the Java file.
     */
    public File getJavaFile() {
        return this.javaFile;
    }

    /**
     * Check if the target file is a java file
     * 
     * @param file the file to check.
     * @return true if the file is a Java file, false otherwise.
     */
    public boolean isJavaFile(File file) {
        return file.getName().toLowerCase().endsWith(".java") || file.getName().toLowerCase().endsWith(".class");
    }

    /**
     * Get the Class object from the java file of this object.
     * 
     * 
     * @param pathToRemove the path to remove from the file path. This is used to
     *                     remove the path to the src folder because the file path
     *                     is an absolute path.
     * @return the class object.
     * @throws ClassNotFoundException if the class is not found due to the
     *                                pathToRemove parameter.
     */
    public Class<?> getClassObject(String pathToRemove) throws ClassNotFoundException {
        String path = this.getJavaFile().getPath().replace(pathToRemove, "").replace("/", ".");
        return Class.forName(path.substring(0, path.length() - 6));
    }
}
