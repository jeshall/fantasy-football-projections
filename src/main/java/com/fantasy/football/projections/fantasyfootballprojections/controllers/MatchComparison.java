/*
 * Lexmark Confidential
 *
 * Copyright (c) 2017 Lexmark International Inc.
 *
 * All Rights Reserved.
 */

package com.fantasy.football.projections.fantasyfootballprojections.controllers;

import lombok.Builder;
import lombok.Data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class MatchComparison {


    @Data
    @Builder
    public static class Projection {
        private String name;
        private String points;
        private String floor;
        private String ceiling;
    }
    @Data
    @Builder
    public static class Matchup {

        private String homeTeam;
        private String awayTeam;
        
        private List<Projection> homeStarters;
        private List<Projection> homeBench;
        private List<Projection> awayStarters;
        private List<Projection> awayBench;
        
        private Double homeStartersTotalPoints;
        private Double homeBenchTotalPoints;
        private Double awayStartersTotalPoints;
        private Double awayBenchTotalPoints;

        private Double homeStartersTotalFloor;
        private Double homeBenchTotalFloor;
        private Double awayStartersTotalFloor;
        private Double awayBenchTotalFloor;

        private Double homeStartersTotalCeiling;
        private Double homeBenchTotalCeiling;
        private Double awayStartersTotalCeiling;
        private Double awayBenchTotalCeiling;
    }


    public String getName(String name) {
        if (Objects.equals(name, "Robert Kelley")) {
            return "Rob Kelley";
        }
        else if (Objects.equals(name, "Bennie Fowler III")) {
            return "Bennie Fowler";
        }
        else if (name.matches(".+?Jr\\.")) {
            return name.split("\\sJr\\.")[0];
        }
        else if (name.matches(".+?D/ST")) {
            return name.split("\\sD/ST")[0];
        }
        return name;
    }

    public Projection elementToProjection(Element element, Map<String, Map<String, String>> projections) {
        try {
            String name = this.getName(element.text());
            Map<String, String> projection = projections.get(name);
            String floor = projection.get("lower");
            String ceiling = projection.get("upper");
            String points = projection.get("points");
            return Projection.builder()
                    .name(name)
                    .floor(floor)
                    .ceiling(ceiling)
                    .points(points)
                    .build();
        }
        catch (NullPointerException e) {
            throw e;
        }
    }

    public Matchup getCurrenMatchup() throws IOException {


        Map<String, Map<String, String>> projections = this.getProjections();
        Document document = Jsoup.connect("http://games.espn.com/ffl/boxscorequick?leagueId=1543015&teamId=13&scoringPeriodId=2&seasonId=2017&view=scoringperiod&version=quick").get();

        Elements homeTeam = document.select("#playertable_0 .playerTableBGRowHead td");
        Elements awayTeam = document.select("#playertable_2 .playerTableBGRowHead td");
        Elements homeStarters = document.select("#playertable_0 tbody .pncPlayerRow .playertablePlayerName .flexpop");
        Elements homeBench = document.select("#playertable_1 tbody .pncPlayerRow .playertablePlayerName .flexpop");
        Elements awayStarters = document.select("#playertable_2 tbody .pncPlayerRow .playertablePlayerName .flexpop");
        Elements awayBench = document.select("#playertable_3 tbody .pncPlayerRow .playertablePlayerName .flexpop");





        Matchup matchup = Matchup.builder()
                .homeTeam(homeTeam.text().split("\\sBox\\sScore")[0])
                .awayTeam(awayTeam.text().split("\\sBox\\sScore")[0])
                .homeStarters(homeStarters.stream()
                        .filter(s -> !s.text().isEmpty())
                        .map(element -> this.elementToProjection(element, projections))
                        .collect(Collectors.toList()))
                .homeBench(homeBench.stream()
                        .filter(s -> !s.text().isEmpty())
                        .map(element -> this.elementToProjection(element, projections))
                        .collect(Collectors.toList()))
                .awayStarters(awayStarters.stream()
                        .filter(s -> !s.text().isEmpty())
                        .map(element -> this.elementToProjection(element, projections))
                        .collect(Collectors.toList()))
                .awayBench(awayBench.stream()
                        .filter(s -> !s.text().isEmpty())
                        .map(element -> this.elementToProjection(element, projections))
                        .collect(Collectors.toList()))
                .build();


        matchup.setHomeStartersTotalPoints(matchup.getHomeStarters()
                .stream()
                .filter(projection -> projection.getPoints() != null)
                .mapToDouble( p -> Double.valueOf(p.getPoints())).sum());
        matchup.setHomeStartersTotalFloor(matchup.getHomeStarters()
                .stream()
                .filter(projection -> projection.getFloor() != null)
                .mapToDouble( p -> Double.valueOf(p.getFloor())).sum());
        matchup.setHomeStartersTotalCeiling(matchup.getHomeStarters()
                .stream()
                .filter(projection -> projection.getCeiling() != null)
                .mapToDouble( p -> Double.valueOf(p.getCeiling())).sum());

        matchup.setHomeBenchTotalPoints(matchup.getHomeBench()
                .stream()
                .filter(projection -> projection.getPoints() != null)
                .mapToDouble( p -> Double.valueOf(p.getPoints())).sum());
        matchup.setHomeBenchTotalFloor(matchup.getHomeBench()
                .stream()
                .filter(projection -> projection.getFloor() != null)
                .mapToDouble( p -> Double.valueOf(p.getFloor())).sum());
        matchup.setHomeBenchTotalCeiling(matchup.getHomeBench()
                .stream()
                .filter(projection -> projection.getCeiling() != null)
                .mapToDouble( p -> Double.valueOf(p.getCeiling())).sum());

        matchup.setAwayStartersTotalPoints(matchup.getAwayStarters()
                .stream()
                .filter(projection -> projection.getPoints() != null)
                .mapToDouble( p -> Double.valueOf(p.getPoints())).sum());
        matchup.setAwayStartersTotalFloor(matchup.getAwayStarters()
                .stream()
                .filter(projection -> projection.getFloor() != null)
                .mapToDouble( p -> Double.valueOf(p.getFloor())).sum());
        matchup.setAwayStartersTotalCeiling(matchup.getAwayStarters()
                .stream()
                .filter(projection -> projection.getCeiling() != null)
                .mapToDouble( p -> Double.valueOf(p.getCeiling())).sum());

        matchup.setAwayBenchTotalPoints(matchup.getAwayBench()
                .stream()
                .filter(projection -> projection.getPoints() != null)
                .mapToDouble( p -> Double.valueOf(p.getPoints())).sum());
        matchup.setAwayBenchTotalFloor(matchup.getAwayBench()
                .stream()
                .filter(projection -> projection.getFloor() != null)
                .mapToDouble( p -> Double.valueOf(p.getFloor())).sum());
        matchup.setAwayBenchTotalCeiling(matchup.getAwayBench()
                .stream()
                .filter(projection -> projection.getCeiling() != null)
                .mapToDouble( p -> Double.valueOf(p.getCeiling())).sum());
        
        return matchup;
    }

    public Map<String, Map<String, String>> getProjections() throws IOException {

        Map<String, Map<String, String>> projections = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get("/users/jeshall/tmp/projections.csv"));
        List<String> headers = Arrays.stream(lines.remove(0).split(","))
                .map(s -> s.replace("\"", ""))
                .collect(Collectors.toList());
        lines.forEach(line ->{
            List<String> cols = Arrays.asList(line.split(","));
            Map<String, String> projection = new HashMap<>();
            cols.forEach(col -> {
                projection.put(headers.get(cols.indexOf(col)), col.replace("\"", ""));
            });
            projections.put(cols.get(headers.indexOf("player")).replace("\"", ""), projection);
        });

        return projections;

    }

    public List<Projection> getFreeAgents(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements elements = document.select(".pncPlayerRow .playertablePlayerName .flexpop");

        Map<String, Map<String, String>> projections = this.getProjections();
        return elements.stream()
                .filter(element -> !element.text().isEmpty())
                .filter(element -> projections.containsKey(element.text()))
                .map(element -> this.elementToProjection(element, projections))
                .filter(projection -> projection.getPoints() != null && !projection.getPoints().isEmpty())
                .filter(projection -> projection.getCeiling() != null && !projection.getCeiling().isEmpty())
                .filter(projection -> projection.getFloor() != null && !projection.getFloor().isEmpty())
                .sorted((p1, p2) ->
                        Comparator.comparingDouble((Projection pPoints) -> Double.valueOf(pPoints.getPoints()))
                                .thenComparingDouble((Projection pFloor) -> Double.valueOf(pFloor.getFloor()))
                                .thenComparingDouble((Projection pCeiling) -> Double.valueOf(pCeiling.getCeiling()))
                                .reversed()
                                .compare(p1, p2))
                .collect(Collectors.toList());
    }


    @RequestMapping("/weekly-matchup")
    public String getMatchup(Model model) throws IOException {

        Matchup matchup = this.getCurrenMatchup();

        model.addAttribute("matchup", matchup);
        model.addAttribute("freeQBs", this.getFreeAgents("http://games.espn.com/ffl/freeagency?leagueId=1543015&teamId=13&seasonId=2017&slotCategoryId=0&context=freeagency&view=overview&sortMap=AAAAARgAAAAHAQAMc3RhdFNlYXNvbklkAwAAB%2BEBAAhjYXRlZ29yeQMAAAAEAQAPc2NvcmluZ1BlcmlvZElkAwAAAAIBAAlkaXJlY3Rpb24D%2F%2F%2F%2F%2FwEABmNvbHVtbgP%2F%2F%2F%2F9AQAQc3RhdFNvdXJjZVR5cGVJZAMAAAABAQALc3RhdFF1ZXJ5SWQDAAAAAA%3D%3D"));
        model.addAttribute("freeRBs", this.getFreeAgents("http://games.espn.com/ffl/freeagency?leagueId=1543015&teamId=13&seasonId=2017&slotCategoryId=2&context=freeagency&view=overview&sortMap=AAAAARgAAAAHAQAMc3RhdFNlYXNvbklkAwAAB%2BEBAAhjYXRlZ29yeQMAAAAEAQAPc2NvcmluZ1BlcmlvZElkAwAAAAIBAAlkaXJlY3Rpb24D%2F%2F%2F%2F%2FwEABmNvbHVtbgP%2F%2F%2F%2F9AQAQc3RhdFNvdXJjZVR5cGVJZAMAAAABAQALc3RhdFF1ZXJ5SWQDAAAAAA%3D%3D"));
        model.addAttribute("freeWRs", this.getFreeAgents("http://games.espn.com/ffl/freeagency?leagueId=1543015&teamId=13&seasonId=2017&slotCategoryId=4&context=freeagency&view=overview&sortMap=AAAAARgAAAAHAQAMc3RhdFNlYXNvbklkAwAAB%2BEBAAhjYXRlZ29yeQMAAAAEAQAPc2NvcmluZ1BlcmlvZElkAwAAAAIBAAlkaXJlY3Rpb24D%2F%2F%2F%2F%2FwEABmNvbHVtbgP%2F%2F%2F%2F9AQAQc3RhdFNvdXJjZVR5cGVJZAMAAAABAQALc3RhdFF1ZXJ5SWQDAAAAAA%3D%3D"));

        return "index";
    }

    @RequestMapping("/gold-mining")
    @ResponseBody
    public String goldMine() throws IOException {
        Document document = Jsoup.connect("http://fantasyfootballanalytics.net/wp-content/uploads/2017/09/RoundUp-2017-W1.html").get();
        Element element = document.getElementById("DataTables_Table_0");
        return document.html();
    }
}

