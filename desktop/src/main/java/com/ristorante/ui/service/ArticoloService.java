package com.ristorante.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.model.ArticoloDTO;

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
                        node.get("categoria").asText(),
                        node.get("attivo").asBoolean(),
                        node.get("iva").asInt()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean createArticolo(String nome, String descrizione,
                                  String prezzo, String categoria, Integer iva) {
        try {
            String jsonInput = """
                {
                  "nome": "%s",
                  "descrizione": "%s",
                  "prezzo": %s,
                  "categoria": "%s",
                  "attivo": true,
                  "iva": %s
                }
                """.formatted(nome, descrizione, prezzo, categoria, iva);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateArticolo(Long id, String nome,
            String descrizione, String prezzo,
            String categoria, Integer iva, boolean attivo) {
        try {
            String jsonInput = """
                {
                  "id": %d,
                  "nome": "%s",
                  "descrizione": "%s",
                  "prezzo": %s,
                  "categoria": "%s",
                  "attivo": %s,
                  "iva": %s
                }
                """.formatted(id, nome, descrizione, prezzo, categoria, attivo, iva);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/articoli/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatoArticolo(Long id, boolean attivo) {
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

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
