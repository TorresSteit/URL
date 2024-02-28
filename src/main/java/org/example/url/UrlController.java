package org.example.url;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("shorten")
    public UrlResultDTO shorten(@RequestBody UrlDTO urlDTO) {
        long id = urlService.saveUrl(urlDTO);

        var res = new UrlResultDTO();
        res.setUrl(urlDTO.getUrl());
        res.setShortUrl("http://localhost:8888/my/" + Long.toString(id));

        return res;
    }


    @GetMapping("my/{id}")
    public ResponseEntity<Void> redirect(@PathVariable("id") Long id) {
        var url = urlService.getUrl(id);
        if (url == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("stat")
    public List<UrlStatDTO> stat(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page
    ) {
        return urlService.getStatistics(PageRequest.of(page, 5));
    }
}
}
