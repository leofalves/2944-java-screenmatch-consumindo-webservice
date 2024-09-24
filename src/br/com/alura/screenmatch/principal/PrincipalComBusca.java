package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.exceptions.ErroDeConversaoDeAnoException;
import br.com.alura.screenmatch.modelos.Filme;
import br.com.alura.screenmatch.modelos.Titulo;
import br.com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PrincipalComBusca {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        System.out.println("Entre com o nome do filme para pesquisa:");
        var filme = leitura.nextLine();

        String url = "https://www.omdbapi.com/?t=" + encodeValue(filme) + "&apikey=c6c97426";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            System.out.println(response.toString());
            System.out.println(json);

            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            TituloOmdb tituloOmdb = gson.fromJson(json, TituloOmdb.class);
            System.out.println(tituloOmdb);

            Titulo meuTitulo = new Titulo(tituloOmdb);
            System.out.println("-----------------------------");
            System.out.println(meuTitulo);

            FileWriter escrita = new FileWriter( filme + ".txt");
            escrita.write(meuTitulo.toString());
            escrita.close();

            File arquivo = new File(filme + ".txt");
            System.out.println(arquivo.exists());

            FileReader reader = new FileReader(arquivo);

            System.out.println("------- LENDO O ARQUIVO -------");
            int data = reader.read();
            while (data != -1) {
                System.out.print((char) data);
                data = reader.read();
            }
            reader.close();


/*
*
* O pacote java.io também fornece outras classes úteis, como:
BufferedReader e BufferedWriter: são usadas para ler e gravar arquivos de texto de maneira eficiente, lendo e escrevendo uma linha por vez. Elas usam um buffer para armazenar os dados, o que torna a leitura e escrita mais rápida do que quando feita um caractere por vez;
FileInputStream e FileOutputStream: são usadas para ler e gravar dados binários em um arquivo. Eles são usados para ler e gravar dados em arquivos que não são de texto, como imagens e arquivos de áudio;
ObjectInputStream e ObjectOutputStream: são usadas para ler e gravar objetos em arquivos. Isso permite que você armazene objetos Java em arquivos para uso posterior ou para transferência entre diferentes aplicações.
*
* */

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

        System.out.println("Fim do programa");
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
