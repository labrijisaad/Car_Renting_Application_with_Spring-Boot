package ma.inpt.rentingCarApp.DAO;

import ma.inpt.rentingCarApp.entities.Car;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {

}
