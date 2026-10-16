package ma.projet.classes;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Bon de commande : une date, un client eventuel et des lignes (produit + quantite).
 */
@Entity
@Table(name = "commande")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "commande")
    private List<LigneCommandeProduit> lignesCommande = new ArrayList<>();

    public Commande() {}

    public Commande(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Commande(LocalDate dateEmission, Client client) {
        this.dateEmission = dateEmission;
        this.client = client;
    }

    public int getId() { return id; }
    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<LigneCommandeProduit> getLignesCommande() { return lignesCommande; }

    @Override
    public String toString() {
        return "Commande{id=" + id + ", dateEmission=" + dateEmission + "}";
    }
}
