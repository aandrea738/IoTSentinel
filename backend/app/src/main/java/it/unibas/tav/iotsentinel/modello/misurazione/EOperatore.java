package it.unibas.tav.iotsentinel.modello.misurazione;

public enum EOperatore {
    MAGGIORE(">"),
    MINORE("<"),
    UGUALE("="),
    MAGGIORE_UGUALE(">="),
    MINORE_UGUALE("<="),
    DIVERSO("!=");

    private final String simbolo;

    EOperatore(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public boolean valuta(double valore, double soglia) {
        return switch (this) {
            case MAGGIORE -> valore > soglia;
            case MINORE -> valore < soglia;
            case UGUALE -> Double.compare(valore, soglia) == 0;
            case MAGGIORE_UGUALE -> valore >= soglia;
            case MINORE_UGUALE -> valore <= soglia;
            case DIVERSO -> Double.compare(valore, soglia) != 0;
        };
    }
}
