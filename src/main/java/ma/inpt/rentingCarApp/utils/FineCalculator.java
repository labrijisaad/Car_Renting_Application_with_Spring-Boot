package ma.inpt.rentingCarApp.utils;

import ma.inpt.rentingCarApp.entities.Car;
import ma.inpt.rentingCarApp.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class FineCalculator {

    // class attributes :
    final DateTracker dateTracker;
    BigDecimal fineAmount = BigDecimal.valueOf(10);

    // class constructor :
    public FineCalculator(DateTracker dateTracker) {
        this.dateTracker = dateTracker;
    }

    // class methods :
    public BigDecimal getFineOfCar(Car car) {
        long daysTooLate = dateTracker.daysTooLate(car.getReturnDate());
        return fineAmount.multiply(BigDecimal.valueOf(daysTooLate));
    }

    // class methods :
    public LinkedHashMap<User, BigDecimal> getAllUsersWithFines(List<User> users) {     //Returns a map of all users with the amount of fines in $.
        LinkedHashMap<User, BigDecimal> allUsersWithFines = new LinkedHashMap<>();

        for (User user : users) {
            if (user.getCars().size() == 0) {
                allUsersWithFines.put(user, BigDecimal.valueOf(0.00));
            } else {
                long totalDaysTooLate = 0;
                for (Car car : user.getCars()) {
                    long daysTooLate = dateTracker.differenceInDays(car.getReturnDate());
                    if (daysTooLate > 0) totalDaysTooLate += daysTooLate;
                }
                BigDecimal totalDaysTooLateInBD = new BigDecimal(totalDaysTooLate);
                BigDecimal totalFineInDollar = fineAmount.multiply(totalDaysTooLateInBD);
                allUsersWithFines.put(user, totalFineInDollar);
            }
        }
        return allUsersWithFines;
    }

    public LinkedHashMap<Car, BigDecimal> getCarsWithFines(List<Car> cars) {     //Returns a map of cars with the amount of fines in $.
        LinkedHashMap<Car, BigDecimal> carsWithFines = new LinkedHashMap<>();
        for (Car car : cars) {
            long daysTooLate = dateTracker.differenceInDays(car.getReturnDate());
            if (daysTooLate > 0) {
                BigDecimal daysTooLateInBD = new BigDecimal(daysTooLate);
                BigDecimal totalFineInDollar = fineAmount.multiply(daysTooLateInBD);
                carsWithFines.put(car, totalFineInDollar);
            } else {
                BigDecimal noFine = BigDecimal.valueOf(0.00);
                carsWithFines.put(car, noFine);
            }
        }
        return carsWithFines;
    }

    public LinkedHashMap<Car, BigDecimal> selectCarsWithFines(List<Car> cars) {     //Returns a map containing only cars that have a fine > $0.00.
        LinkedHashMap<Car, BigDecimal> carsWithFines = new LinkedHashMap<>();
        for (Car car : cars) {
            long daysTooLate = dateTracker.differenceInDays(car.getReturnDate());
            if (daysTooLate > 0) {
                BigDecimal daysTooLateInBD = new BigDecimal(daysTooLate);
                BigDecimal totalFineInDollar = fineAmount.multiply(daysTooLateInBD);
                carsWithFines.put(car, totalFineInDollar);
            }
        }
        return carsWithFines;
    }

    public boolean hasFineOrNot(User user) {
        boolean hasFine = false;
        for (Car car : user.getCars()) {
            if (car.getReturnDate().compareTo(LocalDate.now()) < 0) {
                hasFine = true;
            }
        }
        return hasFine;
    }

    public BigDecimal getTotalFine(List<Car> cars) {
        BigDecimal totalFine = BigDecimal.valueOf(0.0);
        for (Car car : cars) {
            long daysTooLate = dateTracker.differenceInDays(car.getReturnDate());
            if (daysTooLate > 0) {
                BigDecimal daysTooLateInBD = new BigDecimal(daysTooLate);
                BigDecimal fineOfCar = fineAmount.multiply(daysTooLateInBD);
                totalFine = totalFine.add(fineOfCar);
            }
        }
        return totalFine;
    }
}
