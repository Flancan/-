package ru.iu3.rpo.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.rpo.backend.models.Artist;
import ru.iu3.rpo.backend.models.Country;
import ru.iu3.rpo.backend.models.Painting;
import ru.iu3.rpo.backend.models.Museum;
import ru.iu3.rpo.backend.repositories.ArtistRepository;
import ru.iu3.rpo.backend.repositories.CountryRepository;
import ru.iu3.rpo.backend.repositories.MuseumRepository;
import ru.iu3.rpo.backend.repositories.PaintingRepository;

import java.util.*;

@RestController
@RequestMapping("/api/v1")

public class PaintingController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    PaintingRepository paintingRepository;
    @GetMapping("/paintings")
    public List<Painting> getAllPaintings() {
        return paintingRepository.findAll();
    }

    @GetMapping("/artists/{id}/paintings")
    public ResponseEntity<List<Painting>> getArtistPaintings(@PathVariable(value = "id") Long artistId) {
        Optional<Artist> cc = artistRepository.findById(artistId);
        if (cc.isPresent())
        {
            return ResponseEntity.ok(cc.get().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getMuseumPaintings(@PathVariable(value = "id") Long museumId) {
        Optional<Museum> cc = museumRepository.findById(museumId);
        if (cc.isPresent())
        {
            return ResponseEntity.ok(cc.get().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@Validated @RequestBody Painting painting) {
        try {
            Optional<Artist> cc = artistRepository.findById(painting.artist.id);
            cc.ifPresent(artist -> painting.artist = artist);
            Painting na = paintingRepository.save(painting);
            return new ResponseEntity<Object>(na, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("paintings.name_UNIQUE"))
                error = "painting already exists";
            else
                error = "undefined error";
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        }
    }

    @DeleteMapping("/paintings/{id}")
    public Map<String, Boolean> deletePainting(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting> painting = paintingRepository.findById(paintingId);
        Map<String, Boolean> response = new HashMap<>();
        if (painting.isPresent()) {
            paintingRepository.delete(painting.get());
            response.put("deleted", Boolean.TRUE);
        }
        else {
            response.put("deleted", Boolean.FALSE);
        }
        return response;
    }
}
