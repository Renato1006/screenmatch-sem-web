package br.com.renato.screenmatch;

import br.com.renato.screenmatch.model.DadosEpisodio;
import br.com.renato.screenmatch.model.DadosSerie;
import br.com.renato.screenmatch.model.DadosTemporada;
import br.com.renato.screenmatch.principal.Principal;
import br.com.renato.screenmatch.service.ConsumoAPI;
import br.com.renato.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal();
		principal.exibeMenu();
	}
}
