package ma.inpt.rentingCarApp.controllers;

import ma.inpt.rentingCarApp.entities.Car;
import ma.inpt.rentingCarApp.entities.User;
import ma.inpt.rentingCarApp.security.CurrentUserFinder;
import ma.inpt.rentingCarApp.services.CarService;
import ma.inpt.rentingCarApp.services.UserService;
import ma.inpt.rentingCarApp.utils.DateTracker;
import ma.inpt.rentingCarApp.utils.FineCalculator;
import ma.inpt.rentingCarApp.utils.ListInStringConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    // class attributes :
    final UserService usService;
    final CarService carService;
    final CurrentUserFinder currentUserFinder;
    final FineCalculator fineCalculator;
    final DateTracker dateTracker;
    final ListInStringConverter listConverter;

    // class constructor :
    public UserController(UserService usService, CarService carService, CurrentUserFinder currentUserFinder, FineCalculator fineCalculator, DateTracker dateTracker, ListInStringConverter listConverter) {
        this.usService = usService;
        this.carService = carService;
        this.currentUserFinder = currentUserFinder;
        this.fineCalculator = fineCalculator;
        this.dateTracker = dateTracker;
        this.listConverter = listConverter;
    }

    // class methods :
    @GetMapping
    public String userHome(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        model.addAttribute("carsWithFines", fineCalculator.selectCarsWithFines(currentUser.getCars()));
        model.addAttribute("currentUser", currentUser);
        return "user/user-home.html";
    }

    @GetMapping(value = "/yourcars")
    public String yourCars(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        List<Car> cars = currentUser.getCars();
        LinkedHashMap<Car, BigDecimal> carsWithFines = fineCalculator.getCarsWithFines(cars);
        model.addAttribute("cars", carsWithFines);
        return "user/user-your-cars.html";
    }

    @PutMapping(value = "/yourcars/extend")
    public String extendRequest(@RequestParam BigDecimal fineAmount,
                                @RequestParam Long carId,
                                @RequestParam int weeksToExtend) {

        Car car = carService.findById(carId);
        int maximumWeeksToExtend = 3;
        int extensionsLeft = maximumWeeksToExtend - car.getTimesExtended();
        long daysTooLate = dateTracker.daysTooLate(car.getReturnDate());

        if (car.getTimesExtended() < maximumWeeksToExtend && fineAmount.compareTo(BigDecimal.valueOf(0)) == 0 && car.getReservedByUser() == null) {
            car.setReturnDate(car.getReturnDate().plusDays(7L * weeksToExtend));
            car.setTimesExtended(car.getTimesExtended() + weeksToExtend);
            carService.save(car);
            return "redirect:/user/yourcars/carextended";

        } else if (fineAmount.compareTo(BigDecimal.valueOf(0)) > 0 && daysTooLate <= (extensionsLeft * 7L) && car.getReservedByUser() == null) {
            return "redirect:/user/yourcars/payfine/" + carId;

        } else {
            return "redirect:/user/yourcars/carcannotbeextended";

        }
    }

    @GetMapping(value = "/yourcars/payfine/{carId}")
    public String payFine(Model model, @PathVariable(value = "carId") Long carId) {

        Car car = carService.findById(carId);
        BigDecimal fine = fineCalculator.getFineOfCar(car);
        int weeksToExtend = dateTracker.getWeeksToExtendReturnDate(car);

        model.addAttribute("weeksToExtend", weeksToExtend);
        model.addAttribute("fine", fine);
        model.addAttribute("car", car);

        return "user/user-pay-fine.html";
    }

    @PostMapping(value = "/yourcars/dopayment")
    public String doPayment(@RequestParam int weeksToExtend,
                            @RequestParam BigDecimal fineAmount,
                            @RequestParam long carId,
                            Model model) {
        Car currentCar = carService.findById(carId);
        model.addAttribute("fineAmount", fineAmount);
        model.addAttribute("weeksToExtend", weeksToExtend);
        model.addAttribute("car", currentCar);
        return "user/user-do-payment.html";
    }

    @GetMapping(value = "/yourcars/carextended")
    public String carExtended() {
        return "user/user-car-extended.html";
    }

    @GetMapping(value = "/yourcars/carcannotbeextended")
    public String carCanNotBeExtended() {
        return "user/user-car-can-not-be-extended.html";
    }

    @GetMapping(value = "/browsecars")
    public String browseCars(@RequestParam(required = false) String carNme,
                             @RequestParam(required = false) String owner,
                             @RequestParam(required = false) String showAllCars,
                             @RequestParam(required = false) Long reservedCarId,
                             @RequestParam(required = false) Long removeCarId,
                             @RequestParam(required = false) String reservedCarIdsInString,
                             Model model) {

        Set<Long> reservedCarIds = new LinkedHashSet<>();
        if (reservedCarIdsInString != null)
            reservedCarIds = listConverter.convertListInStringToSetInLong(reservedCarIdsInString);
        if (removeCarId != null) reservedCarIds.remove(removeCarId);
        if (reservedCarId != null) reservedCarIds.add(reservedCarId);

        Map<Car, String> listedCarReservations = dateTracker.listedCarReservations(reservedCarIds);

        List<Car> cars;
        if (showAllCars == null) cars = carService.searchCars(carNme, owner);
        else cars = carService.findAll();

        model.addAttribute("userHasFine", fineCalculator.hasFineOrNot(currentUserFinder.getCurrentUser()));
        model.addAttribute("listedCarReservations", listedCarReservations);
        model.addAttribute("reservedCarIds", reservedCarIds);
        model.addAttribute("carName", carNme);
        model.addAttribute("owner", owner);
        model.addAttribute("showAllCars", showAllCars);
        model.addAttribute("cars", cars);
        return "user/user-browse-cars.html";
    }

    @GetMapping(value = "/FAQ")
    public String FAQ() {
        return "user/user-FAQ.html";
    }


    @PutMapping(value = "/browsecars/payreservation")
    public String payReservation(@RequestParam String reservedCarIdsInString,
                                 @RequestParam Double amountToPay,
                                 Model model) {

        model.addAttribute("amountToPay", amountToPay);
        model.addAttribute("reservedCarIdsInString", reservedCarIdsInString);
        return "user/user-pay-reservation.html";
    }

    @PutMapping(value = "browsecars/savereservation")
    public String saveCarReservations(@RequestParam String reservedCarIdsInString) {
        Set<Long> reservedCarIds = listConverter.convertListInStringToSetInLong(reservedCarIdsInString);
        dateTracker.setReservationDatesAndReservedByCurrentUserForMultipleCars(reservedCarIds);
        return "redirect:/user/yourreservations";
    }

    @GetMapping(value = "/yourreservations")
    public String yourReservations(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        model.addAttribute("reservedCars", currentUser.getReversedCars());
        return "user/user-your-reservations.html";
    }
}
