package proj.w41k4z.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used to help with file operations.
 */
public class FileHelper {

    /**
     * This method is used to create a new file.
     * 
     * @param root        The root directory.
     * @param fileName    The name of the file.
     * @param isDirectory True if the file is a directory, false otherwise.
     * @return The created file.
     * @throws IOException If an I/O error occurs
     */
    public static File createNewFile(
            File root,
            String fileName,
            boolean isDirectory) throws IOException {
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("The first parameter has to be a directory");
        }
        File file = new File(root, fileName);
        if (isDirectory) {
            file.mkdir();
        } else {
            file.createNewFile();
        }
        return file;
    }

    /**
     * This method is used to clear the content of a file.
     * 
     * @param file The file to clear.
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException If the file is not found.
     */
    public static void clearFile(File file) throws FileNotFoundException, IOException {
        new FileOutputStream(file).close();
    }

    /**
     * This method is used to delete a file and all its subfile if directory.
     * 
     * @param file The file to delete.
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteFile(children[i]);
            }
        }
        file.delete();
    }

    /**
     * This method is used to get all the subfiles of a file.
     * 
     * @param file The file to get the subfiles from.
     * @return The subfiles of the file.
     */
    public static File[] getSubFiles(File file) {
        ArrayList<File> files = new ArrayList<>();
        if (file.isFile()) {
            files.add(file);
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                for (File grandChildren : getSubFiles(children[i])) {
                    files.add(grandChildren);
                }
            }
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * This method is used to get the extension of a file.
     * 
     * @param file The file to get the extension from.
     * @return The extension of the file.
     */
    public static String getFileExtension(File file) {
        String[] splittedName = file.getName().split("\\.");
        return splittedName[splittedName.length - 1];
    }
}
