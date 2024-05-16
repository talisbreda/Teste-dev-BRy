package talis.bry.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import talis.bry.Async.InsertionMultiStatusResponse;
import talis.bry.Async.UpdateMultiStatusResponse;
import talis.bry.Database.Classes.User;
import talis.bry.Database.ImageManager;
import talis.bry.Database.repositories.UserRepository;
import talis.bry.Utils.CPFHandler;
import talis.bry.Utils.JSONHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageManager imageManager;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(
            UserRepository userRepository,
            ImageManager imageManager,
            @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor,
            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.imageManager = imageManager;
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public synchronized void insertUser(String userJson) throws NoSuchAlgorithmException, JSONException, SQLException, IOException, IllegalArgumentException {
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
    public synchronized void updateUser(Long id, String userJson) throws NoSuchAlgorithmException, JSONException, IOException, SQLException, IllegalArgumentException {
        JSONHandler.validateUserUpdateJson(userJson);

        User userToUpdate = userRepository.findById(id).orElse(null);
        if (userToUpdate == null) {
            throw new IllegalArgumentException("User not found");
        }
        JSONObject jsonObject = new JSONObject(userJson);

        String name = jsonObject.getString("name");
        userToUpdate.setName(name);

        String base64Photo = jsonObject.getString("photo");
        long oid = imageManager.storeImageAndGetOID(base64Photo);
        imageManager.deleteImage(userToUpdate.getPhotoOID());

        userToUpdate.setPhotoOID(oid);
        userRepository.save(userToUpdate);
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<InsertionMultiStatusResponse> insertMultipleUsers(String usersJson) throws NoSuchAlgorithmException, JSONException, SQLException, IOException, IllegalArgumentException {
        JSONHandler.validateMultipleUserInsertionJson(usersJson);
        JSONObject jsonObject = new JSONObject(usersJson);
        JSONArray users = jsonObject.getJSONArray("users");

        List<InsertionMultiStatusResponse.UserInsertionResult> results = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<?>[] futures = new CompletableFuture[users.length()];

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    insertUser(user.toString());
                    results.add(new InsertionMultiStatusResponse.UserInsertionResult(user.getString("cpf"), 201, "User inserted successfully"));
                } catch (NoSuchAlgorithmException | IOException | SQLException e) {
                    e.printStackTrace();
                    results.add(new InsertionMultiStatusResponse.UserInsertionResult(user.getString("cpf"), 500, e.getMessage()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    results.add(new InsertionMultiStatusResponse.UserInsertionResult(user.getString("cpf"), 400, e.getMessage()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    results.add(new InsertionMultiStatusResponse.UserInsertionResult(user.getString("cpf"), 404, e.getMessage()));
                }
            }, taskExecutor);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(v -> new InsertionMultiStatusResponse(results));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<UpdateMultiStatusResponse> updateMultipleUsers(String usersJson) throws NoSuchAlgorithmException, JSONException, IOException, SQLException, IllegalArgumentException {
        JSONHandler.validateMultipleUserUpdateJson(usersJson);
        JSONObject jsonObject = new JSONObject(usersJson);
        JSONArray users = jsonObject.getJSONArray("users");

        List<UpdateMultiStatusResponse.UserUpdateResult> results = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<?>[] futures = new CompletableFuture[users.length()];

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    updateUser(user.getLong("id"), user.toString());
                    results.add(new UpdateMultiStatusResponse.UserUpdateResult(user.getLong("id"), 200, "User updated successfully"));
                } catch (NoSuchAlgorithmException | IOException | SQLException e) {
                    e.printStackTrace();
                    results.add(new UpdateMultiStatusResponse.UserUpdateResult(user.getLong("id"), 500, e.getMessage()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    results.add(new UpdateMultiStatusResponse.UserUpdateResult(user.getLong("id"), 400, e.getMessage()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    results.add(new UpdateMultiStatusResponse.UserUpdateResult(user.getLong("id"), 404, e.getMessage()));
                }
            }, taskExecutor);
        }

        return CompletableFuture.allOf(futures)
                .thenApply(v -> new UpdateMultiStatusResponse(results));
    }

    public JSONObject getUserJsonWithPhoto(User user) throws JSONException, SQLException, IllegalArgumentException{        Long imageOID = user.getPhotoOID();
        String base64Image = imageManager.getUserImageFromOID(imageOID);

        JSONObject userJson = user.toJson();
        userJson.put("photo", base64Image);
        return userJson;
    }

    public String ConvertToJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
