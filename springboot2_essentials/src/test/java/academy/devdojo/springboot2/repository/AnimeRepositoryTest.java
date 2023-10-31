package academy.devdojo.springboot2.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.util.AnimeCreator;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
	
	@Autowired
	private AnimeRepository animeRepository;
	
	@Test
	@DisplayName("Save persists anime when successfull")
	void save_PersistAnime_WhenSuccessfull() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime savedAnime = this.animeRepository.save(animeToBeSaved);
		
		assertNotEquals(savedAnime, null);
		assertNotEquals(savedAnime.getId(), null);
		assertEquals(animeToBeSaved.getName(), savedAnime.getName());
	}
	
	@Test
	@DisplayName("Save updates anime when successfull")
	void save_UpdateAnime_WhenSuccessfull() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime savedAnime = this.animeRepository.save(animeToBeSaved);
		
		savedAnime.setName("Overlord");
		Anime updatedAnime = this.animeRepository.save(savedAnime);
		
		assertNotEquals(updatedAnime, null);
		assertNotEquals(updatedAnime.getId(), null);
		assertEquals(savedAnime.getName(), updatedAnime.getName());
	}
	
	@Test
	@DisplayName("Save deletes anime when successfull")
	void delete_RemovesAnime_WhenSuccessfull() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime savedAnime = this.animeRepository.save(animeToBeSaved);
		
		this.animeRepository.delete(savedAnime);
		
		Optional<Anime> animeOptional = this.animeRepository.findById(animeToBeSaved.getId());
		
		assertEquals(animeOptional.isEmpty(), true);
	}
	
	@Test
	@DisplayName("Find By Name retunrs list of anime when successfull")
	void findByName_ReturnsListOfAnime_WhenSuccessfull() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime savedAnime = this.animeRepository.save(animeToBeSaved);
		
		String name = savedAnime.getName();
		
		List<Anime> animes = this.animeRepository.findByName(name);
		
		assertEquals(animes.isEmpty(), false);
		assertEquals(animes.contains(savedAnime), true);
	}
	
	@Test
	@DisplayName("Find By Name retunrs empty list of anime when no anime is found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		List<Anime> animes = this.animeRepository.findByName("Boku no Hero");
		
		assertNotEquals(animes, null);
		assertEquals(animes.isEmpty(), true);
	}
	
	@Test
	@DisplayName("Save throw ConstraintViolationException when name is empty")
	void save_ThrowsConstraintViolationException_WhenNameIsEmpty(){
		Anime anime = new Anime();
		
		assertThrows(ConstraintViolationException.class, () -> this.animeRepository.save(anime));
	}
	
}
