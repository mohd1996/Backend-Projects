
package com.lawlite.covid.services.Impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.lawlite.covid.Constants;
import com.lawlite.covid.models.LocationStats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * COVIDDataServiceImpl
 */
@Service
public class COVIDDataServiceImpl {

    // @Autowired
    // private AppConfig config;

    @Autowired
    private RestTemplate restTemplate;

    private List<LocationStats> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 10 * * *")
    public void getData() {
        List<LocationStats> newStats = new ArrayList<>();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(Constants.VIRUS_DATA_URL, String.class);
        assert responseEntity.getStatusCode().equals(HttpStatus.OK) : "Server Error";
        try {
            StringReader csvBodyReader = new StringReader(responseEntity.getBody());
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvBodyReader);

            for (CSVRecord record : records) {
                LocationStats locationStat = new LocationStats();
                locationStat.setState(record.get("Province/State"));
                locationStat.setCountry(record.get("Country/Region"));
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStat.setLatestTotalCases(latestCases);
                locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
                newStats.add(locationStat);
            }
            this.allStats = newStats;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

}