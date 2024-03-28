public sealed interface Quantifiable {

    float calculate(float value);

    final class NotZero implements Quantifiable{
        @Override
        public float calculate(float value) {
            if (value == 0) return 0;
            return 1;
        }
    }

    final class OnlyZero implements Quantifiable{
        @Override
        public float calculate(float value) {
            if (value == 0) return 1;
            return 0;
        }
    }

    final class Middle implements  Quantifiable{
        @Override
        public float calculate(float value) {
            if (value<0.5f) return 0;
            if (value==0.5f) return 0.5f;
            return 1;
        }
    }

    final class Power implements Quantifiable{
        final int power;

        public Power(int power) {
            this.power = power;
        }

        @Override
        public float calculate(float value) {
            return (float) Math.pow(value, power);
        }
    }

}
