package com.ristorante.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.common.ServiceResult;
import com.ristorante.ui.model.CategoriaArticoloDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CategoriaArticoloService {

    private static final String BASE_URL = "http://localhost:8081/api";

    public List<CategoriaArticoloDTO> loadCategorie() {
        List<CategoriaArticoloDTO> lista = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/categorie-articoli"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
                lista.add(new CategoriaArticoloDTO(
                        node.get("id").asLong(),
                        node.get("nome").asText(),
                        node.get("colore").asText(),
                        node.get("attivo").asBoolean()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public ServiceResult createCategoria(String nome, String colore) {
        try {
            String jsonInput = """
                {
                  "nome": "%s",
                  "colore": "%s",
                  "attivo": true
                }
                """.formatted(nome, colore);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/categorie-articoli"))
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

    public ServiceResult updateCategoria(Long id, String nome, String colore, boolean attivo) {
        try {
            String jsonInput = """
                {
                  "id": %d,
                  "nome": "%s",
                  "colore": "%s",
                  "attivo": %s
                }
                """.formatted(id, nome, colore, attivo);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/categorie-articoli/" + id))
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

    public ServiceResult updateStatoCategoria(Long id, boolean attivo) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = """
                {
                  "attivo": %s
                }
                """.formatted(attivo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/categorie-articoli/" + id + "/stato"))
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

    public ServiceResult deleteCategoria(Long id) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/categorie-articoli/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("Impossibile comunicare con il server.");
        }
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