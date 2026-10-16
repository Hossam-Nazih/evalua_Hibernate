package ma.projet.classes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Nouvelle entite : acheteur a l'origine des commandes.
 */
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom_client", nullable = false, length = 100)
    private String nomClient;

    // sert d'identifiant fonctionnel : deux clients ne partagent pas la meme adresse
    @Column(name = "email", unique = true, length = 120)
    private String email;

    @OneToMany(mappedBy = "client")
    private List<Commande> commandesPassees = new ArrayList<>();

    public Client() {}

    public Client(String nomClient, String email) {
        this.nomClient = nomClient;
        this.email = email;
    }

    public int getId() { return id; }
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Commande> getCommandesPassees() { return commandesPassees; }

    @Override
    public String toString() {
        return "Client{id=" + id + ", nomClient='" + nomClient + "', email='" + email + "'}";
    }
}
