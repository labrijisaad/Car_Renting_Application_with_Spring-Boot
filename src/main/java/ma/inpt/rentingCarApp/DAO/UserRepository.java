package ma.inpt.rentingCarApp.DAO;

import ma.inpt.rentingCarApp.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
