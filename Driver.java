import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;


/*The purpose of the Genetic Algorithm is extremly important. This class will
define how a population will mutate (mutatePopulation), how certain populations
will cross over with one another (crossoverPopulation), and how a selection
point will be defined to begine the cross over.*/
class GeneticAlgorithm {
    public static final double RATE_OF_MUTATION = 0.25;
    public static final int SELECTION_SIZE = 3;
    public static final int POPULATION_SIZE = 10;
    public static final int NUMB_OF_ELITE_ROUTES = 1;
    public static final int NUMB_OF_GENERATIONS = 50;

    private ArrayList<City> initialRoute = null;

    public GeneticAlgorithm(ArrayList<City> initialRoute) { this.initialRoute = initialRoute; }
    public ArrayList<City> getInitialRoute() { return initialRoute; }
    public Population evolve(Population population) { return mutatePopulation(crossoverPopulation(population)); }

    Population selectPopulation(Population population) {
        Population tournamentPopulation = new Population(SELECTION_SIZE, this);
        IntStream.range(0, SELECTION_SIZE).forEach(x -> tournamentPopulation.getRoutes().set(
        	x, population.getRoutes().get((int) (Math.random() * population.getRoutes().size()))));
		tournamentPopulation.sortRoutesByFitness();
	return tournamentPopulation;
    }

    Population crossoverPopulation(Population population) {
        Population crossoverPopulation = new Population(population.getRoutes().size(), this);
        IntStream.range(0, NUMB_OF_ELITE_ROUTES).forEach(x -> crossoverPopulation.getRoutes().set(x, population.getRoutes().get(x)));
        IntStream.range(NUMB_OF_ELITE_ROUTES, crossoverPopulation.getRoutes().size()).forEach(x -> {
            Route route1 = selectPopulation(population).getRoutes().get(0);
            Route route2 = selectPopulation(population).getRoutes().get(0);
            crossoverPopulation.getRoutes().set(x, crossoverRoute(route1, route2));}
        );
        return crossoverPopulation;
    }
	/*This is an example of how the crossover function is suppose to work.
        We have two routes
        route1: [New York, San Francisco, Houston, Chicago, Boston, Austin, Seattle, Denver, Dallas, Los Angeles]
        route2: [Los Angeles, Seattle, Austin, Boston, Denver, New York, Houston, Dallas, San Francisco, Chicago]

        Because the SELECTION_SIZE is initize to 3, the index #3 is the cut off point.
        intermediate crossoverRoute: [New York, San Francisco, Houston, Chicago, Boston, null, null, null, null, null]

        Afterwards the twe routes are combined into a new route and added to the population
        final     crossoverRoute: [New York, San Francisco, Houston, Chicago, Boston, Los Angeles, Seattle, Austin, Denver, Dallas]
	*/

    Population mutatePopulation(Population population) {
        population.getRoutes().stream().filter(x -> population.getRoutes().indexOf(x) >= NUMB_OF_ELITE_ROUTES).forEach(x -> mutateRoute(x));
	return population;
    }
    /*Down below is an example of how the mutation method is suppose to work.
        route before mutation: [Boston, Denver, Conakry, Austin, New York, Seattle, Bronx, San Francisco, Dallas, Houston]
        route after mutation: [Boston, Denver, New York, Austin, Conakry, Seattle, San Francisco, Bronx, Dallas, Houston]
    */
    private Route fillCrossoverNulls(Route crossoverRoute, Route route) {
        route.getCities().stream().filter(x -> !crossoverRoute.getCities().contains(x)).forEach(cityX -> {
            for (int y = 0; y < route.getCities().size(); y++) {
                if (crossoverRoute.getCities().get(y) == null){
                    crossoverRoute.getCities().set(y, cityX);
                    break;
                }
            }
        });
        return crossoverRoute;
    }

