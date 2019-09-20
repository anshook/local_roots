package com.udacity.ak.localroots.data.service;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.udacity.ak.localroots.data.database.MarketDetail;

import java.lang.reflect.Type;
import java.util.Iterator;

public class MarketDetailDeserializer implements JsonDeserializer<MarketDetail> {
    private static String N_DETAILS = "marketdetails";

    @Override
    public MarketDetail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        JsonObject detailsJson = jsonObject.getAsJsonObject(N_DETAILS);

        MarketDetail detailObject = new Gson().fromJson(detailsJson, MarketDetail.class);
        return detailObject;
    }
}

