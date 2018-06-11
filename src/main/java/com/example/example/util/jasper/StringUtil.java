package com.example.example.util.jasper;

import java.util.LinkedList;

/**
 * Created by carlos on 07/07/17.
 */
public class StringUtil {

    public static LinkedList<String> splitCamelCaseString(String s){
        LinkedList<String> result = new LinkedList<String>();
        for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            result.add(w);
        }
        return result;
    }

    public static String formatPropertyName(String name)
    {
        String resultado = "";
        for (String w : name.split("(?<!(^|[A-Z0-9]))(?=[A-Z0-9])|(?<!^)(?=[A-Z0-9][a-z])")) {
            w = w.substring(0, 1).toUpperCase() + w.substring(1);
            resultado = resultado.compareTo("")==0?
                    resultado + w:
                    resultado + " " + w;
        }
        return resultado;
    }
}
