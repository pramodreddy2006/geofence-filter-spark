package com.geofence.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import com.geofence.filter.entity.GeoFence;
import com.geofence.filter.entity.Point;
import com.geofence.filter.util.GeoFenceUtil;

import ch.hsr.geohash.GeoHash;

public class GeoFilterJob {

	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			System.out.println("Invalid arguments");
			System.out.println("Usage : ");
			System.out.println("java -jar geofence-filter.jar <input.csv> <geoFence.json> <output>");
		}

		String inputPath = args[0];
		String geoFencePath = args[1];
		String outputPath = args[2];

		SparkSession spark = null;
		List<GeoFence> geoFences = GeoFenceUtil.loadGeoFences(geoFencePath);
		final int geoHashSize = GeoFenceUtil.getGeoHashSize(geoFences);
		final Map<String, List<GeoFence>> geoHashFenceMap = GeoFenceUtil.getGeoHashFenceMap(geoFences, geoHashSize);

		// Set this to false if running in cluster.
		boolean local = true;
		if (local) {
			spark = SparkSession.builder().master("local").appName("GeoFilterJob").getOrCreate();
		} else {
			spark = SparkSession.builder().appName("GeoFilterJob").getOrCreate();
		}

		/**
		 * For connecting to S3
		 * 
		 * spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key",s3accesskey);
		 * spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key",s3secret);
		 * spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint",s3Endpoint);
		 * 
		 */

		Dataset<Row> csvDataset = spark.read().csv(inputPath);

		JavaRDD<Row> eventsRDD = csvDataset.javaRDD().flatMap(new FlatMapFunction<Row, Row>() {

			private static final long serialVersionUID = 1047134437837373623L;

			@Override
			public Iterator<Row> call(Row row) throws Exception {
				List<Row> returnItem = new ArrayList<Row>();
				Double lat = new Double(row.getString(2));
				Double lon = new Double(row.getString(3));
				String geoHash = GeoHash.withCharacterPrecision(lat, lon, geoHashSize).toBase32();
				if (geoHashFenceMap.containsKey(geoHash)) {
					List<GeoFence> fences = geoHashFenceMap.get(geoHash);
					for (GeoFence fence : fences) {
						if (fence.contains(new Point(lat, lon))) {
							returnItem.add(row);
							break;
						}
					}
				}
				return returnItem.iterator();
			}
		});

		Dataset<Row> geoFenceDF = spark.createDataFrame(eventsRDD, csvDataset.schema());
		geoFenceDF.write().mode(SaveMode.Overwrite).csv(outputPath);

		if (spark != null) {
			spark.stop();
		}
	}

}
