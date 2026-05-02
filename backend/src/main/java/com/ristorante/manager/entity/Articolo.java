package com.ristorante.manager.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "articoli")
public class Articolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codice;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 255)
    private String descrizione;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaArticolo categoria;

    @Column(nullable = false)
    private Boolean attivo = true;
    
    @Column(nullable = false)
    private Integer iva;
    
    @Column(nullable = false)
    private Integer quantitaDisponibile = 0;

    @Column(nullable = false)
    private Integer sogliaWarning = 0;
    
    @Column(nullable = false)
    private Boolean gestioneMagazzino = false;

    // GETTER & SETTER

    public Long getId() { return id; }

    public String getCodice() { return codice; }
    
    public void setCodice(String codice) { this.codice = codice; }

    public String getNome() { return nome; }
    
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public CategoriaArticolo getCategoria() { return categoria; }
    
    public void setCategoria(CategoriaArticolo categoria) { this.categoria = categoria; }

    public Boolean getAttivo() { return attivo; }
    
    public void setAttivo(Boolean attivo) { this.attivo = attivo; }
    
    public Integer getIva() {return iva;}
    
    public void setIva(Integer iva) {this.iva = iva;}
    
    public Integer getQuantitaDisponibile() { return quantitaDisponibile;}

    public void setQuantitaDisponibile(Integer quantitaDisponibile) {this.quantitaDisponibile = quantitaDisponibile;}

    public Integer getSogliaWarning() {return sogliaWarning; }

    public void setSogliaWarning(Integer sogliaWarning) { this.sogliaWarning = sogliaWarning;}
    
    public Boolean getGestioneMagazzino() {return gestioneMagazzino; }

    public void setGestioneMagazzino(Boolean gestioneMagazzino) {this.gestioneMagazzino = gestioneMagazzino;}
    
    @Transient
    public String getStatoMagazzino() {
    	if (!Boolean.TRUE.equals(gestioneMagazzino)) {
            return "NON_GESTITO";
        }

        if (quantitaDisponibile == null || quantitaDisponibile <= 0) {
            return "ESAURITO";
        }

        if (sogliaWarning != null && sogliaWarning > 0 && quantitaDisponibile <= sogliaWarning) {
            return "IN_ESAURIMENTO";
        }

        return "DISPONIBILE";
    }
}