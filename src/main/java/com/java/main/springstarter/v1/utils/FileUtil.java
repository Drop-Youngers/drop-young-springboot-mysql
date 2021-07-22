package com.java.main.springstarter.v1.utils;

import com.java.main.springstarter.v1.enums.EFileSizeType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {

    /**
     * GetFileName from a path
     * @param path File Path
     * @return String fileName
     */
    public static  String getFileNameFromFilePath(String path) {
        String fileName = new File(path).getName();
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    /**
     * Gert fileSize from a path
     * @param fileLocalPath File Local Path
     * @return long File Size
     * @throws IOException
     */
    public static long getFileSizeFromPath(String fileLocalPath) throws IOException {
        Path path = Paths.get(fileLocalPath);
        return Files.size(path);
    }

    /**
     * Get FileSizeType from fileSize
     * @param size File Size
     * @return String of FileSizedTypeEnum
     */
    public static String getFileSizeTypeFromFileSize(long size) {
        if (size >= (1024L * 1024 * 1024 * 1024))
            return EFileSizeType.TB.toString();
        else if (size >= 1024 * 1024 * 1024)
            return EFileSizeType.GB.toString();
        else if (size >= 1024 * 1024)
            return EFileSizeType.MB.toString();
        else if (size >= 1024)
            return EFileSizeType.KB.toString();
        else
            return EFileSizeType.B.toString();
    }



    /**
     * Get formatted fileSize from file Size
     * @param size File size
     * @param type FileSize type
     * @return int formattedFileSize
     */
    public static int getFormattedFileSizeFromFileSize(double size, EFileSizeType type ) {
        if (type == EFileSizeType.TB)
            return (int) (size / (1024L * 1024 * 1024 * 1024));
        else if (type == EFileSizeType.GB)
            return (int) (size / (1024 * 1024 * 1024));
        else if (type == EFileSizeType.MB)
            return (int) (size / (1024 * 1024));
        else if (type == EFileSizeType.KB)
            return (int) (size / (1024));
        else
            return (int) size;
    }

    /**
     * Get File Type From File Path
     * @param path String File Path
     * @return String fileType
     * @throws IOException
     */
    public static String getFileTypeFromFilePath(String path) throws IOException {
        return Files.probeContentType(Paths.get(path));
    }

    /**
     * Get File Extension From File
     * @param file File
     * @return String fileExtension
     */
    private static String getFileExtensionFromFile(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return "." + fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }




    /**
     * Generate Random UUID
     * @param fileName FileName
     * @return String UUID
     */
    public static String generateUUID(String fileName) {
        int period = fileName.indexOf(".");
        String prefix = fileName.substring(0, period);
        String suffix = fileName.substring(period);

        return prefix + "-" +  UUID.randomUUID().toString().replace("-", "")  + suffix;
    }



}
