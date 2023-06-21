package org.nastation.common.util;

/**
 * @author John
 * @since 07/23/2022 1:20
 */
public class StringUtil {

    public static final String escapeHTMLTag(String source) {
        if (source == null) {
            source = "";
            return source;
        }
        String newSource = source;
        newSource = newSource.trim().replaceAll("&", "&amp;");
        newSource = newSource.trim().replaceAll("<", "&lt;");
        newSource = newSource.trim().replaceAll(">", "&gt;");
        newSource = newSource.trim().replaceAll("\t", "    ");
        newSource = newSource.trim().replaceAll("\r\n", "\n");
        newSource = newSource.trim().replaceAll("\n", "<br>");
        newSource = newSource.trim().replaceAll("  ", " &nbsp;");
        newSource = newSource.trim().replaceAll("'", "&#39;");
        newSource = newSource.trim().replaceAll("\\\\", "&#92;");

        return newSource;
    }
}
