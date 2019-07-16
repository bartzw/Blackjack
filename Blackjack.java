package nl.qien.weekopdrachtenBlackjack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Blackjack {
    public static String[] kaartSoort = {"Harten", "Ruiten", "Schoppen", "Klaver"};
    public static String[] kaartNummer = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Boer", "Vrouw", "Heer", "Aas"};

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        KaartSpel.maakSpel(kaartSoort, kaartNummer);
        System.out.print("\n" + "Welkom bij Blackjack!");
        System.out.print(" Hoeveel personen willen een gokje wagen? : ");
        int numberPeople = Integer.parseInt(reader.nextLine());
        Speler1.registreerSpelers(numberPeople);
        BlackjackSpelen spel = new BlackjackSpelen();
        spel.speelBlackjack();
        //KaartSpel.laatResterendeKaartenZien();
    }
}

class BlackjackSpelen {

    ArrayList<Speler1> spelers = Speler1.getSpelers();
    static boolean eersteSpel = false;
    void speelBlackjack() {
        spelers.forEach(Speler1 -> {
            if (!Speler1.getDealer()) {
                inlegRonde(Speler1.getSpelerNr());
            }
        });
        if(eersteSpel) {
            Speler1.kaartenDelen();
        }
        eersteSpel = true;
        spelers.forEach(Speler1 -> {
            System.out.println(Speler1.getSpelernaam() + " Je hebt de volgende kaarten:");
            for (int i = 0; i < Speler1.getSpelerKaartenLijst().size(); ++i) {
                System.out.println(Speler1.getSpelerKaartenLijst().get(i).getKaart());
            }
            System.out.println("De dealer heeft de volgende kaart liggen: " + Speler1.getDealerBeginKaart().get(0).getKaart());
            if(Speler1.getwilSpelen()) {
                if (!Speler1.getDealer()) {
                    rondeTwee(Speler1.getSpelerNr());
                } else {
                    speelDealerRonde(Speler1.getSpelerNr());
                }
            } else {
                System.out.println(Speler1.getSpelernaam() + " je hebt helaas geen geld meer, je kan pinnen bij ons.");
            }
        });
        spelers.forEach(Speler1 -> {
            if (!Speler1.getDealer()) {
                checkWinnaar(Speler1.getSpelerPunten(), Speler1.getDealerPunten(), Speler1.getSpelerNr());
            }
            resetSpeler(Speler1.getSpelerNr());
            System.out.println("exit");
        });
        KaartSpel.maakSpel(Blackjack.kaartSoort, Blackjack.kaartNummer);
        speelBlackjack();
    }
    private void inlegRonde(int spelernr) {
        Scanner reader1 = new Scanner(System.in);
        System.out.println("");
        if (spelers.get(spelernr).getTotaalGeld() > 0) {
            System.out.println(spelers.get(spelernr).getSpelernaam() + " hoeveel wil je inzetten? Je hebt nog: " + spelers.get(spelernr).getTotaalGeld() + "euro om in te zetten");
            double inzet = (reader1.nextDouble());
            if (checkInzet(inzet, spelers.get(spelernr).getSpelerNr())) {
                spelers.get(spelernr).setInzet(inzet);
                System.out.println(spelers.get(spelernr).getSpelernaam() + " heeft " + inzet + "euro ingezet. " + spelers.get(spelernr).getTotaalGeld());
            } else {
                inzet = spelers.get(spelernr).getTotaalGeld();
                System.out.println(spelers.get(spelernr).getSpelernaam() + " je hebt meer ingezet dan je aan geld hebt, je inzet is nu: " + inzet);
                spelers.get(spelernr).setInzet(inzet);
            }
        } else {
            spelers.get(spelernr).setWilSpelen(false);
            System.out.println("Je hebt helaas geen geld meer om te spelen :(");
        }
    }

