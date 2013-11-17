package net.mightypork.rpack.utils.validation;


public class CharValidatorWhitelist implements CharValidator {

	private String whitelist;


	public CharValidatorWhitelist(String allowed) {

		this.whitelist = allowed;
	}


	@Override
	public boolean isValid(char c) {

		return whitelist.contains(Character.toString(c));
	}

}
