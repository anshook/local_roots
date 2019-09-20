package com.udacity.ak.localroots.data.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.udacity.ak.localroots.data.database.Market;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MarketListDeserializer implements JsonDeserializer<List<Market>> {
    private static String N_RESULTS = "results";

    @Override
    public List<Market> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        JsonArray marketListJson = jsonObject.getAsJsonArray(N_RESULTS);

        List<Market> marketList = new Gson().fromJson(marketListJson, typeOfT);

        return marketList;

    }
}
