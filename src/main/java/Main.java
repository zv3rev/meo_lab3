import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.*;

public class Main {
    static ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    static InputStream is = classloader.getResourceAsStream("input.txt");
    static Scanner scanner = new Scanner(Objects.requireNonNull(is));
    static LinguisticRate[][] matrix;
    static List<String> shops;
    static List<String> criteria;
    static Float[] weights;
    static List<ShopAggregateRatePair> aggregateRates;

    enum LinguisticRate {
        VH("Very High", 5),
        H("High", 4),
        M("Medium", 3),
        L("Low", 2),
        VL("Very Low", 1);

        final String title;
        final int order;

        LinguisticRate(String title, int order) {
            this.title = title;
            this.order = order;
        }

        public static LinguisticRate getByOrder(int order) {
            for (LinguisticRate rate : LinguisticRate.values()) {
                if (rate.order == order) return rate;
            }
            throw new InvalidParameterException("Linguistic rate");
        }
    }

    public static void main(String[] args) {
        getDataFromFile();

        Quantifiable.Power quantification = new Quantifiable.Power(2);
        calculateWeights(quantification);
        System.out.println();
        System.out.printf("Quantification type: %s\r\n\r\n\r\n", quantification.getClass().getName());

        aggregateRates = new ArrayList<>();
        for (int i = 0; i < shops.size(); i++) {
            System.out.println("======================");
            System.out.printf("Calculating aggregate rate for %d shop\r\n", i + 1);
            System.out.println("======================");
            List<LinguisticRate> shopRates = Arrays.stream(matrix[i]).sorted(new LinguisticRateComparator()).toList();
            System.out.print("Sorted ratings: ");
            System.out.println(shopRates);

            List<WeightRatePair> lowa = new ArrayList<>();
            for (int j = 0; j < criteria.size(); j++) {
                lowa.add(new WeightRatePair(weights[j], shopRates.get(j)));
            }

            LinguisticRate rate = lowaOperator(lowa);
            aggregateRates.add(new ShopAggregateRatePair(i, shops.get(i), rate));
            System.out.printf("Aggregate rate: %s\r\n", rate);
            System.out.println("======================\r\n\r\n");
        }

        System.out.println("======================");
        aggregateRates.sort(new ShopAggregateRatePair.SARComparator());
        for (int i = 0; i < aggregateRates.size(); i++) {
            System.out.printf("#%d: %s (%s)\r\n", i+1, aggregateRates.get(i).shopName(), aggregateRates.get(i).aggregateRate());
        }
        System.out.println("======================");
    }

    private static LinguisticRate lowaOperator(List<WeightRatePair> lowa) {
        System.out.printf("Calculating LOWA for %s\r\n", lowa);
        if (lowa.size() == 2) {
            return lowaOperator(lowa.getFirst(), lowa.getLast());
        }

        WeightRatePair first = lowa.getFirst();
        Float reverseWeight = 1 - first.getWeight();
        for (int i = 1; i < lowa.size(); i++) {
            WeightRatePair wrp = lowa.get(i);
            wrp.setWeight(wrp.getWeight() / reverseWeight);
        }
        var result = getAggregateRate(
                first,
                new WeightRatePair(
                        reverseWeight,
                        lowaOperator(lowa.subList(1, lowa.size()))
                )
        );
        System.out.printf("LOWA result for (%f,%s): %s\r\n", first.getWeight(), first.getRate(), result);
        return result;
    }

    private static LinguisticRate lowaOperator(WeightRatePair wrp1, WeightRatePair wrp2) {
        var result = getAggregateRate(wrp1, wrp2);
        System.out.printf("LOWA result for %s and %s: %s\r\n", wrp1, wrp2, result);
        return result;
    }

    private static LinguisticRate getAggregateRate(WeightRatePair wrp1, WeightRatePair wrp2) {
        System.out.printf("k = min(%d, %d + round(%f * (%d - %d)))\r\n",
                LinguisticRate.values().length,
                wrp2.getRate().order,
                wrp1.getWeight(),
                wrp1.getRate().order,
                wrp2.getRate().order);
        var result = LinguisticRate.getByOrder(
                Math.min(
                        LinguisticRate.values().length,
                        wrp2.getRate().order + Math.round(wrp1.getWeight() * (wrp1.getRate().order - wrp2.getRate().order))));
        System.out.printf("Aggregation for %s and %s: %s\r\n", wrp1, wrp2, result);
        return result;
    }

    private static void calculateWeights(Quantifiable quantification) {
        weights = new Float[criteria.size()];
        for (int i = 1; i <= criteria.size(); i++) {
            calculateWeightForCriteria(quantification, i);
        }
    }

    private static void calculateWeightForCriteria(Quantifiable quantification, int i) {
        if (i == 1) {
            weights[i - 1] = quantification.calculate((float) 1 / criteria.size());
            System.out.printf("%d criteria weight: %f\r\n", i, weights[i - 1]);
        } else {
            weights[i - 1] = quantification.calculate((float) i / criteria.size()) -
                    quantification.calculate((float) (i - 1) / criteria.size());
            System.out.printf("%d criteria weight: %f\r\n", i, weights[i - 1]);
        }
    }

    private static void getDataFromFile() {
        String shopString = scanner.nextLine();
        shops = splitAndTrimString(shopString);
        System.out.printf("Shops list: %s\r\n\r\n", shops);

        String criteriaString = scanner.nextLine();
        criteria = splitAndTrimString(criteriaString);
        System.out.printf("Criteria list: %s\r\n\r\n", criteria);

        int shopsAmount = shops.size();
        int criteriaAmount = criteria.size();
        matrix = new LinguisticRate[shopsAmount][criteriaAmount];

        for (int i = 0; i < shopsAmount; i++) {
            final int[] j = {0};
            String rateLine = scanner.nextLine();
            int finalI = i;
            splitAndTrimString(rateLine)
                    .stream()
                    .limit(criteriaAmount)
                    .map(String::toUpperCase)
                    .map(LinguisticRate::valueOf)
                    .forEach(rate -> {
                        matrix[finalI][j[0]] = rate;
                        j[0]++;
                    });
        }
        System.out.println("Rating matrix:");
        for (int i = 0; i < shopsAmount; i++) {
            System.out.println(Arrays.deepToString(matrix[i]));
        }
        System.out.println();
    }

    private static List<String> splitAndTrimString(String s) {
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .toList();
    }
}
