package com.ristorante.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.common.ServiceResult;
import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.model.MovimentoMagazzinoDTO;
import com.ristorante.ui.util.SessionManager;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ArticoloService {

    private static final String BASE_URL = "http://localhost:8081/api";

    public List<ArticoloDTO> loadArticoli() {
        List<ArticoloDTO> lista = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
            	lista.add(new ArticoloDTO(
            	        node.get("id").asLong(),
            	        node.get("codice").asText(),
            	        node.get("nome").asText(),
            	        node.hasNonNull("descrizione") ? node.get("descrizione").asText() : "",
            	        node.get("prezzo").decimalValue(),
            	        node.hasNonNull("categoriaId") ? node.get("categoriaId").asLong() : null,
            	        node.hasNonNull("categoriaNome") ? node.get("categoriaNome").asText() : "",
            	        node.get("attivo").asBoolean(),
            	        node.get("iva").asInt(),
            	        node.hasNonNull("quantitaDisponibile") ? node.get("quantitaDisponibile").asInt() : 0,
    	        		node.hasNonNull("sogliaWarning") ? node.get("sogliaWarning").asInt() : 0,
    	        		node.hasNonNull("statoMagazzino") ? node.get("statoMagazzino").asText() : "DISPONIBILE",
    	        		node.hasNonNull("gestioneMagazzino") ? node.get("gestioneMagazzino").asBoolean() : false
            	));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public ServiceResult createArticolo(String nome, String descrizione,
    							BigDecimal prezzo, Long categoriaId, Integer iva, Integer quantitaDisponibile,
    							Integer sogliaWarning, boolean gestioneMagazzino) {
        try {
        	
        	if (categoriaId == null) {
        		return ServiceResult.fail("Seleziona una categoria.");
            }
        	
        	String jsonInput = """
        		    {
        		      "nome": "%s",
        		      "descrizione": "%s",
        		      "prezzo": %s,
        		      "categoriaId": %d,
        		      "attivo": true,
        		      "iva": %s,
        		      "quantitaDisponibile": %s,
        			  "sogliaWarning": %s,
        			  "gestioneMagazzino": %s
        		    }
        		    """.formatted(nome, descrizione, prezzo, categoriaId, iva, quantitaDisponibile, sogliaWarning, gestioneMagazzino);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
    }

    public ServiceResult updateArticolo(Long id, String nome,
            String descrizione, BigDecimal prezzo,
            Long categoriaId, Integer iva, boolean attivo, Integer quantitaDisponibile,
            Integer sogliaWarning, boolean gestioneMagazzino) {
        try {
        	
			 if (categoriaId == null) {
				 return ServiceResult.fail("Seleziona una categoria.");
			 }
        	 
        	String jsonInput = """
        		    {
        		      "nome": "%s",
        		      "descrizione": "%s",
        		      "prezzo": %s,
        		      "categoriaId": %d,
        		      "attivo": %s,
        		      "iva": %s,
        		      "quantitaDisponibile": %s,
        			  "sogliaWarning": %s,
        			  "gestioneMagazzino": %s
        		    }
        		    """.formatted(nome, descrizione, prezzo, categoriaId, attivo, iva, quantitaDisponibile, sogliaWarning, gestioneMagazzino);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
    }

    public ServiceResult updateStatoArticolo(Long id, boolean attivo) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = """
                {
                  "attivo": %s
                }
                """.formatted(attivo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + id + "/stato"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
    }
    
    public ServiceResult deleteArticolo(Long id) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
    }
    
    public ServiceResult updateQuantitaArticolo(Long id, int delta) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = """
            	    {
            	      "delta": %s,
            	      "utenteId": %s,
            	      "utenteUsername": "%s"
            	    }
            	    """.formatted(
            	        delta,
            	        SessionManager.getUtenteId(),
            	        SessionManager.getUsername()
            	    );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + id + "/quantita"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
    }
    
    public List<MovimentoMagazzinoDTO> loadMovimentiArticolo(Long articoloId) {
        List<MovimentoMagazzinoDTO> lista = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + articoloId + "/movimenti"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
                lista.add(new MovimentoMagazzinoDTO(
                        node.get("id").asLong(),
                        node.hasNonNull("articoloNome") ? node.get("articoloNome").asText() : "",
                        node.hasNonNull("tipo") ? node.get("tipo").asText() : "",
                        node.hasNonNull("quantita") ? node.get("quantita").asInt() : 0,
                        node.hasNonNull("note") ? node.get("note").asText() : "",
                        node.hasNonNull("dataMovimento") ? node.get("dataMovimento").asText() : "",
                        node.hasNonNull("utenteId") ? node.get("utenteId").asLong() : null,
                        node.hasNonNull("utenteUsername") ? node.get("utenteUsername").asText() : ""
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    private ServiceResult handleResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return ServiceResult.ok();
        }

        String message = extractErrorMessage(response.body());

        if (message == null || message.isBlank()) {
            message = "Operazione non riuscita.";
        }

        return ServiceResult.fail(message);
    }

    private String extractErrorMessage(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(responseBody);

            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
