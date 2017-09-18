/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.services;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ESPNFantasyJsoupClient implements ESPNFantasyClient {

    @Value("${fantasy.football.projections.host}")
    private String host;

    @Value("${fantasy.football.projections.path.base}")
    private String basePath;

    @Value("${fantasy.football.projections.path.owner-info}")
    private String ownerInfoPath;

    @Value("${fantasy.football.projections.path.league-rosters}")
    private String leaugeRostersPath;

    @Value("${fantasy.football.projections.path.clubhouse}")
    private String clubhousePath;

    @Value("${fantasy.football.projections.path.standings}")
    private String standingsPath;

    @Value("${fantasy.football.projections.path.free-agency}")
    private String freeAgencyPath;

    @Value("${fantasy.football.projections.path.quick-box-score}")
    private String quickBoxScorePath;

    @Value("${fantasy.football.projections.league-id}")
    private String leagueId;

    @Value("${fantasy.football.projections.team-id}")
    private String teamId;

    @Override
    public List<Team> scrapeTeams() {
        try {
            String seasonId = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

            String leagueRosterUriString = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(this.host)
                .pathSegment(this.basePath, this.leaugeRostersPath)
                .queryParam("leagueId", this.leagueId)
                .queryParam("seasonId", seasonId)
                .build().toUriString();

            Document document = Jsoup.connect(leagueRosterUriString).get();
            return document.select(".playerTableTable tbody .playerTableBgRowHead th a")
                    .parallelStream()
                    .map(element -> {
                        try {
                            String href = element.attr("href");
                            String teamName = element.text();
                            String parentText = element.parent().text();
                            Matcher recordMatcher = Pattern.compile("\\((?<wins>\\d+?)-(?<losses>\\d+?)(-(?<ties>\\d+?))?\\)").matcher(parentText);
                            recordMatcher.find();
                            String wins = recordMatcher.group("wins");
                            String losses = recordMatcher.group("losses");
                            String ties = recordMatcher.group("ties");

                            UriComponents clubhouseUriComponents = UriComponentsBuilder.fromUriString(href)
                                    .scheme("http")
                                    .host(this.host)
                                    .build();

                            String teamId = clubhouseUriComponents.getQueryParams().getFirst("teamId");

                            Document clubhouseDocument = Jsoup.connect(clubhouseUriComponents.toUriString()).get();
                            String division = clubhouseDocument.select(".games-univ-mod1 p strong").first().text();

                            String standingsUriString = UriComponentsBuilder.newInstance()
                                    .scheme("http")
                                    .host(this.host)
                                    .pathSegment(this.basePath, this.standingsPath)
                                    .queryParam("leagueId", this.leagueId)
                                    .queryParam("seasonId", seasonId)
                                    .build()
                                    .toUriString();



                            Team team = Team.builder()
                                    .teamId(teamId)
                                    .name(teamName)
                                    .wins(Integer.valueOf(wins))
                                    .losses(Integer.valueOf(losses))
                                    .ties((ties == null) ? 0 : Integer.valueOf(ties))
                                    .division(division)

                                    .build();
                            return team;
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }).collect(Collectors.toList());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
