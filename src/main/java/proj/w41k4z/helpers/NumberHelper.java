package proj.w41k4z.helpers;

/**
 * The {@code NumberHelper} class is used to provide some useful methods for
 * number manipulation.
 */
public class NumberHelper {

    /**
     * Returns the currency format for a given number.
     * 
     * @param money the number to be formatted.
     * @return the currency format for a given number.
     */
    public static String toCurrency(Number money) {
        if (money == null || money.doubleValue() == 0) {
            return "0,00";
        }
        StringBuilder moneyString = new StringBuilder(String.format("%.2f", money).replace(".", ","));
        int start = moneyString.indexOf(",") != -1 ? moneyString.indexOf(",") - 1 : moneyString.length();
        for (int i = start, j = 1; i >= 0; i--, j++) {
            if (j % 3 == 0) {
                moneyString.insert(i, " ");
            }
        }
        return moneyString.toString();
    }
}
