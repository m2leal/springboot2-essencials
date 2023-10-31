package academy.devdojo.springboot2.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class) // Não inicia o contexto inteiro do Spring
class AnimeControllerTest {

	@InjectMocks // Testa a classe
	private AnimeController animeController;

	@Mock // Testa as variáveis da classe
	private AnimeService animeServiceMock;

	@BeforeEach
	void setUp() {
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any())).thenReturn(animePage);
		
		BDDMockito.when(animeServiceMock.listAllNonPageable()).thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong())).thenReturn(AnimeCreator.createValidAnime());
		
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class))).thenReturn(AnimeCreator.createValidAnime());
		
		BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
		
		BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
	}

	@Test
	@DisplayName("List returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		Page<Anime> animePage = animeController.list(null).getBody();

		assertNotEquals(animePage, null);
		assertEquals(animePage.toList().isEmpty(), false);
		assertEquals(animePage.toList().get(0).getName(), expectedName);
		
	}
	
	@Test
	@DisplayName("ListAll returns list of anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeController.listAll().getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("Find By Id returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Long expectedId = AnimeCreator.createValidAnime().getId();

		Anime anime = animeController.findById(1L).getBody();

		assertNotEquals(anime, null);
		assertEquals(anime.getId(), expectedId);
		
	}
	
	@Test
	@DisplayName("Find By Name returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeController.findByName("anything").getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.size(), 1);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("Find By Name returns an empty list of anime when anime is not found")
	void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		//Sobrescreve o comportamento definido no setUp()
		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
		
		List<Anime> animes = animeController.findByName("anything").getBody();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), true);
	}
	
	@Test
	@DisplayName("Save returns anime when successfull")
	void save_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

		assertNotEquals(anime, null);
		assertEquals(anime, AnimeCreator.createValidAnime());
	}
	
	@Test
	@DisplayName("Replace update anime when successfull")
	void replace_UpdateAnime_WhenSuccessfull() {
		
		assertDoesNotThrow(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()));
		
		ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());
		assertNotEquals(entity, null);
		assertEquals(entity.getStatusCode(), HttpStatus.NO_CONTENT);
	}
	
	@Test
	@DisplayName("Delete removes anime when successfull")
	void delete_RemovesAnime_WhenSuccessfull() {
		
		assertDoesNotThrow(() -> animeController.delete(1L));
		
		ResponseEntity<Void> entity = animeController.delete(1L);
		assertNotEquals(entity, null);
		assertEquals(entity.getStatusCode(), HttpStatus.NO_CONTENT);
	}

}
