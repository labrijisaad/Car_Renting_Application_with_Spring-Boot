package ma.inpt.rentingCarApp.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Cars")
public class Car {

    // class attributes :
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Primary key of the CARS table.
    private long carId;
    private String carName;
    private String owner;
    private int releaseYear;
    private int edition;
    private LocalDate returnDate = null;
    private LocalDate startReservationDate = null;
    private LocalDate endReservationDate = null;
    private int timesExtended = 0;
    private boolean readyForPickUp = false;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private User reservedByUser;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    private User theUser;

    // class constructors :
    public Car() {

    }

    public Car(String carName, String owner, int releaseYear, int edition) {
        super();
        this.carName = carName;
        this.owner = owner;
        this.releaseYear = releaseYear;
        this.edition = edition;
    }

    // class methods :
    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public User getTheUser() {
        return theUser;
    }

    public void setTheUser(User theUser) {
        this.theUser = theUser;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public int getTimesExtended() {
        return timesExtended;
    }

    public void setTimesExtended(int timesExtended) {
        this.timesExtended = timesExtended;
    }

    public LocalDate getEndReservationDate() {
        return endReservationDate;
    }

    public void setEndReservationDate(LocalDate endReservationDate) {
        this.endReservationDate = endReservationDate;
    }

    public LocalDate getStartReservationDate() {
        return startReservationDate;
    }

    public void setStartReservationDate(LocalDate startReservationDate) {
        this.startReservationDate = startReservationDate;
    }

    public User getReservedByUser() {
        return reservedByUser;
    }

    public void setReservedByUser(User reservedByUser) {
        this.reservedByUser = reservedByUser;
    }

    public void setReadyForPickup(boolean readyForPickUp) {
        this.readyForPickUp = readyForPickUp;
    }

    public boolean getReadyForPickUp() {
        return readyForPickUp;
    }

}
