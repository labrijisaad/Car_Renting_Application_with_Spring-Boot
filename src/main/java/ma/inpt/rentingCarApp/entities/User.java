package ma.inpt.rentingCarApp.entities;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity @Table(name = "USERS")
public class User {

    // class attributes :
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;
    @Column(name = "username")
    private String userName;
    private String password;
    private boolean enabled = true;
    private String role = "ROLE_USER";

    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String phoneNumber;

    @OneToMany(mappedBy = "reservedByUser")
    public List<Car> reservedCars;

    @OneToMany(mappedBy = "theUser")
    private List<Car> cars;

    @OneToMany(mappedBy = "notificationReceiver")
    private List<Notification> notifications;

    // class constructors :
    public User() {

    }

    public User(String userName, String password, String email, String firstName,
                String lastName, String address, String phoneNumber, String city) {
        super();
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    // class methods :
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Car> getReversedCars() {
        return reservedCars;
    }

    public void setReversedCars(List<Car> reservedCars) {
        this.reservedCars = reservedCars;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
