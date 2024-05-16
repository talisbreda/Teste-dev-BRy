package talis.bry.Database;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;

@Component
public class ImageManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long storeImageAndGetOID(String base64Image) throws SQLException, IOException {
        byte[] photoBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);

        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            conn.setAutoCommit(false);

            // Store the photo in pg_largeobject
            long oid = 0;
            PreparedStatement psCreate = conn.prepareStatement("SELECT lo_create(0)");
            ResultSet rsCreate = psCreate.executeQuery();
            if (rsCreate.next()) {
                oid = rsCreate.getLong(1);
            }


            PreparedStatement psOpen = conn.prepareStatement("SELECT lo_open(?, 131072)"); // 131072 is INV_WRITE
            psOpen.setLong(1, oid);
            ResultSet rsOpen = psOpen.executeQuery();
            if (rsOpen.next()) {
                PGConnection pgConnection = conn.unwrap(org.postgresql.PGConnection.class);
                LargeObjectManager largeObjectManager = pgConnection.getLargeObjectAPI();
                try (LargeObject largeObject = largeObjectManager.open(oid, LargeObjectManager.WRITE)) {
                     OutputStream outputStream = largeObject.getOutputStream();
                     outputStream.write(photoBytes);
                }
            }

            conn.commit();

            return oid;
        }
    }

    public String getUserImageFromOID(Long photoOid) throws SQLException, IllegalArgumentException {
        if (photoOid == null) {
            throw new IllegalArgumentException("User OID was not found");
        }

        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            conn.setAutoCommit(false);

            byte[] imageData;

            PGConnection pgConnection = conn.unwrap(org.postgresql.PGConnection.class);
            LargeObjectManager largeObjectManager = pgConnection.getLargeObjectAPI();
            try (LargeObject largeObject = largeObjectManager.open(photoOid, LargeObjectManager.READ)) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                System.out.println(largeObject.toString());

                while ((bytesRead = largeObject.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                imageData = outputStream.toByteArray();
            }
            return Base64.getEncoder().encodeToString(imageData);
        }
    }

    public void deleteImage(Long photoOid) throws SQLException, IllegalArgumentException {
        if (photoOid == null) {
            throw new IllegalArgumentException("User OID was not found");
        }

        try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            conn.setAutoCommit(false);

            PGConnection pgConnection = conn.unwrap(org.postgresql.PGConnection.class);
            LargeObjectManager largeObjectManager = pgConnection.getLargeObjectAPI();
            largeObjectManager.delete(photoOid);
        }
    }
}