    Route crossoverRoute(Route route1, Route route2) {
        Route crossoverRoute = new Route(this);
	Route tempRoute1 = route1;
	Route tempRoute2 = route2;
            if (Math.random() < 0.5){
		tempRoute1 = route2;
		tempRoute2 = route1;
            }
            for (int x = 0; x < crossoverRoute.getCities().size()/2; x++)
	       crossoverRoute.getCities().set(x, tempRoute1.getCities().get(x));
		return fillCrossoverNulls(crossoverRoute, tempRoute2);
    }

    Route mutateRoute(Route route) {
    	route.getCities().stream().filter(x -> Math.random() < RATE_OF_MUTATION).forEach(cityX -> {
    	int y = (int) (route.getCities().size() * Math.random());
           City cityY = route.getCities().get(y);
           route.getCities().set(route.getCities().indexOf(cityX), cityY);
           route.getCities().set(y, cityX);
        });
        return route;
    }

}

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

/*The purpose of the Population class is to create a diverse and an array
of different types of population. This class also has an important method called
the sortRoutesByFitness. This method takes two city route and returns a negetive
or positive integer*/
class Population {
	private ArrayList<Route> routes = new ArrayList<>(GeneticAlgorithm.POPULATION_SIZE);
	public Population(int populationSize, GeneticAlgorithm geneticAlgorithm) {
		IntStream.range(0, populationSize).forEach(x -> routes.add(new Route(geneticAlgorithm.getInitialRoute())));
    }
	public Population(int populationSize, ArrayList<City> cities) {
		IntStream.range(0, populationSize).forEach(x -> routes.add(new Route(cities)));
	}
	public ArrayList<Route> getRoutes() { return routes;}

        public void sortRoutesByFitness() {
		routes.sort((route1, route2) -> {
			int flag = 0;
			if (route1.getFitness() > route2.getFitness()) flag = -1;
			else if (route1.getFitness() < route2.getFitness()) flag = 1;
			return flag;
		});
	}
}

/*The purpose of the Route class is to is to define the fitness of a
particular route. getFitness is also another essential method*/
class Route {
    private boolean isFitnessChanged = true;
    private double fitness = 0;
    private ArrayList<City> cities =  new ArrayList<>();
    public Route(GeneticAlgorithm geneticAlgorithm) {
        geneticAlgorithm.getInitialRoute().forEach(x -> cities.add(null));
    }
    public Route(ArrayList<City> cities) {
	this.cities.addAll(cities);
	Collections.shuffle(this.cities);
    }
    public ArrayList<City> getCities() {
	isFitnessChanged = true;
	return cities;
    }
    public double getFitness() {
	if (isFitnessChanged == true) {
            fitness = (1/calculateTotalDistance())*10000;
            isFitnessChanged = false;
	}
	return fitness;
    }
    public double calculateTotalDistance() {
    	int citiesSize = this.cities.size();
    	return (int) (this.cities.stream().mapToDouble(x -> {
            int cityIndex = this.cities.indexOf(x);
            double returnValue = 0;
		if (cityIndex < citiesSize - 1) returnValue = x.measureDistance(this.cities.get(cityIndex + 1));
		return returnValue;
	}).sum() + this.cities.get(0).measureDistance(this.cities.get(citiesSize - 1)));
    }
    @Override
    public String toString() { return Arrays.toString(cities.toArray()); }
}

/*The purpose of the Diver class is to be the main class and test out my Genetic
Alorithms and methods. The Diver class is also where you initialize and create
City objects*/

public class Driver{

