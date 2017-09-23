/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.respositories;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Roster;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "teams", itemResourceRel = "team", path = "teams")
public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByRoster(Roster roster);

    Team findTeamByTeamId(String teamId);
}
