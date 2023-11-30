package br.com.renato.screenmatch.principal;

import br.com.renato.screenmatch.model.DadosEpisodio;
import br.com.renato.screenmatch.model.DadosSerie;
import br.com.renato.screenmatch.model.DadosTemporada;
import br.com.renato.screenmatch.model.Episodio;
import br.com.renato.screenmatch.service.ConsumoAPI;
import br.com.renato.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        List<DadosEpisodio> dadosEpisodios = temporadaList.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nMostrando os dez episódios mais avaliados: ");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(10)
                .map(e -> e.titulo().toUpperCase())
                .forEach(System.out::println);

        System.out.println("\nDados dos episódios por temporada: ");
        List<Episodio> episodios = temporadaList.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("\nDigite um trecho de um título a ser buscado: ");
        var trechoTitulo = sc.nextLine();

        Optional<Episodio> episodioOptional = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

        if(episodioOptional.isPresent()){
            System.out.println("Episódio foi encontrado! Na temporada " + episodioOptional.get().getTemporada());
        } else {
            System.out.println("Episódio não foi encontrado...");
        }

        System.out.println("Digite a partir de que ano você deseja ver os episódios: ");
        int ano = sc.nextInt();
        sc.nextLine();

        LocalDate anoBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(anoBusca))
                .forEach(e -> System.out.println(
                        "\nTemporada: " + e.getTemporada() +
                        "\nEpisódio: " + e.getTitulo() +
                        "\nData lançamento: " + e.getDataLancamento().format(dtf)
                ));

        Map<Integer,Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao()>0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter((e -> e.getAvaliacao()>0.0))
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média dos episódios da série: " + est.getAverage());
        System.out.println("Melhor nota de um episódio: " + est.getMax());
        System.out.println("Menor nota de um episódio: " + est.getMin());
        System.out.println("Quantidade de avaliações: " + est.getCount());
    }
}