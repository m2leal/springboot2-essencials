package academy.devdojo.springboot2.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Teste da aplicação inteira em porta randomica
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) //Limpa o contexto (banco) antes de cada @Test
class AnimeControllerIT {
	
	@Autowired
	@Qualifier(value = "testRestTemplateRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;
	
	@Autowired
	@Qualifier(value = "testRestTemplateRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;
	
	@Autowired
	private AnimeRepository animeRepository;
	
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;
	
	private static final DevDojoUser USER = DevDojoUser
			.builder()
			.name("DevDojo Academy")
			.username("devdojo")
			.password("$2a$10$17IIQMs8be.LCkz1/ne4f.aHg.SsrTXW5Pp2dF5z7k4Gk/lFd2gla")
			.authorities("ROLE_USER")
			.build();
	
	private static final DevDojoUser ADMIN = DevDojoUser
			.builder()
			.name("William Suane")
			.username("william")
			.password("$2a$10$17IIQMs8be.LCkz1/ne4f.aHg.SsrTXW5Pp2dF5z7k4Gk/lFd2gla")
			.authorities("ROLE_USER,ROLE_ADMIN")
			.build();
	
	@TestConfiguration
	@Lazy
	static class Config {
		
		@Bean(name = "testRestTemplateRoleUser")
		TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("devdojo", "academy");
			
			return new TestRestTemplate(restTemplateBuilder);
		}
		
		@Bean(name = "testRestTemplateRoleAdmin")
		TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("william", "academy");
			
			return new TestRestTemplate(restTemplateBuilder);
		}
		
	}
	
	@Test
	@DisplayName("List returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		String expectedName = savedAnime.getName();

		PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
				new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();

		assertNotEquals(animePage, null);
		assertEquals(animePage.toList().isEmpty(), false);
		assertEquals(animePage.toList().get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("ListAll returns list of anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		String expectedName = savedAnime.getName();

		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("Find By Id returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		Long expectedId = AnimeCreator.createValidAnime().getId();
		
		Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);
		
		assertNotEquals(savedAnime, null);
		assertEquals(anime.getId(), expectedId);
		
	}
	
	@Test
	@DisplayName("Find By Name returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		String expectedName = savedAnime.getName();
		
		String url = String.format("/animes/find?name=%s", expectedName);
		List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.size(), 1);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("Find By Name returns an empty list of anime when anime is not found")
	void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		devDojoUserRepository.save(USER);
		
		List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Anime>>() {}).getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), true);
	}
	
	@Test
	@DisplayName("Save returns anime when successfull")
	void save_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);

		assertNotEquals(animeResponseEntity, null);
		assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.CREATED);
		assertNotEquals(animeResponseEntity.getBody(), null);
		assertNotEquals(animeResponseEntity.getBody().getId(), null);
	}
	
	@Test
	@DisplayName("Replace update anime when successfull")
	void replace_UpdateAnime_WhenSuccessfull() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		savedAnime.setName("new name");
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

		assertNotEquals(animeResponseEntity, null);
		assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("Delete removes anime when successfull")
	void delete_RemovesAnime_WhenSuccessfull() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(ADMIN);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, savedAnime.getId());

		assertNotEquals(animeResponseEntity, null);
		assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("Delete returns 403 when user is not admin")
	void delete_Returns403_WhenUserIsNotAdmin() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
		
		devDojoUserRepository.save(USER);
		
		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, savedAnime.getId());

		assertNotEquals(animeResponseEntity, null);
		assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
	}
}
