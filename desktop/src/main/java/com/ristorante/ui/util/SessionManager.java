package com.ristorante.ui.util;

import com.ristorante.ui.model.LoginResponseDTO;

public class SessionManager {

    private static LoginResponseDTO utenteLoggato;

    private SessionManager() {
    }

    public static void setUtenteLoggato(LoginResponseDTO utente) {
        utenteLoggato = utente;
    }

    public static LoginResponseDTO getUtenteLoggato() {
        return utenteLoggato;
    }

    public static Long getUtenteId() {
        return utenteLoggato != null ? utenteLoggato.getId() : null;
    }

    public static String getUsername() {
        return utenteLoggato != null ? utenteLoggato.getUsername() : null;
    }

    public static String getRuolo() {
        return utenteLoggato != null ? utenteLoggato.getRuolo() : null;
    }

    public static void clear() {
        utenteLoggato = null;
    }
}