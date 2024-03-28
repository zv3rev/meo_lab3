public class WeightRatePair {
    private Float weight;
    private Main.LinguisticRate rate;

    public WeightRatePair(Float weight, Main.LinguisticRate rate) {
        this.weight = weight;
        this.rate = rate;
    }

    public Float getWeight() {
        return weight;
    }

    public Main.LinguisticRate getRate() {
        return rate;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "[" +
                 weight +
                ", "  + rate +
                ']';
    }
}
