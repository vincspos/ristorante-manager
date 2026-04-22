package com.ristorante.manager.dto;

import java.math.BigDecimal;

public class ArticoloResponse {

    private Long id;
    private String codice;
    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private Long categoriaId;
    private String categoriaNome;
    private boolean attivo;
    private Integer iva;

    public ArticoloResponse() {
    }

    public ArticoloResponse(Long id, String codice, String nome, String descrizione,
                            BigDecimal prezzo, Long categoriaId, String categoriaNome,
                            boolean attivo, Integer iva) {
        this.id = id;
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.attivo = attivo;
        this.iva = iva;
    }

    public Long getId() { return id; }
    public String getCodice() { return codice; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public BigDecimal getPrezzo() { return prezzo; }
    public Long getCategoriaId() { return categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public boolean isAttivo() { return attivo; }
    public Integer getIva() { return iva; }
}