package br.com.renato.screenmatch.principal;

import br.com.renato.screenmatch.model.*;
import br.com.renato.screenmatch.repository.SerieRepository;
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

    private List<DadosSerie> dadosSerieList = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> serieList = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu(){
        var op = -1;
        while (op!=0) {
            var mensagem = """
                    \n
                    *****************************************
                    
                    1 - Buscar séries para adicionar no banco
                    2 - Buscar episódios
                    3 - Listar séries
                    4 - Buscar série pelo título no banco
                    5 - Buscar série pelo ator
                    6 - Top 5
                    7 - Buscar séries pela categoria
                    8 - Filtrar séries por máximo de temporadas e avaliação
                    9 - Buscar episódio por trecho do nome
                    10 - Buscar Top 5 de uma Série
                    11 - Episódios a partir de uma data
                                    
                    0 - Sair      
                    
                    *****************************************                           
                    """;

            System.out.println(mensagem);

            System.out.println("Digite a opção: ");
            op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriesMaxTemporadas();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAPartirDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarEpisodiosAPartirDeUmaData() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano: ");
            var lancamento = sc.nextInt();
            sc.nextLine();

            List<Episodio> episodioAno = repositorio.episodioDaSeriePorAno(serie, lancamento);

            episodioAno.forEach(System.out::println);
        }
    }

    private void buscarTopEpisodiosPorSerie() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();

            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);

            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Avaliação: %s - Episódio %s - %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getAvaliacao(),
                            e.getNumeroEP(), e.getTitulo()));
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual nome do episódio para busca: ");
        var trechoNome = sc.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorTrecho(trechoNome);

        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada: %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEP(), e.getTitulo()));
    }

    private void buscarSeriesMaxTemporadas() {
        System.out.println("Digite o máximo de temporadas desejado: ");
        var maxTemporadas = sc.nextInt();
        sc.nextLine();

        System.out.println("Digite a avaliação mínima: ");
        var avaliacao = sc.nextDouble();

        Optional<List<Serie>> seriesMaxTemporadas = repositorio.seriesTemporadasEAvaliacao(maxTemporadas,avaliacao);

        if(seriesMaxTemporadas.isPresent() && !seriesMaxTemporadas.get().isEmpty()) {
            seriesMaxTemporadas.get().forEach(s ->
                    System.out.println(s.getTitulo() + " com avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Não foi possível fazer essa filtragem com esse número de temporadas e essa avaliação.");
        }
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Digite a categoria: ");
        var nomeCategoria = sc.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);

        Optional<List<Serie>> seriesPorCategoria = repositorio.findByGenero(categoria);

        if (seriesPorCategoria.isPresent() && !seriesPorCategoria.get().isEmpty()){
            seriesPorCategoria.get().forEach(System.out::println);
        } else {
            System.out.println("Não foi possível buscar nenhuma série com a categoria informada.");
        }
    }

    private void buscarTop5Series() {
        Optional<List<Serie>> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        if(serieTop.isPresent() && !serieTop.get().isEmpty()) {
            serieTop.get().forEach(s ->
                    System.out.println(s.getTitulo() + " com avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Não foi possível buscar o Top 5");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator a ser buscado no banco: ");
        var nomeAtor = sc.nextLine();
        System.out.println("Digite a partir de qual avaliação mínima: ");
        double avaliacao = sc.nextDouble();

        Optional<List<Serie>> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avaliacao);

        if (seriesEncontradas.isPresent() && !seriesEncontradas.get().isEmpty()) {
            System.out.println("Séries que o ator trabalhou: ");
            seriesEncontradas.get().forEach(s ->
                    System.out.println(s.getTitulo() + " com avaliação: " + s.getAvaliacao()));
        } else {
            System.out.println("Não encontramos este autor no banco com esta avaliação.");
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Informe o título da série a ser buscada no banco: ");
        var tituloBuscado = sc.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(tituloBuscado);

        if(serieBusca.isPresent()){
            System.out.println("Dados da série encontrada no banco: " + serieBusca.get());
        } else {
            System.out.println("Não foi encontrada nenhuma série com este nome no banco!");
        }
    }

    private void listarSeriesBuscadas() {
        serieList = repositorio.findAll();

        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSerieList.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série que deseja buscar os episódios: ");
        String nomeSerie = sc.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodioList(episodios);
            repositorio.save(serieEncontrada);
        } else{
            System.out.println("Esta série não foi encontrada no banco de dados!");
        }
    }



}

/* menu antigo
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

 */