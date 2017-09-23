/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.services;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Player;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Roster;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;

import java.util.List;

public interface ESPNFantasyClient {

    List<Team> scrapeTeams();

    Roster scrapeTeamRosters(Team team);

    List<Player> scrapePlayers();
}
