package org.example.url;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public long saveUrl(UrlDTO url) {
        var urlRecord = urlRepository.findByUrl(url.getUrl());
        if (urlRecord == null) {
            urlRecord = new UrlRecord();
            urlRecord.setUrl(url.getUrl());
            urlRepository.save(urlRecord);
        }

        return urlRecord.getId();
    }

    @Transactional
    public String getUrl(long id) {
        var url = urlRepository.findById(id);
        if (url.isEmpty())
            return null;

        var urlRecord = url.get();

        urlRecord.setLastAccess(new Date());
        urlRecord.setCount(urlRecord.getCount() + 1);
        urlRepository.save(urlRecord);

        return urlRecord.getUrl();
    }

    @Transactional(readOnly = true)
    public List<UrlStatDTO> getStatistics(Pageable pageable) {
        var urls = urlRepository.findAll(pageable);
        var result = new ArrayList<UrlStatDTO>();

        for (var url : urls) {
            var stat = new UrlStatDTO();
            stat.setLastAccess(url.getLastAccess());
            stat.setRedirects(url.getCount());
            stat.setUrl(url.getUrl());

            result.add(stat);
        }

        return result;
    }
}
