package base;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static base.Utils.setParametersFieldsAndValues;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public HttpResponse makeGetRequest(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        logger.info("URL : " + url);

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.error("IOExceptions in makeGetResponse method.", e);
        }

        return response;
    }

    public HttpResponse makePostRequest(String url, List<NameValuePair> params) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        logger.info("URL : " + url);

        try {
            if (params == null || params.isEmpty()) {
                request.setEntity(new UrlEncodedFormEntity(setParametersFieldsAndValues("", "")));
            } else {
                request.setEntity(new UrlEncodedFormEntity(params));
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("IOException set Entities to Request", e);
        }

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.error("IOExceptions in makePostResponse method with params.", e);
        }

        return response;
    }

    public HttpResponse makePostRequest(String url, JSONObject object) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        logger.info("URL : " + url);

        if (object != null) {
            StringEntity input = new StringEntity(object.toString(), StandardCharsets.UTF_8);
            input.setContentType("application/json;charset=UTF-8");
            input.setContentEncoding(new BasicHeader(CONTENT_TYPE, "application/json;charset=UTF-8"));
            request.setEntity(input);
        }

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.error("IOExceptions in makePostResponse method with params.", e);
        }

        return response;
    }

    public HttpResponse makePutRequest(String url, JSONObject object) {
        return makePutRequest(url, object, null);
    }

    public HttpResponse makePutRequest(String url, JSONArray array) {
        return makePutRequest(url, null, array);
    }

    private HttpResponse makePutRequest(String url, JSONObject object, JSONArray array) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(url);
        logger.info("URL : " + url);

        if (object != null || array != null) {
            StringEntity input = new StringEntity(object == null ? array.toString() : object.toString(), StandardCharsets.UTF_8);
            input.setContentType("application/json;charset=UTF-8");
            input.setContentEncoding(new BasicHeader(CONTENT_TYPE, "application/json;charset=UTF-8"));
            request.setEntity(input);
        }

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.error("IOExceptions in makePostResponse method with params.", e);
        }

        return response;
    }

    public HttpResponse makeDelRequest(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete request = new HttpDelete(url);
        logger.info("URL : " + url);

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            logger.error("IOExceptions in makePostResponse method with params.", e);
        }

        return response;
    }
}
