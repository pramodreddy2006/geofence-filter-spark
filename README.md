# geofence-filter-spark
Spark job to filter the events efficiently within provided Geo fences


## How it works
- Filters the geo based data efficiently using [GeoHash](https://en.wikipedia.org/wiki/Geohash)
- Initially it reads all the GeoFences and gets all possible GeoHashes where the events can fall under our GeoFences. GeoHash size will be based on biggest GeoFence.
- It generates a mapping of GeoHashes to the GeoFences in which the event might have occured. From provided input events, if the GeoHash of event has equivalent GeoFence in the map then it would validate the event if it occured in the GeoFence. 
- This reduces the evaluation time, as we only check the events that occur in vicinity of each GeoFence provided.


## Execution
**Execute:** *mvn clean install*

Pick the jar from *target/geofence-filter.jar*

### Executing the job and filtering the geo data 
#### Spark
- *spark-submit <args> --class com.geofence.filter.GeoFilterJob <path: geofence-filter.jar> <path: input> <path: geoFence.json> <path: output>*
#### Local
- *java -jar <path: geofence-filter.jar> <path: input> <path: geoFence.json> <path: output>*


### input
Sample input.csv is available in this project
id,timestamp,latitude,longitude


### geoFence.json
Sample geoFence.json is available in this project