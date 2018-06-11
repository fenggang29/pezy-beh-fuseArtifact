package hive_jdbc_handler;

import java.util.Collections;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by 冯刚 on 2017/11/3.
 */
public class StringUtils {

    public static List<String> tokenize(String string)
    {
        return tokenize(string, ",");
    }

    public static List<String> tokenize(String string, String delimiters)
    {
        return tokenize(string, delimiters, true, true);
    }

    public static List<String> tokenize(String string, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
    {
        if (!hasText(string)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(string, delimiters);
        List<String> tokens = new ArrayList();
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if ((!ignoreEmptyTokens) || (token.length() > 0)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static boolean hasLength(CharSequence sequence)
    {
        return (sequence != null) && (sequence.length() > 0);
    }

    public static boolean hasText(CharSequence sequence)
    {
        if (!hasLength(sequence)) {
            return false;
        }
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(sequence.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
