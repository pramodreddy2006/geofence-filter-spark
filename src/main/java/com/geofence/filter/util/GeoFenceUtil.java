package com.geofence.filter.util;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geofence.filter.entity.Circle;
import com.geofence.filter.entity.GeoFence;
import com.geofence.filter.entity.Point;
import com.geofence.filter.entity.Polygon;
import com.geofence.filter.entity.Rectangle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import ch.hsr.geohash.GeoHash;

public class GeoFenceUtil {

	public static Map<String, List<GeoFence>> getGeoHashFenceMap(List<GeoFence> geoFences, int geoHashSize)
			throws Exception {
		Map<String, List<GeoFence>> geoHashMap = new HashMap<String, List<GeoFence>>();
		for (GeoFence geoFence : geoFences) {
			Point point = geoFence.getCentroid();
			GeoHash geoHash = GeoHash.withCharacterPrecision(point.getLatitude(), point.getLongitude(), geoHashSize);
			GeoHash[] adjacent = geoHash.getAdjacent();
			String[] geoHashes = new String[9];
			geoHashes[0] = geoHash.toBase32();
			for (int i = 1; i < 9; i++) {
				geoHashes[i] = adjacent[i - 1].toBase32();
			}
			for (String geo : geoHashes) {
				if (!geoHashMap.containsKey(geo)) {
					geoHashMap.put(geo, new ArrayList<GeoFence>());
				}
				geoHashMap.get(geo).add(geoFence);
			}
		}
		return geoHashMap;
	}

	public static List<GeoFence> loadGeoFences(String geoFencePath) throws Exception {
		List<GeoFence> geoFences = new ArrayList<GeoFence>();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(geoFencePath));
		JsonArray geoJsonArray = gson.fromJson(reader, JsonArray.class);
		for (JsonElement geoJson : geoJsonArray) {
			JsonObject geo = geoJson.getAsJsonObject();
			String geoType = geo.get("geoType").getAsString();
			JsonElement geoFenceJson = geo.get("geoFence");
			if ("RECTANGLE".equals(geoType)) {
				GeoFence geoFence = gson.fromJson(geoFenceJson, Rectangle.class);
				geoFences.add(geoFence);
			} else if ("CIRCLE".equals(geoType)) {
				GeoFence geoFence = gson.fromJson(geoFenceJson, Circle.class);
				geoFences.add(geoFence);
			} else if ("POLYGON".equals(geoType)) {
				GeoFence geoFence = gson.fromJson(geoFenceJson, Polygon.class);
				geoFences.add(geoFence);
			} else {
				throw new Exception("Invalid geo fence type");
			}
		}
		return geoFences;
	}

	public static int getGeoHashSize(List<GeoFence> geoFences) throws Exception {
		double max = 0.0;
		for (GeoFence geoFence : geoFences) {
			double dist = geoFence.getMaxDistanceFromCentroid();
			max = Math.max(max, dist);
		}
		if (max < 120.0) {
			return 7;
		} else if (max < 500.0) {
			return 6;
		} else if (max < 4000.0) {
			return 5;
		} else if (max < 17000.0) {
			return 4;
		} else {
			return 3;
		}

	}

}
