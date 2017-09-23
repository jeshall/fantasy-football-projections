/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.respositories;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Performance;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Player;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "performances", itemResourceRel = "performance", path = "performances")
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Performance findPerformanceByPlayerAndWeekAndYear(Player player, String week, String year);
}
