package bt.edu.gcit.usermicroservice.service;

import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User save(User user);

    boolean isEmailDuplicate(String email);

    User updateUser(Long id, User updatedUser);

    void deleteById(Long theId);

    void updateUserEnabledStatus(Long id, boolean enabled);

    void uploadUserPhoto(Long id, MultipartFile photo) throws IOException;

    User findByID(Long theId);

}
