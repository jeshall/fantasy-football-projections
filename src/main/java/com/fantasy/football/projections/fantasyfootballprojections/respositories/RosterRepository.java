/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.respositories;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Player;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Roster;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "rosters", itemResourceRel = "roster", path = "rosters")
public interface RosterRepository extends JpaRepository<Roster, Long> {

    Roster findByTeam(Team team);

    Roster findRosterByQuarterback(Player player);

    Roster findRosterByRunningBack1(Player player);

    Roster findRosterByRunningBack2 (Player player);

    Roster findRosterByWideReceiver1(Player player);

    Roster findRosterByWideReceiver2(Player player);

    Roster findRosterByTightEnd(Player player);

    Roster findRosterByDefense(Player player);

    Roster findRosterByKicker(Player player);

    Roster findRosterByBench1(Player player);

    Roster findRosterByBench2(Player player);

    Roster findRosterByBench3(Player player);

    Roster findRosterByBench4(Player player);

    Roster findRosterByBench5(Player player);

}
