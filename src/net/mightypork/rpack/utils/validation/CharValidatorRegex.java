package net.mightypork.rpack.utils.validation;


public class CharValidatorRegex implements CharValidator {

	private String formula;


	public CharValidatorRegex(String regex) {

		this.formula = regex;
	}


	@Override
	public boolean isValid(char c) {

		return Character.toString(c).matches(formula);
	}

}