    public ArrayList<City> initialRoute = new ArrayList<>(Arrays.asList(
        new City("Conakry", 0.3642, 0.7770),
	new City("Hong Kong", 0.7185, 0.8312),
	new City("Phuket", 0.0986, 0.5891),
	new City("Toronto", 0.2954, 0.9606),
	new City("New York City", 0.5951, 0.4647),
	new City("Cairo", 0.6697, 0.7657),
	new City("London", 0.4353, 0.1709),
	new City("Paris", 0.2131,0.8349),
	new City("Burlington", 0.2131,0.8349),
	new City("Los Angles", 0.3479,0.6984),
	new City("Mexcio City", 0.4516,0.0488)
    ));
    public static ArrayList<City> readCityFile(Scanner scanner){
        String nameOfCity;
        double xCoor;
        double yCoor;
            ArrayList newCities = new ArrayList<City>();
            while(scanner.hasNextLine()){
                nameOfCity = scanner.next();
                xCoor = scanner.nextDouble();
                yCoor = scanner.nextDouble();
                City newCityInfo = new City(nameOfCity, xCoor, yCoor);
                newCities.add(newCityInfo);
            }
                return newCities;
            }
	public static void main(String[] args)throws IOException{
            //File cityCoorFile = new File("C:\\Users\\mouba\\OneDrive\\Documents\\NetBeansProjects\\Artificial Intelligence Project 1\\build\\classes\\artificial\\intelligence\\project\\pkg1\\newCities.txt");
            //Scanner readNewCities = new Scanner(cityCoorFile);

            long startTime = System.currentTimeMillis();
            Driver driver = new Driver();
            Population population = new Population(GeneticAlgorithm.POPULATION_SIZE, driver.initialRoute);
            population.sortRoutesByFitness();
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(driver.initialRoute);
            int generationNumber = 0;
            driver.printHeading(generationNumber++);
            driver.printPopulation(population);
            while (generationNumber < GeneticAlgorithm.NUMB_OF_GENERATIONS) {
            	driver.printHeading(generationNumber++);
            	population = geneticAlgorithm.evolve(population);
            	population.sortRoutesByFitness();
            	driver.printPopulation(population);
            }
            System.out.println("Best Route Found so far: " + population.getRoutes().get(0));
            System.out.println("w/ a distance of: "+String.format("%.2f",population.getRoutes().get(0).calculateTotalDistance())+ " miles");
            long endTime = System.currentTimeMillis();
            System.out.println("This search took "+ ((endTime - startTime)*.001) + " seconds to run after "+
                    GeneticAlgorithm.NUMB_OF_GENERATIONS+ " generation");
        /*
        //---------------------------------------------------------------------------

        long newStartTime = System.currentTimeMillis();
        System.out.println("these new cities are "+Driver.readCityFile(readNewCities));
        long newEndTime = System.currentTimeMillis();
        System.out.println("This search took "+ ((newEndTime - newStartTime)*.001) + " seconds to run");
        //---------------------------------------------------------------------------
	*/
        }
	public void printPopulation(Population population) {
    	population.getRoutes().forEach(x -> {
    		System.out.println(Arrays.toString(x.getCities().toArray()) + " |  "+
    				String.format("%.4f", x.getFitness()) +"   |  "+ String.format("%.2f", x.calculateTotalDistance()));
    	});
		System.out.println("");
	}
	public void printHeading(int generationNumber) {
    	System.out.println("> Generation # "+generationNumber);
    	String headingColumn1 = "Route";
    	String remainingHeadingColumns = "Fitness   | Distance (in miles)";
    	int cityNamesLength = 0;
    	for (int x = 0; x < initialRoute.size(); x++) cityNamesLength += initialRoute.get(x).getName().length();
    	int arrayLength = cityNamesLength + initialRoute.size()*2;
    	int partialLength = (arrayLength - headingColumn1.length())/2;
    	for (int x=0; x < partialLength; x++)System.out.print(" ");
    	System.out.print(headingColumn1);
    	for (int x=0; x < partialLength; x++)System.out.print(" ");
    	if ((arrayLength % 2) == 0)System.out.print(" ");
    	System.out.println(" | "+ remainingHeadingColumns);
    	cityNamesLength += remainingHeadingColumns.length() + 3;
    	for (int x=0; x < cityNamesLength+initialRoute.size()*2; x++)System.out.print("-");
    	System.out.println("");
    }
}
