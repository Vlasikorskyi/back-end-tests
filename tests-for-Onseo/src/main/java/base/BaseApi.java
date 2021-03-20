package base;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static base.APIConst.RESPONSE;
import static base.APIConst.RESPONSE_CODE;
import static base.Utils.setParametersFieldsAndValues;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class BaseApi {
    private static final Logger logger = LoggerFactory.getLogger(BaseApi.class);

    protected List<NameValuePair> objectParams = null;
    private final JSONObject emptyJSON = new JSONObject("{\"status\": \"success\",  \"data\": \"any type value\"}");


    private JSONObject getResponseFromPostRequest(Client httpClient, JSONObject object, String path) {
        HttpResponse response = httpClient.makePostRequest(path, object);
        return getFullResponse(response);
    }

    private JSONObject getResponseFromGetRequest(Client httpClient, String path) {
        HttpResponse response = httpClient.makeGetRequest(path);
        return getFullResponse(response);
    }

    private JSONObject getResponseFromDeleteRequest(Client httpClient, String path) {
        HttpResponse response = httpClient.makeDelRequest(path);
        return getFullResponse(response);
    }

    private JSONObject getFullResponse(HttpResponse response) {
        int responseCode = getResponseCode(response);
        logger.info("Response Code : " + responseCode);

        String res = getJsonFromRequest(response);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESPONSE, res);
        jsonObject.put(RESPONSE_CODE, responseCode);
        return jsonObject;
    }

    private int getResponseCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    protected static String getJsonFromRequest(HttpResponse response) {
        StringBuilder result = new StringBuilder();

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            logger.error("IOException, read entities from Response ", e);
        }

        String res = result.toString();
        logger.info("Response Body " + res);
        return res;
    }

    public JSONObject getResponseFromPostRequest(JSONObject object, String path) {
        objectParams = null;
        Client httpClient = new Client();
        setObjectParams(object);
        logger.info("make POST request...");
        return getResponseFromPostRequest(httpClient, object, path);
    }

    private void setObjectParams(JSONObject object) {
        if (object != null) {
            ArrayList<String> builder = new ArrayList<>();
            if (object.names() != null) {
                for (int i = 0; i < object.names().length(); i++) {
                    String objectNames = object.names().getString(i);
                    builder.add(objectNames);

                    Object objectParams = object.get(objectNames);

                    if (objectParams instanceof JSONObject ||
                            objectParams instanceof JSONArray ||
                            objectParams instanceof String ||
                            objectParams instanceof Boolean ||
                            objectParams instanceof Integer ||
                            objectParams instanceof Long) {
                        builder.add(objectParams.toString());

                    } else {
                        if (objectParams == JSONObject.NULL) {
                            builder.add(null);
                        } else {
                            builder.add(Arrays.toString((String[]) objectParams));
                        }
                    }
                }
            }
            objectParams = setParametersFieldsAndValues(builder.toArray(new String[0]));
        }
    }

    public JSONObject postData(String path, JSONObject object) {
        JSONObject jsonObject = getResponseFromPostRequest(object, path);
        String res = jsonObject.getString(RESPONSE);
        int responseCode = jsonObject.getInt(RESPONSE_CODE);
        logger.info("Response Code : " + responseCode);

        return assertJsonFormat(responseCode, res);
    }

    public JSONObject getData(String path) {
        objectParams = null;
        Client httpClient = new Client();
        JSONObject jsonObject = getResponseFromGetRequest(httpClient, path);
        String res = jsonObject.getString(APIConst.RESPONSE);
        int responseCode = jsonObject.getInt(APIConst.RESPONSE_CODE);
        logger.info("Response Code : " + responseCode);

        return assertJsonFormat(responseCode, res);
    }

    public JSONObject deleteData(String path) {
        objectParams = null;
        Client httpClient = new Client();
        JSONObject jsonObject = getResponseFromDeleteRequest(httpClient, path);
        String res = jsonObject.getString(APIConst.RESPONSE);
        int responseCode = jsonObject.getInt(APIConst.RESPONSE_CODE);
        logger.info("Response Code : " + responseCode);

        return assertJsonFormat(responseCode, res);
    }

    public JSONObject putData(String path, JSONObject object) {
        Client httpClient = new Client();
        setObjectParams(object);
        HttpResponse response = httpClient.makePutRequest(path, object);
        int responseCode = getResponseCode(response);
        logger.info("Response Code : " + responseCode);
        String res = getJsonFromRequest(response);

        return assertJsonFormat(responseCode, res);
    }

    private JSONObject assertJsonFormat(int responseCode, String res) {
        JSONObject resultInJson = new JSONObject();
        int statusCode = 200;
        String responseFormat = "Response format isn't correct. ";

        try {
            assertOur(responseFormat + "Data field is empty", res.length() != 0);
            resultInJson = new JSONObject(res);

            if (responseCode != statusCode && responseCode != 201) {
                assertOur("Response isn't correct. Response code isn't " + statusCode + ", but was " + responseCode, resultInJson, is(equalTo(emptyJSON)));
            }
        } catch (Exception e) {
            logger.error("Test failed because: " + e.getMessage());

            if (responseCode >= 500) {
                System.out.println("500 !!!!!!!!!!!!!!!!!!!!!!!");
                check500Errors(responseCode, res);
            }
        }

        return resultInJson;
    }

    protected <T> void assertOur(String reason, T actual, Matcher<? super T> matcher) {
        StringBuilder builder = new StringBuilder();

        if (objectParams != null) {
            builder.append("Object Params :");
            builder.append(System.getProperty("line.separator"));
            for (NameValuePair pair : objectParams) {
                builder.append("\t").append(pair.getName()).append(" = ").append(pair.getValue());
                builder.append(System.getProperty("line.separator"));
            }
        }

        if (!matcher.matches(actual)) {
            StringDescription description = new StringDescription();

            description.appendText(reason + builder).appendText("\nExpected: ").appendDescriptionOf(matcher).appendText("\n     but: ");
            matcher.describeMismatch(actual, description);

            throw new AssertionError(description.toString());
        }
    }

    protected void assertOur(String reason, boolean assertion) {
        StringBuilder builder = new StringBuilder();

        if (objectParams != null) {
            builder.append("Object Params : ");
            builder.append(System.getProperty("line.separator"));
            for (NameValuePair pair : objectParams) {
                builder.append("\t").append(pair.getName()).append(" = ").append(pair.getValue());
                builder.append(System.getProperty("line.separator"));
            }
        }

        if (!assertion) {
            throw new AssertionError(reason + builder);
        }
    }

    private void check500Errors(int responseCode, String res) {
        if (res.contains("504 Gateway Time-out") || res.contains("502 Bad Gateway")) {
            String result = res.split("<title>")[1].split("</title>")[0];
            assertOur("Response is not JSON \n" + "Response Code : " + responseCode + "\n", result, is(equalTo(emptyJSON)));
        }

        // add response data log because we had error with JSON in code below, but it's not reproducible
        System.out.println("Response data: " + res);

        JSONObject object;
        if (res.startsWith("{")) {
            object = new JSONObject(res);
        } else {
            object = new JSONObject("{\"message\": \"" + res + "\"}");
        }

        assertOur("Response is not JSON \n" + "Response Code : " + responseCode + "\n", object, is(equalTo(emptyJSON)));
    }
}
