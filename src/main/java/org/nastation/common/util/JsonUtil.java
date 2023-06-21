package org.nastation.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author John | NaChain
 * @since 01/04/2022 22:10
 */
public class JsonUtil {

    public static ObjectMapper om = new ObjectMapper();
    public static Gson gson = new Gson();

    public static <T> T parseObjectByOm(String content, Class<T> valueType) throws Exception {
        //GsonBuilder builder = new GsonBuilder();
        //builder.registerTypeAdapter(Object.class, new MyObjectDeserializer());
        //Gson gson = builder.create();
        //Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map<String,Object>>(){}.getType(),new DataTypeAdapter()).create();
        //return gson.fromJson(content, valueType);
        return om.readValue(content.getBytes(), valueType);
    }

    public static <T> T parseObjectByGson(String content, Class<T> valueType) throws Exception {
        return gson.fromJson(content, valueType);
    }

    public static class DefaultJsonDeserializer implements JsonDeserializer<Object> {

        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            Number num = null;
            try {
                num = NumberFormat.getInstance().parse(json.getAsJsonPrimitive().getAsString());
            } catch (Exception e) {
                //ignore
            }
            if (num == null) {
                return context.deserialize(json, typeOfT);
            } else {
                return num;
            }
        }
    }

    public static String toJsonByGson(Object obj) throws Exception {
        return gson.toJson(obj);
    }

    public static String prettyByGson(String raw) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(raw).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    public static class DataTypeAdapter extends TypeAdapter<Object> {
        private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    String input = in.nextString();
                    if (input.contains(".") || input.contains("e") || input.contains("E")) {
                        return Double.parseDouble(input);
                    }
                    if (Long.parseLong(input) <= Integer.MAX_VALUE) {
                        return Integer.parseInt(input);
                    }
                    return Long.parseLong(input);
                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out, value);
        }
    }



    public static ObjectMapper om() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
    public static String toJsonByOm(Object obj) throws Exception {
        return om().writeValueAsString(obj);
    }

    public static String toPrettyJsonByOm(Object obj) throws Exception {
        return om().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static JsonNode getDataByOm(String json) throws Exception {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        JsonNode root = om().readTree(json);
        JsonNode data = root.get("data");
        return data;
    }

    public static void prettyPrintByOm(Object obj){
        try {
            System.out.println(om().writerWithDefaultPrettyPrinter().writeValueAsString(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
