package kgec.group11.engine;

import kgec.group11.exception.InvalidUnitException;

public final class ConverterFactory {
    private static final AbstractUnitConverter[] CONVERTERS = {
        new LengthConverter(),
        new TemperatureConverter(),
        new WeightConverter()
    };

    private ConverterFactory() {
    }

    public static AbstractUnitConverter[] getAllConverters() {
        return CONVERTERS.clone();
    }

    public static String[] getCategoryNames() {
        String[] names = new String[CONVERTERS.length];
        for (int i = 0; i < CONVERTERS.length; i++) {
            names[i] = CONVERTERS[i].getCategoryName();
        }
        return names;
    }

    public static AbstractUnitConverter getConverter(String categoryName) throws InvalidUnitException {
        if (categoryName != null) {
            for (AbstractUnitConverter converter : CONVERTERS) {
                if (converter.getCategoryName().equalsIgnoreCase(categoryName.trim())) {
                    return converter;
                }
            }
        }
        throw new InvalidUnitException("Unsupported conversion category: " + categoryName);
    }
}
