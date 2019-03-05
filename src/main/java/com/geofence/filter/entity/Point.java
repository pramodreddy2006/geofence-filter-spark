package com.geofence.filter.entity;

import java.io.Serializable;

import com.google.gson.Gson;

public class Point implements Serializable {

	private static final long serialVersionUID = -7473707865672954404L;

	private Double latitude;

	private Double longitude;

	public Point(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
