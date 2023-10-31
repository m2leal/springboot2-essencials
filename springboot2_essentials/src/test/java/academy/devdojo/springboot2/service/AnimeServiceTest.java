package academy.devdojo.springboot2.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class) // Não inicia o contexto inteiro do Spring e.g. Banco de Dados
class AnimeServiceTest {
	
	@InjectMocks // Testa a classe
	private AnimeService animeService;

	@Mock // Testa as variáveis da classe
	private AnimeRepository animeRepositoryMock;

	@BeforeEach
	void setUp() {
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);
		
		BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(List.of(AnimeCreator.createValidAnime()));
		
		BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class))).thenReturn(AnimeCreator.createValidAnime());
		
		BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
	}

	@Test
	@DisplayName("listAll returns list of anime inside page object when successful")
	void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

		assertNotEquals(animePage, null);
		assertEquals(animePage.toList().isEmpty(), false);
		assertEquals(animePage.toList().get(0).getName(), expectedName);
		
	}
	
	@Test
	@DisplayName("listAll returns list of anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeService.listAllNonPageable();

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
	void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
		Long expectedId = AnimeCreator.createValidAnime().getId();

		Anime anime = animeService.findByIdOrThrowBadRequestException(1L);

		assertNotEquals(anime, null);
		assertEquals(anime.getId(), expectedId);
		
	}
	
	@Test
	@DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when anime is not found")
	void findByIdOrThrowBadRequestException_ThrowsBadRequestException_WhenSuccessful() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
		
		assertThrows(BadRequestException.class, () -> animeService.findByIdOrThrowBadRequestException(1L));
		
	}
	
	@Test
	@DisplayName("findByName returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeService.findByName("anything");

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.size(), 1);
		assertEquals(animes.get(0).getName(), expectedName);
	}
	
	@Test
	@DisplayName("findByName returns an empty list of anime when anime is not found")
	void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		//Sobrescreve o comportamento definido no setUp()
		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
		
		List<Anime> animes = animeService.findByName("anything");

		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), true);
	}
	
	@Test
	@DisplayName("save returns anime when successfull")
	void save_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
		Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

		assertNotEquals(anime, null);
		assertEquals(anime, AnimeCreator.createValidAnime());
	}
	
	@Test
	@DisplayName("replace update anime when successfull")
	void replace_UpdateAnime_WhenSuccessfull() {
		
		assertDoesNotThrow(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()));
		
	}
	
	@Test
	@DisplayName("delete removes anime when successfull")
	void delete_RemovesAnime_WhenSuccessfull() {
		
		assertDoesNotThrow(() -> animeService.delete(1L));
		
	}
}
