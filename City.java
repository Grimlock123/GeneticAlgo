import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
/*The purpose of the City class is define the cities that the travelling
salesman will visit. The information that will be given to the City constructor
is the name of the city, the longitude (X-Coordinates), and the latitude
(Y-Coordinates). When we create a list of cities and run the Geneitic Search
Algorithms, it show the list of names instead of the X and Y Coordinates for
simplicity. The method that will be included will be getters for all instance
veriables, an overriden toString method that will display the information of any
object created by the City class, And MOST IMPORTANTLY the measureDistance method.
The measureDistance method is the most important medthod in this class because it
calculates the distance between 2 Cities.*/
class City {
    private static final double EARTH_EQUATORIAL_RADIUS = 6378.1370D;
    private static final double CONVERT_DEGREES_TO_RADIANS = Math.PI/180D;
    private static final double CONVERT_KM_TO_MILES = 0.621371;
	 private final double longitude;
	 private final double latitude;
	 private final String name;
	 public City(String name, double latitude, double longitude) {
            this.name = name;
	    this.longitude = longitude * CONVERT_DEGREES_TO_RADIANS;
	    this.latitude = latitude * CONVERT_DEGREES_TO_RADIANS;
	 }
	 public String getName() {return name;}
	 public double getLongitude() {return this.longitude;}
	 public double getLatitude() {return this.latitude;}
        @Override
	 public String toString(){return getName();}
	 public double measureDistance(City city) {
            double deltaLongitude = city.getLongitude() - this.getLongitude();
	    double deltaLatitude = city.getLatitude() - this.getLatitude();
	    double a = Math.pow(Math.sin(deltaLatitude / 2D), 2D) +
      		   Math.cos(this.getLatitude()) * Math.cos(city.getLatitude()) * Math.pow(Math.sin(deltaLongitude / 2D), 2D);
	    return CONVERT_KM_TO_MILES * EARTH_EQUATORIAL_RADIUS * 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
	 }
}