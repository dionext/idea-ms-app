package com.dionext.ideaportal.db;


import java.text.ParseException;
import java.util.Locale;

@SuppressWarnings({"java:S1192", "java:S3776", "java:S1172"})
public class HistoricalDate {

    private static final String[] romeDigits = new String[]{"I", "II", "III", "IV",
            "V", "VI", "VI",
            "VIII", "IX", "X",
            "XI", "XII", "XIII",
            "XIV", "XV", "XVI",
            "XVII", "XVIII", "XIX",
            "XX", "XXI", "XXII",
            "XXIII", "XXIV", "XXV",
            "XXVI", "XXVII",
            "XXVIII", "XXIX",
            "XXX"};
    private static final String[] eras = new String[]{"до н.э.", "н.э."};
    private static final String[][] histDateWords = new String[][]{
            {"G", null, "г."}, {"O", "около", "г."},
            {"?", "неизв.(?)", null}, {"-", "нет даты", null},
            {"B", "начало", "века"}, {"F", "первая пол.", "века"},
            {"V", null, "век"}, {"M", "серед.", "века"},
            {"S", "вторая пол.", "века"}, {"E", "конец", "века"},
            {"P", "начало", "тысяч."}, {"R", "первая пол.", "тысяч."},
            {"T", null, "тысяч."}, {"C", "серед.", "тысяч."},
            {"X", "вторая пол.", "тысяч."}, {"Y", "конец", "тысяч."},
            {"A", "не важно", null}
    };
    private static final Locale locale = Locale.ENGLISH;
    private int ilc = -1;
    private int era = -1;
    private int iyear = -1;    //пр¤мой формат нэ и обратный (для показа) до нэ
    private int icenture = -1;
    private int imillenium = -1;

    public static String[][] getHistDateWords(Locale locale) {
        return histDateWords;
    }

    public static String[] getHistDateWordYear(Locale locale) {
        return histDateWords[0];
    }

    public static String[] getRomeDigits(Locale locale) {
        return romeDigits;
    }

    public static String[] getEras(Locale locale) {
        return eras;
    }

    public boolean isNull() {

        if (ilc == -1) {
            return true;
        }
        if (this.isNeedYear()) {
            return (era == -1) || (iyear == -1);
        }
        return false;
    }

    public int getYear() {
        return iyear;
    }

    public int getEra() {
        return era;
    }

    public int getCenture() {
        return icenture;
    }

    public int getMillenium() {
        return imillenium;
    }

    public int getHistDateWord() {
        return ilc;
    }

    private void parseHistDateWord(String input) throws ParseException {

        String[][] words = HistoricalDate.getHistDateWords(Locale.ENGLISH);
        for (int i = 0; i < words.length; i++) {
            if (words[i][0].charAt(0) == input.charAt(0)) {
                ilc = i;
                break;
            }
        }
        if (ilc == -1) {
            throw new ParseException("Not valid last symbol", 5);
        }
    }

    private void parseEra(String input) throws ParseException {

        if (input.equals("0")) {
            era = 0;
        } else if (input.equals("1")) {
            era = 1;
        } else if (input.equals("9")) {
            era = 9;
        } else {
            throw new ParseException("Not valid era symbol: " + input, 0);
        }
    }

    private void parseYear(String input, int inputEra, boolean fromBase)
            throws ParseException {

        try {
            iyear = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ParseException("NumberFormatException", 1);
        }
        if (iyear > 9999) {
            throw new ParseException("Not supported year", 1);
        }
        if ((fromBase) && (inputEra == 0)) {
            iyear = 10000 - iyear;
        }

        if ((iyear / 100 > 30)
                && !((iyear / 100 == 99) && (!this.isNeedYear()))) {
            throw new ParseException("Not supported centure", 1);
        }
        if ((iyear / 1000 > 4)
                && !((iyear / 1000 == 9) && (!this.isNeedYear()))) {
            throw new ParseException("Not supported millenium", 1);
        }

        icenture = iyear / 100;
        imillenium = iyear / 1000;
    }


    /**
     * parse date
     */
    public void parseHistDate(String histDate) throws ParseException {

        if ((histDate.length() < 6) || (histDate.length() > 6)) {
            throw new ParseException("Not valid length", 0);
        }

        String lc = histDate.substring(5);
        this.parseHistDateWord(lc);
        this.parseEra(histDate.substring(0, 1));
        this.parseYear(histDate.substring(1, 5), this.getEra(), true);
    }

    public String formatHistDateForView() {

        String[][] words = HistoricalDate.getHistDateWords(locale);
        String[] word = words[this.getHistDateWord()];
        char lc = word[0].charAt(0);
        if ((lc == 'A') || (lc == '-')) {
            return "";
        }
        if (lc == '?') {
            return "?";
        }
        StringBuilder out = new StringBuilder();
        String[] romes = HistoricalDate.getRomeDigits(locale);
        if (word[1] != null) {
            out.append(word[1]);
            out.append(" ");
        }
        if ((lc == 'G') || (lc == 'O')) {
            out.append(iyear);
            out.append(" ");
            out.append(word[2]);
        } else if ((lc == 'B') || (lc == 'F') || (lc == 'V') || (lc == 'M')
                || (lc == 'S') || (lc == 'E')) {
            out.append(romes[icenture]);
            out.append(" ");
            out.append(word[2]);
        } else if ((lc == 'P') || (lc == 'R') || (lc == 'T') || (lc == 'C')
                || (lc == 'X') || (lc == 'Y')) {
            out.append(romes[imillenium]);
            out.append(" ");
            out.append(word[2]);
        }
        if (era == 0) {
            out.append(" ");
            out.append(HistoricalDate.getEras(locale)[0]);
        }
        return out.toString();
    }

    public String formatHistDateForDB() {

        if (this.getHistDateWord() == -1) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        if (era == -1) {
            out.append(1);
        } else {
            out.append(era);
        }
        String[][] words = HistoricalDate.getHistDateWords(Locale.ENGLISH);
        String[] word = words[this.getHistDateWord()];
        if (word[0].equals("A") || word[0].equals("-")) {
            out.append("9999");
        } else {
            int tIyear = 0;
            if (era == 0) {
                tIyear = 10000 - iyear;
            } else {
                tIyear = iyear;
            }
            if (tIyear < 10) {
                out.append("000");
            } else if (tIyear < 100) {
                out.append("00");
            } else if (tIyear < 1000) {
                out.append("0");
            }
            out.append(tIyear);
        }
        out.append(word[0]);
        return out.toString();
    }

    private boolean isNeedYear() {

        if (this.getHistDateWord() == -1) {
            return false;
        }
        String[][] words = HistoricalDate.getHistDateWords(Locale.ENGLISH);
        String[] word = words[this.getHistDateWord()];
        return !word[0].equals("A") && !word[0].equals("-");
    }
}
