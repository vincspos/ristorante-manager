package com.ristorante.manager.dto;

import java.time.LocalDateTime;

public class MovimentoMagazzinoResponse {

    private Long id;
    private String articoloNome;
    private String tipo;
    private Integer quantita;
    private String note;
    private LocalDateTime dataMovimento;
    private Long utenteId;
    private String utenteUsername;

    public MovimentoMagazzinoResponse(
            Long id,
            String articoloNome,
            String tipo,
            Integer quantita,
            String note,
            LocalDateTime dataMovimento,
            Long utenteId,
            String utenteUsername
    ) {
        this.id = id;
        this.articoloNome = articoloNome;
        this.tipo = tipo;
        this.quantita = quantita;
        this.note = note;
        this.dataMovimento = dataMovimento;
        this.utenteId = utenteId;
        this.utenteUsername = utenteUsername;
    }

    public Long getId() { return id; }
    public String getArticoloNome() { return articoloNome; }
    public String getTipo() { return tipo; }
    public Integer getQuantita() { return quantita; }
    public String getNote() { return note; }
    public LocalDateTime getDataMovimento() { return dataMovimento; }
    public Long getUtenteId() { return utenteId; }
    
}
