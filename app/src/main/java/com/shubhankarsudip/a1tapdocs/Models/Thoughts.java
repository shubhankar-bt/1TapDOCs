package com.shubhankarsudip.a1tapdocs.Models;

public class Thoughts {
   public String thoughts;
   public String mood;

    public Thoughts(String thoughts, String mood) {
        this.thoughts = thoughts;
        this.mood = mood;
    }

    public Thoughts() {
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
