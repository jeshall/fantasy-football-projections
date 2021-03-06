/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.bootstrap;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;
import com.fantasy.football.projections.fantasyfootballprojections.respositories.TeamRepository;
import com.fantasy.football.projections.fantasyfootballprojections.services.ESPNFantasyClient;
import com.fantasy.football.projections.fantasyfootballprojections.services.JRIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamUpdater implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ESPNFantasyClient espnFantasyClient;

    @Autowired
    private JRIClient jriClient;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        List<Team> teams = this.espnFantasyClient.scrapeTeams();
//        this.espnFantasyClient.scrapePlayers();
        this.jriClient.scrapeData();
    }
}
