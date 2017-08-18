package net.mightypork.rpw.utils.validation;

public class CharValidatorRegex implements CharValidator {

    private final String formula;


    public CharValidatorRegex(String regex) {
        this.formula = regex;
    }


    @Override
    public boolean isValid(char c) {
        return Character.toString(c).matches(formula);
    }

}
