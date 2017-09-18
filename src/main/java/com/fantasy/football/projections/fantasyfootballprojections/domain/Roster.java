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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "qb_id")
    private Player quarterback;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rb_id")
    private Player runningBack1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rb_2_id")
    private Player runningBack2;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wr_1_id")
    private Player wideReceiver1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wr_2_id")
    private Player wideReceiver2;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "te_id")
    private Player tightEnd;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "flex_id")
    private Player flex;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "def_id")
    private Player defense;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "k_id")
    private Player kicker;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "b1_id")
    private Player bench1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "b2_id")
    private Player bench2;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "b3_id")
    private Player bench3;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "b4_id")
    private Player bench4;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "b5_id")
    private Player bench5;
}
