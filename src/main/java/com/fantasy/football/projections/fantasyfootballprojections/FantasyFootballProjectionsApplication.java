package com.fantasy.football.projections.fantasyfootballprojections;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FantasyFootballProjectionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyFootballProjectionsApplication.class, args);
    }


    @Bean
    public Rengine rEngine() {
        Rengine engine = new Rengine(new String[] {}, false, null);
        REXP libPathsExp = engine.eval(".libPaths('C:/Users/Jesse/Documents/R/win-library/3.4')");
        return engine;
    }
}
