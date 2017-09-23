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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "players", itemResourceRel = "player", path = "players")
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findPlayerByRoster(Roster roster);

    Player findPlayerByEspnPlayerId(String espnPlayerId);

    List<Player> findPlayerByPosition(@Param("position") String position);
}
