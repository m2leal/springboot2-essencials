package academy.devdojo.springboot2.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringClient {

	public static void main(String[] args) {

//==================================================GET==================================================
		ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 2);
		log.info(entity);

		Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 2);
		log.info(object);

		Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
		log.info(Arrays.toString(animes));
		
		// Transforma o Array retornado em Lista - Substitui o casting
		ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange(
						"http://localhost:8080/animes/all",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<>(){});
		log.info(exchange.getBody());
		
//==================================================POST==================================================
//		Anime kingdome = Anime.builder().name("Kingdome").build();
//		Anime kingdomeSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdome, Anime.class);
//		log.info(kingdomeSaved);
		
//		Anime samuraiChamploo = Anime.builder().name("Smurai Champloo").build();
//		ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange(
//				"http://localhost:8080/animes",
//				HttpMethod.POST,
//				new HttpEntity<Anime>(samuraiChamploo),
//				Anime.class);
//		log.info(samuraiChamplooSaved);
		
		// Mesmo resultado do código acima mas passando JSON ao invés de Objeto
		Anime samuraiChamploo = Anime.builder().name("Smurai Champloo").build();
		ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange(
				"http://localhost:8080/animes",
				HttpMethod.POST,
				new HttpEntity<Anime>(samuraiChamploo, createJsonHeaders()),
				Anime.class);
		log.info(samuraiChamplooSaved);
		
		Anime animeToBeUpdated = samuraiChamplooSaved.getBody();
		animeToBeUpdated.setName("Samurai Champloo 2");
		
		ResponseEntity<Void> samuraiChamplooUpdated = new RestTemplate().exchange(
				"http://localhost:8080/animes",
				HttpMethod.PUT,
				new HttpEntity<>(animeToBeUpdated, createJsonHeaders()),
				Void.class);
		log.info(samuraiChamplooUpdated);
		
		ResponseEntity<Void> samuraiChamplooDelete = new RestTemplate().exchange(
				"http://localhost:8080/animes/{id}",
				HttpMethod.DELETE,
				null,
				Void.class,
				animeToBeUpdated.getId());
		log.info(samuraiChamplooDelete);
	}
	
	public static HttpHeaders createJsonHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

}
