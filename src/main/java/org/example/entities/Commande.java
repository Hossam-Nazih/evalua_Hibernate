package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Commande passee par un client ; son contenu est decrit par des lignes CommandeProduit.
 */
@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_passage")
    private LocalDate datePassage;

    // facultatif : une commande peut etre saisie sans client identifie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // les lignes suivent le cycle de vie de la commande (enregistrees et supprimees avec elle)
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeProduit> lignesCommande = new ArrayList<>();

    public Commande() {}
    public Commande(LocalDate datePassage) { this.datePassage = datePassage; }
    public Commande(LocalDate datePassage, Client client) {
        this.datePassage = datePassage;
        this.client = client;
    }

    // ajoute une ligne (produit, quantite) a la commande
    public void ajouterProduit(Produit p, int quantite) {
        lignesCommande.add(new CommandeProduit(this, p, quantite));
    }

    public Long getId() { return id; }
    public LocalDate getDatePassage() { return datePassage; }
    public void setDatePassage(LocalDate datePassage) { this.datePassage = datePassage; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<CommandeProduit> getLignesCommande() { return lignesCommande; }

    @Override
    public String toString() {
        return "Commande{id=" + id + ", datePassage=" + datePassage + "}";
    }
}
