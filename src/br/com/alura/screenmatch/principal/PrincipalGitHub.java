package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.exceptions.ErroConsultaGitHubException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class PrincipalGitHub {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        System.out.println("Entre um usuário pesquisa:");
        var usuario = leitura.nextLine();

        String url = "https://api.github.com/users/" + usuario;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            if (response.statusCode() == 404 && json.indexOf("Not Found") > 0){
                throw new ErroConsultaGitHubException("Usuário não existe");
            }
            System.out.println(response.toString());
            System.out.println(json);
        } catch (ErroConsultaGitHubException e){
            System.out.println("Erro ao consultar usuário no Github");
            System.out.println(e.getMessage());
        }
    }
}
