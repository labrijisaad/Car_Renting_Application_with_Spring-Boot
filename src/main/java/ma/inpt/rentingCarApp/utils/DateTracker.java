package ma.inpt.rentingCarApp.utils;

import ma.inpt.rentingCarApp.entities.Car;
import ma.inpt.rentingCarApp.security.CurrentUserFinder;
import ma.inpt.rentingCarApp.services.CarService;
import ma.inpt.rentingCarApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DateTracker {

    private final LocalDate now = LocalDate.now();
    @Autowired
    CarService carService;
    @Autowired
    CurrentUserFinder currentUserFinder;
    @Autowired
    UserService usService;
    @Autowired
    DateTracker dateTracker;

    public LocalDate getNow() {
        return now;
    }

    public long differenceInDays(LocalDate date) {
        return ChronoUnit.DAYS.between(date, LocalDate.now());
    }

    public long daysTooLate(LocalDate date) {
        long dayDifference = ChronoUnit.DAYS.between(date, LocalDate.now());
        if (dayDifference > 0) return dayDifference;
        else return 0;
    }

    public String getReservationDatesInString(Car car) {

        String reservationDatesInString = "";

        LocalDate firstAvailableDate;

        if (car.getReturnDate() == null) {
            reservationDatesInString += LocalDate.now() + "   /   ";
            firstAvailableDate = LocalDate.now();
        } else {
            if (car.getReturnDate().compareTo(LocalDate.now()) < 0) {
                reservationDatesInString += LocalDate.now() + "   /   ";
                firstAvailableDate = LocalDate.now();
            } else {
                reservationDatesInString += car.getReturnDate().plusDays(1) + "   /   ";
                firstAvailableDate = car.getReturnDate().plusDays(1);
            }
        }
        reservationDatesInString += firstAvailableDate.plusDays(7).toString();

        return reservationDatesInString;
    }

    public void setReservationDatesAndReservedByCurrentUserForMultipleCars(Collection<Long> carIds) {
        for (Long carId : carIds) setCarReservationDatesAndReservedByCurrentUser(carId);
    }

    public void setCarReservationDatesAndReservedByCurrentUser(Long carId) {
        Car car = carService.findById(carId);
        LocalDate startReservationDate;

        if (car.getReturnDate() == null) {
            startReservationDate = LocalDate.now();
        } else {
            if (car.getReturnDate().compareTo(LocalDate.now()) < 0) {
                startReservationDate = LocalDate.now();
            } else {
                startReservationDate = car.getReturnDate().plusDays(1);
            }
        }

        LocalDate endReservationDate = startReservationDate.plusDays(7);
        car.setStartReservationDate(startReservationDate);
        car.setEndReservationDate(endReservationDate);
        car.setReservedByUser(usService.findById(currentUserFinder.getCurrentUserId()));
        carService.save(car);
        usService.save(currentUserFinder.getCurrentUser());
    }

    public int getWeeksToExtendReturnDate(Car car) {
        long daysTooLate = dateTracker.daysTooLate(car.getReturnDate());

        int weeksToExtend;
        if (daysTooLate <= 7) weeksToExtend = 1;
        else if (daysTooLate <= 14) weeksToExtend = 2;
        else weeksToExtend = 3;

        return weeksToExtend;
    }

    public Map<Car, String> listedCarReservations(Collection<Long> carIds) {

        Map<Car, String> listedCarReservations = new LinkedHashMap<>();
        for (Long carId : carIds) {
            Car reservedCarObject = carService.findById(carId);
            String reservationDates = dateTracker.getReservationDatesInString(reservedCarObject);
            listedCarReservations.put(reservedCarObject, reservationDates);
        }
        return listedCarReservations;
    }
}
