package base;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
    public static List<NameValuePair> setParametersFieldsAndValues(String... values) {
        List<NameValuePair> urlParameters = new ArrayList<>();
        for (int i = 0; i < values.length; i += 2) {
            urlParameters.add(new BasicNameValuePair(values[i], values[i + 1]));
        }
        return urlParameters;
    }

    public static Stream<JSONObject> getJSONArrayStream(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), false).map(JSONObject.class::cast);
    }

    public static String getCurrentDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }
}
