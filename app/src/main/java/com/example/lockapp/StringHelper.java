package com.example.lockapp;

public class StringHelper {
    public static boolean isEqual(String s1, String s2)
    {
        // If one of them is null, ...
        if ((s1 == null) || (s2 == null))
        {
            // - All of them are null => Equal
            // - Only one of them is null => Not Equal
            return (s1 == null) && (s2 == null);
        }

        return s1.equals(s2);
    }

    public static boolean isNullOrEmpty(String s)
    {
        return (s == null) || s.isEmpty();
    }

    public static String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(pos);
    }

    public static String getFileNameWithoutExtension(String fileName){
        String item = "";
        if (!StringHelper.isNullOrEmpty(fileName)){
            item = fileName.substring(0,fileName.lastIndexOf("."));
        }
        return item;
    }
}