    private void rondeTwee(int spelerNr) {
        Scanner reader1 = new Scanner(System.in);
        while (spelers.get(spelerNr).getVolgendeKaart() && !spelers.get(spelerNr).getSpelerTeveelPunten()) {
            System.out.println("Speler: " + spelers.get(spelerNr).getSpelernaam() + " De totale waarde van je kaarten is: " + spelers.get(spelerNr).getSpelerPunten());
            System.out.println("Wanneer je een kaart wilt typ dan de letter: ");
            System.out.println("k : voor nog een kaart.");
            System.out.println("p : om te passen.");
            String invoer1 = reader1.nextLine();
            if (invoer1.equals("k")) {
                Kaart1 kaart = KaartSpel.pakKaart(spelers.get(spelerNr).getSpelerNr());
                System.out.println("Je hebt de volgende kaart getrokken = " + kaart.getKaart());
                System.out.println("Je kaarten hebben een totale waarde van: " + spelers.get(spelerNr).getSpelerPunten());
                if (spelers.get(spelerNr).getSpelerPunten() > 21) {
                    spelers.get(spelerNr).setSpelerTeveelPunten(true);
                }
                if(spelers.get(spelerNr).getSpelerPunten() == 21) {
                    spelers.get(spelerNr).setVolgendeKaart(false);
                    System.out.println("U heeft BLACKJACK!");
                }
            }
            else if (invoer1.equals("p")) {
                spelers.get(spelerNr).setVolgendeKaart(false);
                System.out.println(spelers.get(spelerNr).getSpelernaam() + " hoeft geen kaarten meer.");
                System.out.println("Je kaarten hebben een totale waarde van: " + spelers.get(spelerNr).getSpelerPunten());
            } else {
                System.out.println("invoeraa = " + invoer1);
            }
        }
    }

    private void speelDealerRonde(int spelerNr) {
        while (spelers.get(spelerNr).getSpelerPunten() <= 16) {
            KaartSpel.pakKaart(spelers.get(spelerNr).getSpelerNr());
        }
    }

    private boolean checkInzet(double inzet, int spelerNr) {
        return spelers.get(spelerNr).getTotaalGeld() >= inzet;
    }

