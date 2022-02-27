package ma.inpt.rentingCarApp.entities;

import java.time.LocalDate;
import javax.persistence.*;

@Entity @Table(name = "NOTIFICATIONS")
public class Notification {

    // class attributes :
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long notificationId;
    private LocalDate notificationDate;
    private LocalDate validUntilDate;
    private String notificationMessage;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    private User notificationReceiver;

    // class constructors :
    public Notification() {

    }

    public Notification(LocalDate notificationDate, LocalDate validUntilDate, String notificationMessage) {
        this.notificationDate = notificationDate;
        this.validUntilDate = validUntilDate;
        this.notificationMessage = notificationMessage;
    }

    // class methods :
    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDate notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public User getNotificationReceiver() {
        return notificationReceiver;
    }

    public void setNotificationReceiver(User notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    public LocalDate getValidUntilDate() {
        return validUntilDate;
    }

    public void setValidUntilDate(LocalDate validUntilDate) {
        this.validUntilDate = validUntilDate;
    }

}
