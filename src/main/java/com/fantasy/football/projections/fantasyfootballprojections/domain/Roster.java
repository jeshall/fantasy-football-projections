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

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "qb_id")
    private Player quarterback;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "rb_1_id")
    private Player runningBack1;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "rb_2_id")
    private Player runningBack2;


    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "wr_1_id")
    private Player wideReceiver1;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "wr_2_id")
    private Player wideReceiver2;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "te_id")
    private Player tightEnd;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "flex_id")
    private Player flex;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "def_id")
    private Player defense;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "k_id")
    private Player kicker;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "b1_id")
    private Player bench1;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "b2_id")
    private Player bench2;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "b3_id")
    private Player bench3;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "b4_id")
    private Player bench4;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "b5_id")
    private Player bench5;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;
}
