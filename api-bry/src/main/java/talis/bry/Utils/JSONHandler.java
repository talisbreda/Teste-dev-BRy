package talis.bry.Utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class JSONHandler {

    private static final String userInsertionSchemaPath = "jsonSchemas/UserInsertionJsonSchema.json";
    private static final String userUpdateSchemaPath = "jsonSchemas/UserUpdateJsonSchema.json";

    public static void validateUserInsertionJson(String userJson) throws IllegalArgumentException {
        JsonSchemaValidator jsonSchemaValidator = getSchema(userInsertionSchemaPath);
        JSONObject jsonObject = new JSONObject(userJson);

        if (!jsonObject.getString("name").matches("^[a-zA-ZÀ-ÖØ-öø-ÿ\\s]*$")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }
        if (!jsonObject.getString("cpf").matches("^[0-9.-]*$")) {
            throw new IllegalArgumentException("CPF format is wrong");
        }
        if (!jsonSchemaValidator.matches(userJson)) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    public static void validateUserUpdateJson(String userJson) throws IllegalArgumentException {
        JsonSchemaValidator jsonSchemaValidator = getSchema(userUpdateSchemaPath);
        JSONObject jsonObject = new JSONObject(userJson);

        if (!jsonObject.getString("name").matches("^[a-zA-ZÀ-ÖØ-öø-ÿ\\s]*$")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }
        if (!jsonSchemaValidator.matches(userJson)) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    private static JsonSchemaValidator getSchema(String schemaPath) {
        InputStream userSchemaInputStream = JSONHandler.class.getClassLoader().getResourceAsStream(schemaPath);
        assert userSchemaInputStream != null;
        return JsonSchemaValidator.matchesJsonSchema(userSchemaInputStream);
    }

}
