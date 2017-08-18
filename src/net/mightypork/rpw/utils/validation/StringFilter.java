package net.mightypork.rpw.utils.validation;

/**
 * Utility interface for string filters (accepting filepaths and similar)
 *
 * @author Ondřej Hruška (MightyPork)
 */
public interface StringFilter {

    public boolean accept(String entry);
}
