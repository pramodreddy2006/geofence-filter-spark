package com.geofence.filter.entity;

import java.io.Serializable;

import com.google.gson.Gson;

public class Circle implements GeoFence, Serializable {

	private static final long serialVersionUID = -5128210068443788154L;

	private Point center;

	private Double radius;

	public Circle(Point center, Double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	@Override
	public Point getCentroid() {
		return center;
	}

	@Override
	public Double getMaxDistanceFromCentroid() {
		return radius;
	}

	@Override
	public boolean contains(Point point) {
		double distance = distance(point);
		if (distance > radius) {
			return false;
		}
		return true;
	}

	private double distance(Point point) {
		double lat1 = this.center.getLatitude();
		double lat2 = point.getLatitude();
		double lon1 = this.center.getLongitude();
		double lon2 = point.getLongitude();
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515 * 1609.344;
		return dist;
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
