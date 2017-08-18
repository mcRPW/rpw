package net.mightypork.rpw.utils.validation;

public class FilenameCharValidator extends CharValidatorRegex {

    public FilenameCharValidator() {
        super("[a-zA-Z0-9 +\\-.,_%@#$!'\"]");
    }

}
