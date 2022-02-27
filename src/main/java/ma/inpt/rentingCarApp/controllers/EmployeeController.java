package ma.inpt.rentingCarApp.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ma.inpt.rentingCarApp.entities.Car;
import ma.inpt.rentingCarApp.entities.Notification;
import ma.inpt.rentingCarApp.entities.User;
import ma.inpt.rentingCarApp.security.CurrentUserFinder;
import ma.inpt.rentingCarApp.utils.FineCalculator;
import ma.inpt.rentingCarApp.utils.ListInStringConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ma.inpt.rentingCarApp.services.CarService;
import ma.inpt.rentingCarApp.services.NotificationService;
import ma.inpt.rentingCarApp.services.UserService;

@Controller
@RequestMapping(value = "/employee")
public class EmployeeController {

    // class attributes :
    final UserService userService;
    final CarService carService;
    final CurrentUserFinder currentUserFinder;
    final NotificationService notifyService;
    final FineCalculator fineCalculator;
    final ListInStringConverter listConverter;

    // class constructors :
    public EmployeeController(UserService userService, CarService carService, CurrentUserFinder currentUserFinder, NotificationService notifService, FineCalculator fineCalculator, ListInStringConverter listConverter) {
        this.userService = userService;
        this.carService = carService;
        this.currentUserFinder = currentUserFinder;
        this.notifyService = notifService;
        this.fineCalculator = fineCalculator;
        this.listConverter = listConverter;
    }

    // class methods :
    @GetMapping
    public String employeeHomePage(Model model) {
        long currentUserId = currentUserFinder.getCurrentUserId();
        User currentUser = userService.findById(currentUserId);
        model.addAttribute("currentUser", currentUser);
        return "employee/employee-home.html";
    }

    @GetMapping(value = "/users/showusers")
    public String showUsers(Model model,
                            @RequestParam(required = false) String firstName,
                            @RequestParam(required = false) String lastName,
                            @RequestParam(required = false) String showAllUsers) {

        List<User> users = new ArrayList<>();
        LinkedHashMap<User, BigDecimal> usersAndFines;

        if (showAllUsers != null) users = userService.findAll();
        else if (firstName != null || lastName != null) users = userService.userSearcher(firstName, lastName);

        usersAndFines = fineCalculator.getAllUsersWithFines(users);
        model.addAttribute("usersWithFines", usersAndFines);
        return "employee/employee-show-users.html";
    }

    @GetMapping(value = "/users/showuserinfo")
    public String showUserInfo(@RequestParam Long userId,
                               @RequestParam BigDecimal fine,
                               Model model) {
        User user = userService.findById(userId);
        model.addAttribute("carsInUse", fineCalculator.getCarsWithFines(user.getCars()));
        model.addAttribute("fine", fine);
        model.addAttribute("user", user);
        return "employee/employee-show-user-info.html";
    }

    @GetMapping(value = "/cars")
    public String cars() {
        return "employee/employee-cars.html";
    }

    @GetMapping(value = "/cars/showcars")
    public String showCars(Model model,
                           @RequestParam(required = false) String carName,
                           @RequestParam(required = false) String owner,
                           @RequestParam(required = false) String showAllCars) {

        List<Car> cars;
        if (showAllCars == null) cars = carService.searchCars(carName, owner);
        else cars = carService.findAll();

        model.addAttribute("cars", cars);
        return "employee/employee-show-cars.html";
    }

    @GetMapping(value = "/cars/newcar")
    public String newCar(Model model) {
        model.addAttribute("car", new Car());
        return "employee/employee-new-car.html";
    }

    @PostMapping(value = "/cars/save")
    public String saveCar(Car car) {
        carService.save(car);
        return "redirect:/employee/cars/carsaved";
    }

    @GetMapping(value = "/cars/carsaved")
    public String carSaved() {
        return "employee/employee-car-saved.html";
    }