    private void checkWinnaar(int spelerTotaal, int dealerTotaal, int spelerNr) {
        if (spelerTotaal > 21) {
            spelers.get(spelerNr).setInzet(0);
            System.out.println(spelers.get(spelerNr).getSpelernaam() + " helaas, je hebt meer dan 21, dus je bent je inzet kwijt.");
            System.out.println("Je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
        if (dealerTotaal > 21) {
            if (spelerTotaal < 21) {
                double totaalGeldPlus = spelers.get(spelerNr).getInzet() * 2;
                spelers.get(spelerNr).setTotaalGeld(totaalGeldPlus);
                spelers.get(spelerNr).setInzet(0);
                System.out.println(spelers.get(spelerNr).getSpelernaam() + " lekker, de dealer heeft meer dan 21! Je wint 1x je inleg.");
                System.out.println("Je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
            }
        }
        if (spelerTotaal == 21 && dealerTotaal != 21) {
            double totaalGeldPlus = spelers.get(spelerNr).getInzet()*2 + spelers.get(spelerNr).getInzet()*1.5;
            spelers.get(spelerNr).setTotaalGeld(totaalGeldPlus);
            spelers.get(spelerNr).setInzet(0);
            System.out.println(spelers.get(spelerNr).getSpelernaam() + " lekker, je hebt BLACKJACK! je wint 1,5 keer in inleg.");
            System.out.println("je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
        if (spelerTotaal != 21 && dealerTotaal == 21) {
            double totaalGeldPlus = spelers.get(spelerNr).getInzet()*2 + spelers.get(spelerNr).getInzet()*1.5;
            spelers.get(spelerNr).setInzet(0);
            System.out.println(spelers.get(spelerNr).getSpelernaam() + " potjandorie, de dealert heeft BLACKJACK! Je bent je geld kwijt");
            System.out.println("je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
        if (spelerTotaal == dealerTotaal && spelerTotaal < 22) {
                double totaalGeldPlus = spelers.get(spelerNr).getInzet();
                spelers.get(spelerNr).setTotaalGeld(totaalGeldPlus);
                spelers.get(spelerNr).setInzet(0);
                System.out.println(spelers.get(spelerNr).getSpelernaam() + " jij en de dealer hebben hetzelfde aantal punten, je inzet is weer terug bij je geld.");
                System.out.println("je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
        if (spelerTotaal < 22 && dealerTotaal < 22 && spelerTotaal > dealerTotaal) {
            double totaalGeldPlus = spelers.get(spelerNr).getInzet()*2;
            spelers.get(spelerNr).setTotaalGeld(totaalGeldPlus);
            spelers.get(spelerNr).setInzet(0);
            System.out.println(spelers.get(spelerNr).getSpelernaam() + " lekker, je hebt meer punten dan de dealer, je wint 1x je inzet.");
            System.out.println("Je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
        if (dealerTotaal < 22 && spelerTotaal < dealerTotaal) {
            spelers.get(spelerNr).setInzet(0);
                System.out.println(spelers.get(spelerNr).getSpelernaam() + " helaas, de dealer heeft meer punten, dus je bent je inzet kwijt.");
            System.out.println("Je totale geld is nu: " + spelers.get(spelerNr).getTotaalGeld());
        }
    }
    private void resetSpeler(int spelerNr) {
        int k = spelers.get(spelerNr).getSpelerKaartenLijst().size();
        for (int j = 0; j < k; j++) {
            spelers.get(spelerNr).getSpelerKaartenLijst().remove(0);
            spelers.get(spelerNr).setWilSpelen(true);
            spelers.get(spelerNr).setVolgendeKaart(true);
            spelers.get(spelerNr).setSpelerTeveelPunten(false);
            spelers.get(spelerNr).resetSpelerPunten();
        }
    }
}

class KaartSpel {
    static ArrayList<Kaart1> spel = new ArrayList<>();

    static Kaart1 pakKaart(int i) {
        Kaart1 kaart = spel.remove(0);
        Speler1.Spelers.get(i).setKaartSpeler(kaart);
        Speler1.Spelers.get(i).setSpelerPunten(kaart.getwaarde());
        return kaart;
    }

    public static void laatResterendeKaartenZien() {
        System.out.println("De volgende kaarten zitten nog in het spel: ");
        for (Kaart1 kaart1 : spel) {
            System.out.println("Kaart " + kaart1.getKaart());
        }
    }

    static void maakSpel(String[] kaartSoort, String[] kaartNummer) {
        for (String s : kaartSoort) {
            for (String value : kaartNummer) {
                Kaart1 kaart = new Kaart1(s, value, value);
                spel.add(kaart);
            }
        }
        Collections.shuffle(spel);
    }
}

class Kaart1 {
    private String kaartSoort;
    private String kaartNummer;
    private String kaartWaarde;

    Kaart1(String kaartSoort, String kaartNummer, String kaartWaarde) {
        this.kaartSoort = kaartSoort;
        this.kaartNummer = kaartNummer;
        this.kaartWaarde = kaartWaardeInt(kaartWaarde);
        //KaartSpel.laatResterendeKaartenZien();
    }

    public String kaartWaardeInt(String kaartWaarde) {
        if (kaartWaarde.equals("Boer") || kaartWaarde.equals("Vrouw") || kaartWaarde.equals("Heer")) {
            kaartWaarde = "10";
        } else if (kaartWaarde.equals("Aas")) {
            kaartWaarde = "11";
        }
        return kaartWaarde;
    }

    public String getKaart() {
        String kaartNaam = kaartSoort + " " + kaartNummer;
        return kaartNaam;
    }

    public int getwaarde() {
        int waarde = Integer.parseInt(kaartWaarde);
        return waarde;
    }
}

class Speler1 {
    static ArrayList<Speler1> Spelers = new ArrayList<Speler1>();
    private int spelerNr;
    private String spelerNaam;
    private ArrayList<Kaart1> spelerKaartenLijst;
    private int spelerPunten;
    private boolean spelerVolgendeKaart;
    private boolean spelerTeveelPunten;
    private boolean dealer;
    private boolean wilSpelen;
    private double inzet;
    private double totaalGeld;

    public static void kaartenDelen() {
        int numberPeople = Spelers.size()-1;
        for (int i = 0; i < numberPeople; i++) {
            KaartSpel.pakKaart(i);
            KaartSpel.pakKaart(i);
        }
        KaartSpel.pakKaart(numberPeople);
    }
    public static void registreerSpelers(int numberPeople) {
        Scanner reader = new Scanner(System.in);
        for (int i = 0; i < numberPeople; i++) {
            String SpelerNaam;
            System.out.println("Hallo speler " + (i + 1) + ", wat is jouw naam?:");
            SpelerNaam = reader.nextLine();
            Spelers.add(new Speler1(i, SpelerNaam, new ArrayList<Kaart1>(), 0, true, false, false, true, 0.00, 100.00));
            KaartSpel.pakKaart(i);
            KaartSpel.pakKaart(i);
        }
        Spelers.add(new Speler1(numberPeople, "Dealer", new ArrayList<Kaart1>(), 0, true, false, true, true, 0.00, 10000.00));
        KaartSpel.pakKaart(numberPeople);
    }

    public Speler1(int spelerNr, String spelerNaam, ArrayList<Kaart1> spelerKaartenLijst, int spelerPunten, boolean spelerVolgendeKaart, boolean spelerTeveelPunten, boolean dealer, boolean wilSpelen, double inzet, double totaalGeld) {
        this.spelerNr = spelerNr;
        this.spelerNaam = spelerNaam;
        this.spelerKaartenLijst = spelerKaartenLijst;
        this.spelerPunten = spelerPunten;
        this.spelerVolgendeKaart = spelerVolgendeKaart;
        this.dealer = dealer;
        this.wilSpelen = wilSpelen;
        this.inzet = inzet;
        this.totaalGeld = totaalGeld;
    }

    public static ArrayList<Speler1> getSpelers() {
        return Spelers;
    }

    public String getSpelernaam() {
        return spelerNaam;
    }

    public ArrayList<Kaart1> getSpelerKaartenLijst() {
        return spelerKaartenLijst;
    }

    public int getSpelerNr() {
        return spelerNr;
    }

    public int getSpelerPunten() {
        return spelerPunten;
    }

    public int getDealerPunten() {
        int i = Spelers.size() - 1;
        int dealerPunten = Spelers.get(i).spelerPunten;
        return dealerPunten;
    }

    public ArrayList<Kaart1> getDealerBeginKaart() {
        int i = Spelers.size() - 1;
        ArrayList dealerKaart = Spelers.get(i).spelerKaartenLijst;
        return dealerKaart;
    }

    public int setSpelerPunten(int spelerPunten) {
        this.spelerPunten = this.spelerPunten + spelerPunten;
        return this.spelerPunten;
    }
    public int resetSpelerPunten() {
        this.spelerPunten = 0;
        return this.spelerPunten;
    }
    public boolean getVolgendeKaart() {
        return spelerVolgendeKaart;
    }

    public boolean setVolgendeKaart(boolean spelerVolgendeKaart) {
        this.spelerVolgendeKaart = spelerVolgendeKaart;
        return this.spelerVolgendeKaart;
    }

    public boolean getSpelerTeveelPunten() {
        return spelerTeveelPunten;
    }

    public boolean getDealer() {
        return dealer;
    }

    public boolean setSpelerTeveelPunten(boolean spelerTeveelPunten) {
        this.spelerTeveelPunten = spelerTeveelPunten;
        return this.spelerTeveelPunten;
    }

    public Kaart1 setKaartSpeler(Kaart1 kaart) {
        spelerKaartenLijst.add(kaart);
        return null;
    }

    public double setInzet(double inzet) {
        totaalGeld = totaalGeld - inzet;
        this.inzet = inzet;
        return this.inzet;
    }

    public double getInzet() {
        return inzet;
    }

    public double getTotaalGeld() {
        return totaalGeld;
    }
    public double setTotaalGeld(double totaalGeld) {
        this.totaalGeld = this.totaalGeld + totaalGeld;
        return totaalGeld;
    }

    public boolean setWilSpelen(boolean wilSpelen) {
        this.wilSpelen = wilSpelen;
        return this.wilSpelen;
    }
    public boolean getwilSpelen() {
        return wilSpelen;
    }
}