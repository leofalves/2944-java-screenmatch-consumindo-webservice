package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.exceptions.ErroDeConversaoDeAnoException;
import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalGravaListaNoArquivo {

    public static void main(String[] args) throws IOException, InterruptedException {

        String filme = "";
        Scanner sc = new Scanner(System.in);
        List<Titulo> titulos = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();


        while (!filme.equalsIgnoreCase("sair")){

            System.out.println("Entre com o nome do filme ou digite sair para finalizar:");
            filme = sc.nextLine();
            if(filme.equalsIgnoreCase("sair")){
                break;
            }

            String url = "https://www.omdbapi.com/?t=" + encodeValue(filme) + "&apikey=c6c97426";

            try{
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();
                TituloOmdb tituloOmdb = gson.fromJson(json, TituloOmdb.class);

                if (tituloOmdb.response().equalsIgnoreCase("True"))
                {
                    Titulo meuTitulo = new Titulo(tituloOmdb);
                    titulos.add(meuTitulo);
                }
                else{
                    System.out.println("Filme não encontrado: ".concat(filme));
                }

            } catch (NumberFormatException e) {
                System.out.println("Ocorreu um erro de conversão de números");
                System.out.println(e.getMessage());

            } catch (IllegalArgumentException e){
                System.out.println("Erro de argumento inválido. Verifique o endereço.");
                System.out.println(e.getMessage());

            }
            catch (ErroDeConversaoDeAnoException e){
                System.out.println(e.getMessage());
            }
        }

        FileWriter escrita = new FileWriter("filmes.json");
        escrita.write(gson.toJson(titulos));
        escrita.close();
        System.out.println("----------- Fim do programa --------------------");

    }

    // Method to encode a string value using `UTF-8` encoding scheme
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
