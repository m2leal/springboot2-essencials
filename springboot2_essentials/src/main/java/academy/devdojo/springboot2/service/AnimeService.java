package academy.devdojo.springboot2.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnimeService {
	
	private final AnimeRepository animeRepository;
	
	public Page<Anime> listAll(Pageable pageable) {
		return animeRepository.findAll(pageable);
	}
	
	public List<Anime> listAllNonPageable() {
		return animeRepository.findAll();
	}
	
	public List<Anime> findByName(String name) {
		return animeRepository.findByName(name);
	}
	
	public Anime findByIdOrThrowBadRequestException(Long id) {
		return animeRepository
				.findById(id)
				.orElseThrow(() -> new BadRequestException("Anime Not Found"));
	}
	
	@Transactional
	public Anime save(AnimePostRequestBody animePostRequestBody) {
		Anime anime = AnimeMapper.INSTANCE.toAnime(animePostRequestBody);
		return animeRepository.save(anime);
	}

	public void delete(Long id) {
		animeRepository.delete(findByIdOrThrowBadRequestException(id));
	}

	public void replace(AnimePutRequestBody animePutRequestBody) {
		Anime savedAnime = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
		Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
		
		anime.setId(savedAnime.getId());

		animeRepository.save(anime);
	}

}
