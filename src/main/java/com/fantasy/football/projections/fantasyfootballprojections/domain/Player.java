/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String espnName;

    private String ffAnalyticsName;

    private String espnPlayerId;

    private String position;

    @OneToOne(mappedBy = "id")
    private Roster roster;
}
