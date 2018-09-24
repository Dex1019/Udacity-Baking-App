package com.example.dex.letsbake.utils;

import java.util.Arrays;


public class Utils {

    private static final String[] videoExt = {"mp4"};
    private static final String[] imageExt = {"jpg", "jpeg", "png"};
    private static final String[] singularMeasures = {"K", "G", "OZ"};


    public static boolean isVideo(String file) {
        String fileExt = getFileExt(file);

        return Arrays.asList(videoExt).contains(fileExt);
    }

    public static boolean isImage(String file) {
        String fileExt = getFileExt(file);

        return Arrays.asList(imageExt).contains(fileExt);
    }

    private static String getFileExt(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }

    public static double isSingularMeasure(double quantity, String measure) {
        if (quantity > 1 && Arrays.asList(singularMeasures).contains(measure)) {
            return 1;
        }
        return quantity;
    }
}