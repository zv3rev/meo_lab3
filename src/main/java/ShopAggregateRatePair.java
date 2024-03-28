import java.util.Comparator;

public record ShopAggregateRatePair(int shopNumber, String shopName, Main.LinguisticRate aggregateRate) {
    public static class SARComparator implements Comparator<ShopAggregateRatePair>{
        @Override
        public int compare(ShopAggregateRatePair o1, ShopAggregateRatePair o2) {
            return o1.aggregateRate.order - o2.aggregateRate.order;
        }
    }
}
