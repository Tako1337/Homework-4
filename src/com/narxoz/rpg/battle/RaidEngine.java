package com.narxoz.rpg.battle;

import com.narxoz.rpg.bridge.Skill;
import com.narxoz.rpg.composite.CombatNode;

import java.util.Random;

public class RaidEngine {
    private static final int MAX_ROUNDS = 50;
    private Random random = new Random(1L);

    public RaidEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public RaidResult runRaid(CombatNode teamA, CombatNode teamB, Skill teamASkill, Skill teamBSkill) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams must not be null");
        }
        if (teamASkill == null || teamBSkill == null) {
            throw new IllegalArgumentException("Skills must not be null");
        }
        if (!teamA.isAlive() || !teamB.isAlive()) {
            throw new IllegalArgumentException("Both teams must be alive before raid starts");
        }

        RaidResult result = new RaidResult();
        result.addLine("Raid started: " + teamA.getName() + " vs " + teamB.getName());
        result.addLine(teamA.getName() + " uses " + teamASkill.getSkillName() + " (" + teamASkill.getEffectName() + ")");
        result.addLine(teamB.getName() + " uses " + teamBSkill.getSkillName() + " (" + teamBSkill.getEffectName() + ")");

        int rounds = 0;

        while (teamA.isAlive() && teamB.isAlive() && rounds < MAX_ROUNDS) {
            rounds++;
            result.addLine("");
            result.addLine("Round " + rounds);

            if (teamA.isAlive()) {
                boolean critA = random.nextInt(100) < 10;
                int beforeHealthB = teamB.getHealth();

                if (critA) {
                    result.addLine(teamA.getName() + " lands a critical strike!");
                    teamASkill.cast(teamB);
                }
                teamASkill.cast(teamB);

                int dealtA = Math.max(0, beforeHealthB - teamB.getHealth());
                result.addLine(teamA.getName() + " attacks " + teamB.getName()
                        + " with " + teamASkill.getSkillName()
                        + " [" + teamASkill.getEffectName() + "]"
                        + " for " + dealtA + " total damage.");
                result.addLine(teamB.getName() + " remaining HP: " + teamB.getHealth());
            }

            if (!teamB.isAlive()) {
                break;
            }

            if (teamB.isAlive()) {
                boolean critB = random.nextInt(100) < 10;
                int beforeHealthA = teamA.getHealth();

                if (critB) {
                    result.addLine(teamB.getName() + " lands a critical strike!");
                    teamBSkill.cast(teamA);
                }
                teamBSkill.cast(teamA);

                int dealtB = Math.max(0, beforeHealthA - teamA.getHealth());
                result.addLine(teamB.getName() + " attacks " + teamA.getName()
                        + " with " + teamBSkill.getSkillName()
                        + " [" + teamBSkill.getEffectName() + "]"
                        + " for " + dealtB + " total damage.");
                result.addLine(teamA.getName() + " remaining HP: " + teamA.getHealth());
            }
        }

        result.setRounds(rounds);

        if (teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner(teamA.getName());
        } else if (teamB.isAlive() && !teamA.isAlive()) {
            result.setWinner(teamB.getName());
        } else if (!teamA.isAlive() && !teamB.isAlive()) {
            result.setWinner("Draw");
        } else {
            result.setWinner("No winner (max rounds reached)");
        }

        result.addLine("");
        result.addLine("Raid finished.");
        result.addLine("Winner: " + result.getWinner());
        return result;
    }
}