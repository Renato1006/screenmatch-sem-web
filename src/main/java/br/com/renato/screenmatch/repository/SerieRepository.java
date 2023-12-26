package br.com.renato.screenmatch.repository;

import br.com.renato.screenmatch.model.Categoria;
import br.com.renato.screenmatch.model.Episodio;
import br.com.renato.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    Optional<List<Serie>> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    Optional<List<Serie>> findTop5ByOrderByAvaliacaoDesc();

    Optional<List<Serie>> findByGenero(Categoria categoria);

    Optional<List<Serie>> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer maxTemporadas, Double avaliacao);

    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :maxTemporadas AND s.avaliacao >= :avaliacao")
    Optional<List<Serie>> seriesTemporadasEAvaliacao(Integer maxTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE e.titulo ILIKE %:trechoNome% ")
    List<Episodio> episodioPorTrecho(String trechoNome);

    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodioList e WHERE s = :serie AND YEAR(e.dataLancamento) >= :lancamento")
    List<Episodio> episodioDaSeriePorAno(Serie serie, int lancamento);
}
