import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
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