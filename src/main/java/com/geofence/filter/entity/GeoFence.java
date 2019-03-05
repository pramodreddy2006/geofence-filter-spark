package com.geofence.filter.entity;

public interface GeoFence {

	public boolean contains(Point point);

	public Point getCentroid();

	public Double getMaxDistanceFromCentroid();

}
