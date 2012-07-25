package edu.uccs.ecgs.utility;

public enum FitEvalTypes {
  FINISH_ORDER("Finish Order"),
  NET_WORTH("Net Worth"),
  NUM_MONOPOLIES("Number of Monopolies"), 
  NUM_PROPERTIES("Number of Properties"),
  NUM_WINS("Number of Wins"),
  TOURNAMENT("Tournament");

  private String name;

  private FitEvalTypes(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
