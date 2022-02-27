package ma.inpt.rentingCarApp.services;

import java.util.ArrayList;
import java.util.List;
import ma.inpt.rentingCarApp.DAO.UserRepository;
import ma.inpt.rentingCarApp.entities.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // class attribute :
    final UserRepository usRepo;

    // class constructor :
    public UserService(UserRepository usRepo) {
        this.usRepo = usRepo;
    }

    // class methods :
    public void save(User user) {
        usRepo.save(user);
    }

    public void saveById(Long userId) {
        User user = usRepo.findById(userId).get();
        usRepo.save(user);
    }

    public User findById(long id) {
        return usRepo.findById(id).get();
    }

    public List<User> findAll() {
        return (List<User>) usRepo.findAll();
    }

    public List<User> userSearcher(String firstName, String lastName) {
        if (firstName != null && lastName != null) return getByFullName(firstName, lastName);
        else if (firstName == null && lastName != null) return getByLastName(lastName);
        else if (firstName != null) return getByFirstName(firstName);
        else return new ArrayList<>();
    }

    public List<User> getByFirstName(String firstName) {
        List<User> users = new ArrayList<>();
        for (User user : usRepo.findAll()) {
            if (user.getFirstName().toLowerCase().contains(firstName.toLowerCase())) {
                users.add(user);
            }
        }
        return users;
    }

    public List<User> getByLastName(String lastName) {
        List<User> users = new ArrayList<>();
        for (User user : usRepo.findAll()) {
            if (user.getLastName().toLowerCase().contains(lastName.toLowerCase())) {
                users.add(user);
            }
        }
        return users;
    }

    public List<User> getByFullName(String firstName, String lastName) {
        List<User> users = new ArrayList<>();
        for (User user : usRepo.findAll()) {
            if (user.getFirstName().toLowerCase().contains(firstName.toLowerCase()) &&
                    user.getLastName().toLowerCase().contains(lastName.toLowerCase())) {
                users.add(user);
            }
        }
        return users;
    }

}