    @GetMapping(value = "/cars/areyousuretodeletecar")
    public String areYouSureToDeleteCar(@RequestParam Long deleteCarId, Model model) {
        Car car = carService.findById(deleteCarId);
        model.addAttribute("deleteCar", car);
        return "employee/employee-delete-car.html";
    }

    @DeleteMapping(value = "/cars/deletecar")
    public String deleteCar(@RequestParam Long deleteCarId) {
        carService.deleteById(deleteCarId);
        return "redirect:/employee/cars/cardeleted";
    }

    @GetMapping(value = "/cars/cardeleted")
    public String carDeleted() {
        return "employee/employee-car-deleted.html";
    }

    @GetMapping(value = "/cars/changecarinfo")
    public String changeCarInfo(@RequestParam Long changeCarId, Model model) {
        Car car = carService.findById(changeCarId);
        model.addAttribute("car", car);
        return "employee/employee-change-car-info.html";
    }

    @PutMapping(value = "/cars/savecarchange")
    public String updatecarinfo(@RequestParam(required = false) String removeCurrentUser,
                                @RequestParam(required = false) String removeReservation,
                                Car car) {
        if (removeCurrentUser != null) carService.removeCurrentUserOfCar(car);
        if (removeReservation != null) carService.removeReservation(car);
        carService.save(car);
        return "redirect:/employee/cars/carinfochanged";
    }

    @GetMapping(value = "/cars/carinfochanged")
    public String carInfoChanged() {
        return "employee/employee-car-information-changed.html";
    }

    @GetMapping(value = "/orders")
    public String orders(@RequestParam(required = false) String firstName,
                         @RequestParam(required = false) String lastName,
                         @RequestParam(required = false) Long userId,
                         @RequestParam(required = false) String carName,
                         @RequestParam(required = false) String owner,
                         @RequestParam(required = false) Long selectedCarId,
                         @RequestParam(required = false) Long removeCarId,
                         @RequestParam(required = false) String selectedCarIdsInString,
                         Model model) {

        List<User> users = userService.userSearcher(firstName, lastName);
        LinkedHashMap<User, BigDecimal> usersAndFines = fineCalculator.getAllUsersWithFines(users);

        List<Car> searchedCars = carService.searchCars(carName, owner);

        User user = null;
        if (userId != null) user = userService.findById(userId);

        Set<Long> selectedCarIds = new LinkedHashSet<>();
        if (selectedCarIdsInString != null)
            selectedCarIds = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        if (selectedCarId != null) selectedCarIds.add(selectedCarId);
        if (removeCarId != null) selectedCarIds.remove(removeCarId);

        List<Car> selectedCarObjects = carService.convertIdsCollectionToCarsList(selectedCarIds);

        model.addAttribute("selectedCarObjects", selectedCarObjects);
        model.addAttribute("selectedCarIds", selectedCarIds);
        model.addAttribute("carName", carName);
        model.addAttribute("owner", owner);
        model.addAttribute("searchedCars", searchedCars);
        model.addAttribute("users", usersAndFines);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);

