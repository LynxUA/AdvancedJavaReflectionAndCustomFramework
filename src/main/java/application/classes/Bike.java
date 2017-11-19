package application.classes;

import framework.core.annotations.Autowiring;
import framework.core.annotations.Component;
import framework.core.annotations.Repository;
import framework.core.annotations.Service;

/**
 * Created by denysburlakov on 05.11.2017.
 */
@Component
public class Bike implements Transport {
    private Car car;

    @Override
    public void getTransport() {
        System.out.println("I'm a bike");
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
