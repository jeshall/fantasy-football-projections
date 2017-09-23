/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.services;

import lombok.ToString;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Vector;
import java.util.stream.IntStream;

@Service
public class JRIClient {

    @Autowired
    private Rengine rEngine;

    @Value("${fantasy.football.projections.csv.location}")
    String csvLocation;

    public void scrapeData() {

        this.rEngine.eval("library('ffanalytics')");
        this.rEngine.eval("library('rjava')");



        IntStream.rangeClosed(1, 1).parallel().forEach( i -> {

            String scrapeDataString = String.format("scrapeData%d <- runScrape(week = %d, season = 2016, analysts = c(-1, 5, 7, 18, 27), positions = c('QB', 'RB', 'WR', 'TE', 'K', 'DST'))", i,  i);

            this.rEngine.eval(  scrapeDataString);

        });

        IntStream.rangeClosed(1, 1).forEach( i -> {
            String csvName = String.format("c:/Users/Jesse/code/fantasy-football-projections/csv/projections-2016-week-%d.csv", i);
            String writeCSVString = String.format("write.csv(myProjections$projections, file='%s', row.names=FALSE)", csvName);
            String myProjectionsString =  String.format("myProjections <- getProjections(scrapeData%d, avgMethod = 'weighted', leagueScoring = scoringRules, teams = 12, format = 'standard', mflMocks = 0, mflLeagues = 0, adpSources = c('CBS', 'ESPN', 'FFC', 'MFL', 'NFL'))", i);
            this.rEngine.eval(myProjectionsString);
            this.rEngine.eval(writeCSVString);
        });



    }
}
