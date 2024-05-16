package talis.bry.Services;

import jakarta.transaction.Transactional;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import talis.bry.Database.Classes.User;
import talis.bry.Database.ImageManager;
import talis.bry.Database.repositories.UserRepository;
import talis.bry.Utils.CPFHandler;
import talis.bry.Utils.JSONHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Service
public class UserInsertionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageManager imageManager;

    @Transactional
    public void insertUser(String userJson) throws NoSuchAlgorithmException, JSONException, SQLException, IOException, IllegalArgumentException {
        JSONHandler.validateUserInsertionJson(userJson);

        JSONObject jsonObject = new JSONObject(userJson);
        String cpf = jsonObject.getString("cpf");
        String name = jsonObject.getString("name");

        if (!CPFHandler.validate(cpf)) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        String anonymizedCPF = CPFHandler.anonymize(cpf);
        String hashedCPF = CPFHandler.hash(cpf);

        if (userRepository.findByCpf(hashedCPF).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        String base64Photo = jsonObject.getString("photo");
        long oid = imageManager.storeImageAndGetOID(base64Photo);

        User user = new User(name, hashedCPF, anonymizedCPF, String.valueOf(oid));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, String userJson) throws NoSuchAlgorithmException, JSONException, IOException, SQLException, IllegalArgumentException {
        JSONHandler.validateUserUpdateJson(userJson);

        User userToUpdate = userRepository.findById(id).orElse(null);
        if (userToUpdate == null) {
            throw new IllegalArgumentException("User not found");
        }
        JSONObject jsonObject = new JSONObject(userJson);

        String name = jsonObject.getString("name");
        userToUpdate.setName(name);

        imageManager.deleteImage(userToUpdate.getPhotoOID());
        String base64Photo = jsonObject.getString("photo");
        long oid = imageManager.storeImageAndGetOID(base64Photo);

        userToUpdate.setPhotoOID(oid);
        userRepository.save(userToUpdate);
    }
}
