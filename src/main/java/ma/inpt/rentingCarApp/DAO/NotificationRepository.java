package ma.inpt.rentingCarApp.DAO;

import ma.inpt.rentingCarApp.entities.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

}
