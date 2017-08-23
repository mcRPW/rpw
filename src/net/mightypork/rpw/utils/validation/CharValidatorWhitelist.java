package net.mightypork.rpw.utils.validation;

public class CharValidatorWhitelist implements CharValidator {

    private final String whitelist;


    public CharValidatorWhitelist(String allowed) {
        this.whitelist = allowed;
    }


    @Override
    public boolean isValid(char c) {
        return whitelist.contains(Character.toString(c));
    }

}
