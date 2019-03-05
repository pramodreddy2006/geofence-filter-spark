package com.geofence.filter.entity;

import java.io.Serializable;

import com.google.gson.Gson;

public class Rectangle implements GeoFence, Serializable {

	private static final long serialVersionUID = -4749019721883039791L;

	private Point topLeft;

	private Point bottomRight;

	public Rectangle(Point topLeft, Point bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Point topLeft) {
		this.topLeft = topLeft;
	}

	public Point getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(Point bottomRight) {
		this.bottomRight = bottomRight;
	}

	@Override
	public boolean contains(Point point) {
		if (point.getLatitude() < bottomRight.getLatitude() || point.getLatitude() > topLeft.getLatitude()) {
			return false;
		} else if (point.getLongitude() < topLeft.getLongitude() || point.getLongitude() > bottomRight.getLongitude()) {
			return false;
		}
		return true;
	}

	@Override
	public Point getCentroid() {
		double lat = (topLeft.getLatitude() + bottomRight.getLatitude()) / 2;
		double lon = (topLeft.getLongitude() + bottomRight.getLongitude()) / 2;
		return new Point(lon, lat);
	}

	@Override
	public Double getMaxDistanceFromCentroid() {
		return distance(getCentroid(), topLeft);
	}

	private double distance(Point point1, Point point2) {
		double lat1 = point1.getLatitude();
		double lat2 = point2.getLatitude();
		double lon1 = point1.getLongitude();
		double lon2 = point2.getLongitude();
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
