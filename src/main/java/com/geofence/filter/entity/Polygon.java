package com.geofence.filter.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;

public class Polygon implements GeoFence, Serializable {

	private static final long serialVersionUID = 6380451561816239829L;
	
	private List<Point> points;

	public Polygon(List<Point> points) {
		this.points = points;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	public boolean contains(Point point) {

		int hits = 0;
		int npoints = points.size();
		Double lastx = points.get(npoints - 1).getLatitude();
		Double lasty = points.get(npoints - 1).getLongitude();
		Double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
			curx = points.get(i).getLatitude();
			cury = points.get(i).getLongitude();

			if (cury == lasty) {
				continue;
			}

			Double leftx;
			if (curx < lastx) {
				if (point.getLatitude() >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (point.getLatitude() >= curx) {
					continue;
				}
				leftx = lastx;
			}

			Double test1, test2;
			if (cury < lasty) {
				if (point.getLongitude() < cury || point.getLongitude() >= lasty) {
					continue;
				}
				if (point.getLatitude() < leftx) {
					hits++;
					continue;
				}
				test1 = point.getLatitude() - curx;
				test2 = point.getLongitude() - cury;
			} else {
				if (point.getLongitude() < lasty || point.getLongitude() >= cury) {
					continue;
				}
				if (point.getLatitude() < leftx) {
					hits++;
					continue;
				}
				test1 = point.getLatitude() - lastx;
				test2 = point.getLongitude() - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}

		return ((hits & 1) != 0);
	}

	@Override
	public Point getCentroid() {
		double lat = 0.0;
		double lon = 0.0;
		for (Point p : points) {
			lat += p.getLatitude();
			lon += p.getLongitude();
		}
		lat = lat / points.size();
		lon = lon / points.size();
		return new Point(lon, lat);
	}

	@Override
	public Double getMaxDistanceFromCentroid() {
		Point centroid = getCentroid();
		double max = 0.0;
		for (Point point : points) {
			double cur = distance(centroid, point);
			max = Math.max(max, cur);
		}
		return max;
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
