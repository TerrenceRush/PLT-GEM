package buildinClass;

import java.util.Scanner;

public class Battle {

  // Information of this battle.
  private Scanner sc;
  private String display;
  private Unit myBoss;

  public Unit myHero;
  private int skillNum;
  private int round;

  /** 
   * Initiate a battle with battle message and target.
   * @param display display message of this battle.
   * @param target target of this battle.
   */
  public Battle(String display, Unit target) {
    this.display = display;
    this.myBoss = target;
  }

  /**
   *  Copy constructor.
   * @param bat battle to copy from.
   */
  public Battle(Battle bat) {
    this.display = bat.display;
    this.myBoss = bat.myBoss;
  }

  /**
   * Return true or false of victory or defeat.
   * @param hero hero of this battle.
   * @return True for win, False for defeat.
   */
  public boolean trigger(Unit hero) {
    System.out.printf("%s encountered %s, ", hero.name, myBoss.name);
    System.out.println(this.display);

    this.sc = new Scanner(System.in);
    this.round = 0;
    this.myHero = hero;

    while (myBoss.life > 0 && myHero.life > 0) {
      myHero.status();
      myBoss.status();

      // Choose skill from user input.
      if (myHero.skills != null && myHero.skills.length > 0) {
        System.out
            .println("====================================="
                + "===========================================");
        System.out.println("Choose skill, or press enter to auto act:");
        System.out.println("0 - Attack: A \"normally\" effective attack.");
        myHero.showSkills();
        while (true) {
          String command = sc.nextLine();
          if (command.isEmpty()) {
            myHero.auto(myBoss);
            break;
          }
          try {
            skillNum = Integer.parseInt(command) - 1;

            // Chosen normal attack.
            if (skillNum == -1) {
              break;
            }
            if (skillNum < -1 || skillNum >= myHero.skills.length) {
              System.out.println("No such skill.");
              continue;
            }
            if (myHero.chi < myHero.skills[skillNum].cost) {
              System.out.println("You don't have enough chi for this skill.");
              continue;
            }
            myHero.skills[skillNum].cast(myHero);
            break;
          } catch (NumberFormatException nfe) {
            System.out.println("No such command.");
            continue;
          }
        }
      }

      // Automatically choose skill for boss, if any.
      myBoss.auto(myHero);

      System.out
          .println("===================================="
              + "============================================");
      // Hero round.
      // Only attack if no life/chi skill is used.
      myHero.attack(myBoss);
      if (myBoss.life <= 0) {
        roundOver();
        reward();
        return true;
      }

      // Boss round.
      // Only attack if no life/chi skill is used.

      myBoss.attack(myHero);
      if (myHero.life <= 0) {
        System.out.printf("%s was defeated!\n", myHero.name);
        return false;
      }

      // round clean up.
      roundOver();
    }

    // How could you get here!?
    return false;
  }

  /**
   * Check duration of skill and reset status if expired.
   */
  public void roundOver() {
    if (this.myHero.skill != null) {
      this.myHero.skill.cancel(this.myHero);
    }
    if (this.myBoss.skill != null) {
      this.myBoss.skill.cancel(this.myBoss);
    }
    this.round++;
    System.out.printf("round %d over\n\n\n\n", this.round);
  }

  /**
   *  You win the battle, you get good stuff and become stronger.
   *  May get stuff from monsters by adding logic here.
   */
  void reward() {
    System.out.printf("%s defeated %s after %d %s !\n", myHero.name, myBoss.name, round,
        (round > 1) ? "rounds" : "round");
    this.myHero.grow(this.myBoss);
  }
}
