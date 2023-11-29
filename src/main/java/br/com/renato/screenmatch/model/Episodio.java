package br.com.renato.screenmatch.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer numeroEP;
    private Double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(Integer numeroTemp, DadosEpisodio dadosEP) {
        this.temporada = numeroTemp;
        this.titulo = dadosEP.titulo();
        this.numeroEP = dadosEP.numero();

        try{
            this.avaliacao = Double.valueOf(dadosEP.avaliacao());
        } catch (NumberFormatException e){
            this.avaliacao = 0.0;
        }

        try{
            this.dataLancamento = LocalDate.parse(dadosEP.dataLancamento());
        } catch (DateTimeParseException e){
            this.dataLancamento = null;
        }

    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEP() {
        return numeroEP;
    }

    public void setNumeroEP(Integer numeroEP) {
        this.numeroEP = numeroEP;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        return  "temporada=" + temporada +
                ", titulo='" + titulo +
                ", numeroEP=" + numeroEP +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento;
    }
}
