package talis.bry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import talis.bry.Async.InsertionMultiStatusResponse;
import talis.bry.Async.UpdateMultiStatusResponse;
import talis.bry.Database.Classes.User;
import talis.bry.Database.ImageManager;
import talis.bry.Database.repositories.UserRepository;
import talis.bry.Services.UserService;
import talis.bry.Utils.CPFHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ImageManager imageManager;

    @Autowired
    public UserController(
            UserRepository userRepository,
            UserService userService,
            ImageManager imageManager,
            @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.imageManager = imageManager;
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        List<User> users = userRepository.findAll();
        JSONArray usersArray = new JSONArray();
        try {
            for (User user : users) {
                usersArray.put(user.toJson());
            }

            return ResponseEntity.ok(usersArray.toString());
        } catch (JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @GetMapping("/user/cpf/{cpf}")
    public ResponseEntity<String> getUser(@PathVariable String cpf) {
        try {
            String hashedCpf = CPFHandler.hash(cpf);
            User user = userRepository.findByCpf(hashedCpf).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            JSONObject userJson = userService.getUserJsonWithPhoto(user);
            return ResponseEntity.ok(userJson.toString());

        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<String> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            JSONObject userJson = userService.getUserJsonWithPhoto(user);
            return ResponseEntity.ok(userJson.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody String userJson) {
        try {
            userService.insertUser(userJson);

            return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered!");
        } catch (NoSuchAlgorithmException | IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/id/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody String userJson) {
        try {
            userService.updateUser(id, userJson);

            return ResponseEntity.ok("User successfully updated!");
        } catch (NoSuchAlgorithmException | IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user/id/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            imageManager.deleteImage(user.getPhotoOID());
            userRepository.delete(user);

            return ResponseEntity.ok("User successfully deleted!");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("There was an error while deleting the image");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/registerMultipleUsers")
    public ResponseEntity<String> registerMultipleUsers(@RequestBody String usersJson) {
        try {
            InsertionMultiStatusResponse insertionMultiStatusResponse = userService.insertMultipleUsers(usersJson).get();
            String response = userService.ConvertToJSON(insertionMultiStatusResponse);

            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
        } catch (NoSuchAlgorithmException | IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("There was an error while inserting the users: " + e.getMessage());
        }
    }

    @PutMapping("/updateMultipleUsers")
    public ResponseEntity<String> updateMultipleUsers(@RequestBody String usersJson) {
        try {
            UpdateMultiStatusResponse insertionMultiStatusResponse = userService.updateMultipleUsers(usersJson).get();
            String response = userService.ConvertToJSON(insertionMultiStatusResponse);

            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
        } catch (NoSuchAlgorithmException | IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException | JSONException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("There was an error while updating the users: " + e.getMessage());
        }
    }


}
