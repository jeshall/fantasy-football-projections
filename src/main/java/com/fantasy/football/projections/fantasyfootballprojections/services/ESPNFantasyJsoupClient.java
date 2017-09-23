/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.services;

import com.fantasy.football.projections.fantasyfootballprojections.domain.Performance;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Player;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Roster;
import com.fantasy.football.projections.fantasyfootballprojections.domain.Team;
import com.fantasy.football.projections.fantasyfootballprojections.respositories.PerformanceRepository;
import com.fantasy.football.projections.fantasyfootballprojections.respositories.PlayerRepository;
import com.fantasy.football.projections.fantasyfootballprojections.respositories.RosterRepository;
import com.fantasy.football.projections.fantasyfootballprojections.respositories.TeamRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ESPNFantasyJsoupClient implements ESPNFantasyClient {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RosterRepository rosterRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

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

    @Value("${fantasy.football.projections.path.leaders}")
    private String leadersPath;


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

                            Team existingTeam = this.teamRepository.findTeamByTeamId(teamId);
                            Team team = (existingTeam == null)
                                    ? Team.builder()
                                        .teamId(teamId)
                                        .name(teamName)
                                        .wins(Integer.valueOf(wins))
                                        .losses(Integer.valueOf(losses))
                                        .ties((ties == null) ? 0 : Integer.valueOf(ties))
                                        .division(division)
                                        .build()
                                    : Team.builder()
                                        .id(existingTeam.getId())
                                        .teamId(teamId)
                                        .name(teamName)
                                        .wins(Integer.valueOf(wins))
                                        .losses(Integer.valueOf(losses))
                                        .ties((ties == null) ? 0 : Integer.valueOf(ties))
                                        .division(division)
                                        .build();

                            team.setRoster(this.scrapeTeamRosters(team, document));


                            return this.teamRepository.save(team);
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

    @Override
    public Roster scrapeTeamRosters(Team team) {
        try {
            String leagueRosterUriString = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host(this.host)
                    .pathSegment(this.basePath, this.leaugeRostersPath)
                    .queryParam("leagueId", this.leagueId)
                    .queryParam("seasonId", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                    .build()
                    .toUriString();

                Document document = Jsoup.connect(leagueRosterUriString).get();

                this.scrapeTeamRosters(team, document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Player> scrapePlayers() {
        IntStream.of(0, 2, 4, 6, 16, 17)
                .forEach(slotId -> {
                    IntStream.rangeClosed(1, 13).forEach(week -> {
                        IntStream.of(0, 40, 80, 120, 160, 200, 240, 280, 320, 360, 400)
                                .filter(startIndex  -> startIndex < 401)
                                .forEach(startIndex -> {
                                    try {
                                        String leadersUriString = UriComponentsBuilder.newInstance()
                                            .scheme("http")
                                            .host(this.host)
                                            .pathSegment(this.basePath, this.leadersPath)
                                            .queryParam("seasonId", "2016")
                                            .queryParam("scoringPeriodId", week)
                                            .queryParam("slotCategoryId", slotId)
                                            .queryParam("startIndex", startIndex)
                                            .build()
                                            .toUriString();

                                        Document leadersDocument = Jsoup.connect(leadersUriString).get();
                                        Elements playerRows = leadersDocument.select(".pncPlayerRow");
                                        playerRows.stream()
                                                .forEach(playerrRow -> {
                                                    String espnPlayerName = playerrRow.select(".playertablePlayerName a").first().text();
                                                    String espnPlayerId = playerrRow.select(".playertablePlayerName a").first().attr("playerid");
                                                    String position = (slotId == 0) ? "QB"
                                                            : (slotId == 2) ? "RB"
                                                            : (slotId == 4) ? "WR"
                                                            : (slotId == 6) ? "TE"
                                                            : (slotId == 16) ? "D/ST"
                                                            : "K";
                                                    String actualString = playerrRow.select(".appliedPoints").first().text();
                                                    Double actual = (Objects.equals(actualString, "--")) ? 0.0 : Double.valueOf(actualString);

                                                    String  outcome = (playerRows.select(".gameStatusDiv").isEmpty()) ? "Bye"
                                                            :  playerRows.select(".gameStatusDiv a").first().text().contains("W" ) ? "Win"
                                                            : "Loss";
                                                    Player player = Player.builder()
                                                                    .espnName(espnPlayerName)
                                                                    .espnPlayerId(espnPlayerId)
                                                                    .position(position)
                                                                    .build();
                                                    Player existingPlayer = this.playerRepository.findPlayerByEspnPlayerId(espnPlayerId);

                                                    if (existingPlayer != null) {
                                                        player.setId(existingPlayer.getId());
                                                        player.setRoster(existingPlayer.getRoster());
                                                        player.setPerformances(existingPlayer.getPerformances());
                                                    }
                                                    player = this.playerRepository.save(player);
                                                    Performance performance = Performance.builder()
                                                                                .week(String.valueOf(week))
                                                                                .year("2016")
                                                                                .outcome(outcome)
                                                                                .actual(actual)
                                                                                .build();
                                                    performance.setPlayer(player);
                                                    performance  = this.performanceRepository.save(performance);

//                                                    List<Performance> performances = player.getPerformances();
//                                                    if (performances == null) {
//                                                        performances = new ArrayList<>();
//                                                    }
//                                                    performances.add(performance);
//                                                    player.setPerformances(performances);
//                                                    player = this.playerRepository.save(player);

                                                });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                });
        });

        return null;
    }

    public Roster scrapeTeamRosters(Team team, Document document) {

        Function<Element, Player> elementPlayerFunction = (Element playerElement) -> {
            Element playerNameElement = playerElement.select(".playertablePlayerName a").first();
            String espnPlayerId = playerNameElement.attr("playerid");
            String espnPlayerName = playerNameElement.text();
            Player existingPlayer = this.playerRepository.findPlayerByEspnPlayerId(playerNameElement.attr("playerid"));

            return this.playerRepository.save((existingPlayer == null)
                    ? Player.builder()
                        .espnPlayerId(espnPlayerId)
                        .espnName(espnPlayerName)
                        .position(playerElement.select(".playerSlot").text())
                        .build()
                    : Player.builder()
                        .id(existingPlayer.getId())
                        .espnPlayerId(espnPlayerId)
                        .espnName(espnPlayerName)
                        .position(playerElement.select(".playerSlot").text())
                        .build());
        };

        Element playerTableElement = document.select(".playerTableTable")
                .stream()
                .filter(element -> Objects.equals(element.select(".playerTableBgRowHead th a").first().text(), team.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);



        Element qbElement = playerTableElement.select(".slot_0").first();
        Elements rbElements = playerTableElement.select(".slot_2");
        Elements wrElements = playerTableElement.select(".slot_4");
        Element teElement = playerTableElement.select(".slot_6").first();
        Element flexElement = playerTableElement.select(".slot_23").first();
        Element defenseElement = playerTableElement.select(".slot_16").first();
        Element kElement = playerTableElement.select(".slot_17").first();
        Elements benchElements = playerTableElement.select(".slot_20");

        Player quarterback = elementPlayerFunction.apply(qbElement.parent());
        Player runningback1 = elementPlayerFunction.apply(rbElements.first().parent());
        Player runningback2 = elementPlayerFunction.apply(rbElements.get(1).parent());
        Player wideReceiver1 = elementPlayerFunction.apply(wrElements.first().parent());
        Player wideReceiver2 = elementPlayerFunction.apply(wrElements.get(1).parent());
        Player tightEnd = elementPlayerFunction.apply(teElement.parent());
        Player flex = elementPlayerFunction.apply(flexElement.parent());
        Player defense = elementPlayerFunction.apply(defenseElement.parent());
        Player kicker = elementPlayerFunction.apply(kElement.parent());
        Player bench1 = elementPlayerFunction.apply(benchElements.first().parent());
        Player bench2 = elementPlayerFunction.apply(benchElements.get(1).parent());
        Player bench3 = elementPlayerFunction.apply(benchElements.get(2).parent());
        Player bench4 = elementPlayerFunction.apply(benchElements.get(3).parent());
        Player bench5 = elementPlayerFunction.apply(benchElements.get(4).parent());

        Roster roster =  Roster.builder()
                .quarterback(quarterback)
                .runningBack1(runningback1)
                .runningBack2(runningback2)
                .wideReceiver1(wideReceiver1)
                .wideReceiver2(wideReceiver2)
                .tightEnd(tightEnd)
                .flex(flex)
                .defense(defense)
                .kicker(kicker)
                .bench1(bench1)
                .bench2(bench2)
                .bench3(bench3)
                .bench4(bench4)
                .bench5(bench5)
                .build();

        if (team.getId() != null) {
            Roster existingRoster = this.rosterRepository.findByTeam(team);
            if (existingRoster != null) {
             roster.setId(existingRoster.getId());
            }
        }
        return this.rosterRepository.save(roster);

    }
}
