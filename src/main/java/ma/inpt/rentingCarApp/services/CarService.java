package ma.inpt.rentingCarApp.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ma.inpt.rentingCarApp.DAO.CarRepository;
import ma.inpt.rentingCarApp.DAO.UserRepository;
import ma.inpt.rentingCarApp.entities.Car;
import ma.inpt.rentingCarApp.entities.User;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    // class attributes :
    final CarRepository carRepository;
    final UserRepository usRepo;

    // class constructor :
    public CarService(CarRepository carRepository, UserRepository usRepo) {
        this.carRepository = carRepository;
        this.usRepo = usRepo;
    }

    // class methods :
    public void save(Car car) {
        carRepository.save(car);
    }

    public void saveById(Long carId) {
        carRepository.save(carRepository.findById(carId).get());
    }

    public List<Car> findAll() {
        return (List<Car>) carRepository.findAll();
    }

    public Car findById(long carId) {
        return carRepository.findById(carId).get();
    }

    public List<Car> searchCars(String carName, String owner) {

        List<Car> searchedCars = new ArrayList<>();

        if (carName != null && owner == null) {
            searchedCars = getByCarName(carName);
        } else if (carName == null && owner != null) {
            searchedCars = getByOwner(owner);
        } else if (carName != null) {
            searchedCars = getByCarNameAndOwner(carName, owner);
        }
        return searchedCars;
    }

    public List<Car> getByCarName(String carName) {
        List<Car> cars = new ArrayList<>();
        for (Car car : carRepository.findAll()) {
            if (car.getCarName().toLowerCase().contains(carName.toLowerCase())) {
                cars.add(car);
            }
        }
        return cars;
    }

    public List<Car> getByOwner(String owner) {
        List<Car> cars = new ArrayList<>();
        for (Car car : carRepository.findAll()) {
            if (car.getOwner().toLowerCase().contains(owner.toLowerCase())) {
                cars.add(car);
            }
        }
        return cars;
    }

    public List<Car> getByCarNameAndOwner(String carName, String owner) {
        List<Car> cars = new ArrayList<>();
        for (Car car : carRepository.findAll()) {
            if (car.getCarName().toLowerCase().contains(carName.toLowerCase()) &&
                    car.getOwner().toLowerCase().contains(owner.toLowerCase())) {
                cars.add(car);
            }
        }
        return cars;
    }

    public void deleteById(long carId) {
        carRepository.deleteById(carId);
    }

    public List<Car> getUnprocessedCarReservations() {
        List<Car> unprocessedCarReservations = new ArrayList<Car>();
        for (Car car : carRepository.findAll()) {
            if (car.getReservedByUser() != null && !car.getReadyForPickUp()) {
                unprocessedCarReservations.add(car);
            }
        }
        return unprocessedCarReservations;
    }

    public List<Car> getProcessedCarReservations() {
        List<Car> processedCarReservations = new ArrayList<Car>();
        for (Car car : carRepository.findAll()) {
            if (car.getReservedByUser() != null && car.getReadyForPickUp()) {
                processedCarReservations.add(car);
            }
        }
        return processedCarReservations;
    }

    public List<Car> convertIdsCollectionToCarsList(Collection<Long> carIds) {
        List<Car> cars = new ArrayList<Car>();
        for (Long carId : carIds) cars.add(carRepository.findById(carId).get());
        return cars;
    }

    public void removeCurrentUserOfMultipleCars(List<Car> cars) {
        for (Car car : cars) removeCurrentUserOfCar(car);
    }

    public void removeCurrentUserOfCar(Car car) {
        User currentUser = car.getTheUser();
        for (int i = 0; i < currentUser.getCars().size(); i++) {
            if (currentUser.getCars().get(i).getCarId() == car.getCarId()) {
                currentUser.getCars().remove(i);
                break;
            }
        }
        usRepo.save(currentUser);
        car.setTheUser(null);
        car.setReturnDate(null);
        car.setTimesExtended(0);
        carRepository.save(car);
    }

    public void removeReservation(Car car) {
        User reservedByUser = car.getReservedByUser();
        for (int i = 0; i < reservedByUser.getReversedCars().size(); i++) {
            if (reservedByUser.getReversedCars().get(i).getCarId() == car.getCarId()) {
                reservedByUser.getReversedCars().remove(i);
                break;
            }
        }
        usRepo.save(reservedByUser);
        car.setStartReservationDate(null);
        car.setEndReservationDate(null);
        car.setReadyForPickup(false);
        carRepository.save(car);
    }

    public void saveCarOrder(Collection<Long> selectedCarIds, User user) {
        for (Long carId : selectedCarIds) {
            Car car = carRepository.findById(carId).get();
            car.setReturnDate(LocalDate.now().plusDays(20));
            car.setStartReservationDate(null);
            car.setEndReservationDate(null);
            car.setReservedByUser(null);
            
            car.setReadyForPickup(false);
            car.setTheUser(user);
            carRepository.save(car);
            usRepo.save(user);
        }
    }
}
