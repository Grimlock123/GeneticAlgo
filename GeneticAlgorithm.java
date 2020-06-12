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