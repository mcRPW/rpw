package net.mightypork.rpw.struct;

/**
 * Entry in objects index
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class FileObject {

    public String hash;
    public int size;


    public FileObject() {
    }


    @Override
    public String toString() {
        return "Obj[hash: " + hash + ", size: " + size + "]";
    }
}
