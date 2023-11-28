package br.com.renato.screenmatch.principal;

import br.com.renato.screenmatch.model.DadosSerie;
import br.com.renato.screenmatch.model.DadosTemporada;
import br.com.renato.screenmatch.service.ConsumoAPI;
import br.com.renato.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String SEASON = "&season=";
    private final String API_KEY = "&apikey=7c41444";

    public void exibeMenu(){
        System.out.println("Digite o nome da série: ");
        String name = sc.nextLine();

        String json = consumo.obterDados(ENDERECO + name.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadaList = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + name.replace(" ", "+") + SEASON + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadaList.add(dadosTemporada);
        }

        temporadaList.forEach(System.out::println);

        temporadaList.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}