        return "employee/employee-orders.html";
    }

    @GetMapping(value = "/confirmorder")
    public String confirmOrder(@RequestParam String selectedCarIdsInString,
                               @RequestParam Long userId,
                               Model model) {
        Set<Long> selectedCarIds = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        List<Car> selectedCars = carService.convertIdsCollectionToCarsList(selectedCarIds);
        User user = userService.findById(userId);

        model.addAttribute("selectedCarIds", selectedCarIds);
        model.addAttribute("user", user);
        model.addAttribute("selectedCars", selectedCars);

        return "employee/employee-confirm-order.html";
    }

    @PutMapping(value = "/saveorder")
    public String saveOrder(@RequestParam Long userId,
                            @RequestParam String selectedCarIdsInString) {
        Set<Long> selectedCarIds = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        User user = userService.findById(userId);
        carService.saveCarOrder(selectedCarIds, user);
        return "redirect:/employee/ordersaved";
    }

    @GetMapping(value = "/ordersaved")
    public String orderSaved() {
        return "employee/employee-order-saved.html";
    }


    @GetMapping(value = "/returnedcars")
    public String returnedCars(@RequestParam(required = false) Long userId,
                               @RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               @RequestParam(required = false) Long selectedCarId,
                               @RequestParam(required = false) Long removeCarId,
                               @RequestParam(required = false) String selectedCarIdsInString,
                               Model model) {

        List<User> users = userService.userSearcher(firstName, lastName);

        User user = null;
        if (userId != null) user = userService.findById(userId);

        LinkedHashMap<Car, BigDecimal> carsInUseByUser = null;
        if (user != null) carsInUseByUser = fineCalculator.getCarsWithFines(user.getCars());

        Set<Long> selectedCarIds = new LinkedHashSet<>();
        if (selectedCarIdsInString != null)
            selectedCarIds = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        if (removeCarId != null) selectedCarIds.remove(removeCarId);
        if (selectedCarId != null) selectedCarIds.add(selectedCarId);

        LinkedHashMap<Car, BigDecimal> selectedCars = fineCalculator.getCarsWithFines(carService.convertIdsCollectionToCarsList(selectedCarIds));
        BigDecimal fineToPay = fineCalculator.getTotalFine(carService.convertIdsCollectionToCarsList(selectedCarIds));

        model.addAttribute("selectedCarIds", selectedCarIds);
        model.addAttribute("fineToPay", fineToPay);
        model.addAttribute("selectedCars", selectedCars);
        model.addAttribute("carsInUseByUser", carsInUseByUser);
        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("userId", userId);
        return "employee/employee-returned-cars.html";
    }

    @GetMapping(value = "/confirmreturnedcars")
    public String confirmReturnedCars(@RequestParam Long userId,
                                      @RequestParam BigDecimal fineToPay,
                                      @RequestParam String selectedCarIdsInString,
                                      Model model) {
        Set<Long> selectedCarIds = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        List<Car> selectedCars = carService.convertIdsCollectionToCarsList(selectedCarIds);

        model.addAttribute("selectedCars", selectedCars);
        model.addAttribute("selectedCarIds", selectedCarIds);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("fineToPay", fineToPay);
        return "employee/employee-confirm-returned-cars.html";
    }


    @PutMapping(value = "/savereturnedcars")
    public String saveReturnedCars(@RequestParam String selectedCarIdsInString) {
        Set<Long> returnedCars = listConverter.convertListInStringToSetInLong(selectedCarIdsInString);
        List<Car> cars = carService.convertIdsCollectionToCarsList(returnedCars);
        carService.removeCurrentUserOfMultipleCars(cars);
        return "redirect:/employee/carsreturned";
    }

    @GetMapping(value = "/carsreturned")
    public String carsReturned() {
        return "employee/employee-cars-returned.html";
    }

    @GetMapping(value = "/reservations")
    public String reservations(Model model) {
        model.addAttribute("unprocessedReservations", carService.getUnprocessedCarReservations());
        model.addAttribute("processedReservations", carService.getProcessedCarReservations());
        return "employee/employee-reservations.html";
    }

    @PutMapping(value = "/setreadyforpickup")
    public String setReadyForPickup(@RequestParam Long carId,
                                    @RequestParam Long userId,
                                    Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("car", carService.findById(carId));
        return "employee/employee-reservation-ready-for-pick-up.html";
    }

    @PutMapping(value = "/updatecarreservation")
    public String updateCarReservation(@RequestParam Long carId,
                                       @RequestParam Long userId) {

        Car car = carService.findById(carId);
        Notification notification = new Notification(LocalDate.now(), car.getEndReservationDate(), "Your reservation is ready for pick-up until " +
                car.getEndReservationDate() + ". " + car.getCarName() + " by " + car.getOwner() + ".");

        notification.setValidUntilDate(car.getEndReservationDate());
        notification.setNotificationReceiver(userService.findById(userId));
        notifyService.save(notification);
        userService.saveById(userId);
        car.setReadyForPickup(true);
        carService.save(car);
        return "redirect:/employee/reservations";
    }
}
