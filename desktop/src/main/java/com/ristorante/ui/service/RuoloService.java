package com.ristorante.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.model.RuoloDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RuoloService {

    private static final String BASE_URL = "http://localhost:8081/api";

    public List<RuoloDTO> loadRuoli() {
        List<RuoloDTO> lista = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/ruoli"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
                lista.add(new RuoloDTO(
                        node.get("id").asLong(),
                        node.get("codice").asText(),
                        node.get("descrizione").asText(),
                        node.get("attivo").asBoolean()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean createRuolo(String codice, String descrizione) {
        try {
            String jsonInput = """
                {
                  "codice": "%s",
                  "descrizione": "%s",
                  "attivo": true
                }
                """.formatted(codice, descrizione);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/ruoli"))
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

    public boolean updateRuolo(Long id, String codice, String descrizione, boolean attivo) {
        try {
            String jsonInput = """
                {
                  "id": %d,
                  "codice": "%s",
                  "descrizione": "%s",
                  "attivo": %s
                }
                """.formatted(id, codice, descrizione, attivo);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/ruoli/" + id))
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

    public boolean updateStatoRuolo(Long id, boolean attivo) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = """
                {
                  "attivo": %s
                }
                """.formatted(attivo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/ruoli/" + id + "/stato"))
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