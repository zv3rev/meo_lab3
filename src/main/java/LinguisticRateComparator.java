import java.util.Comparator;

public class LinguisticRateComparator implements Comparator<Main.LinguisticRate> {

    @Override
    public int compare(Main.LinguisticRate o1, Main.LinguisticRate o2) {
        return o2.order - o1.order;
    }
}
