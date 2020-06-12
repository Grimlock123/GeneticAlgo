import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
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