package talis.bry.Database.Classes;

import jakarta.persistence.*;
import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String cpf;
    @Column
    private String anonymizedCpf;
    @Column
    private String photoOID;

    public User() {
    }

    public User(String name, String hashedCpf, String anonymizedCpf, String photoOID) {
        this.name = name;
        this.cpf = hashedCpf;
        this.anonymizedCpf = anonymizedCpf;
        this.photoOID = photoOID;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", name);
        jsonObject.put("cpf", anonymizedCpf);
        return jsonObject;
    }

    public String getCpf() {
        return cpf;
    }

    public void setAnonymizedCpf(String anonymizedCpf) {
        this.anonymizedCpf = anonymizedCpf;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoOID(Long photoOID) {
        this.photoOID = String.valueOf(photoOID);
    }

    public Long getPhotoOID() {
        return Long.parseLong(photoOID);
    }
}


